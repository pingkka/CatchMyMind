package com.example.catchmymind;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.catchmymind.databinding.FragmentGameBinding;
import com.example.catchmymind.databinding.LayoutChatBinding;
import com.example.catchmymind.databinding.LayoutChattingViewBinding;
import com.example.catchmymind.databinding.LayoutDrawBinding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameFragment extends Fragment implements SocketInterface {

    private FragmentGameBinding binding;
    private LayoutDrawBinding drawBinding;
    private LayoutChatBinding chatBinding;
    private LayoutChattingViewBinding chattingViewBinding;
    private ChattingRecyclerAdapter chattingRecyclerAdapter;

    private String userName;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String roomId;
    private String presenter;

    // int countdown = 90; // 카운트다운 시간 선언
    int countdown = 40; // 테스트 용 카운트다운 시간 선언
    Timer timer = null; // 타이머 선언
    TimerTask timerTask = null; // TimerTask 선언

    int gameStatus = 0; // 0 : 게임 시작 전, 1 : 게임 시작 후

    MyView myView;

    private static ArrayList<String> chatting = new ArrayList<>();

    public GameFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            MySocket mySocket = (MySocket) getArguments().getSerializable("obj");
            ois = mySocket.getOis();
            oos = mySocket.getOos();
            userName = mySocket.getUserName();

            roomId = getArguments().getString("roomId");
            presenter = getArguments().getString("presenter");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGameBinding.inflate(inflater, container, false);
        drawBinding = LayoutDrawBinding.bind(binding.getRoot());
        chatBinding = LayoutChatBinding.bind(binding.getRoot());
        chattingViewBinding = LayoutChattingViewBinding.bind(binding.getRoot());

        myView = new MyView(getContext());
        LinearLayout stage = binding.layoutPaintmap;
        stage.addView(myView);

        View view = binding.getRoot();

        chatBinding.etChat.setOnEditorActionListener(new TxtInputAction());
        chattingRecyclerAdapter = new ChattingRecyclerAdapter(chatting);
        chattingViewBinding.rvMessage.setAdapter(chattingRecyclerAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnLeave.setOnClickListener(v -> {
            GameExit();
        });

        binding.btnGameStart.setOnClickListener(v -> {
            binding.btnGameStart.setVisibility(View.INVISIBLE);
            buttonClickSetting();

            GameStart();
        });
    }

    public void buttonClickSetting() {
        drawBinding.btnPen.setOnClickListener(v -> {
            PenSettingDialogFragment penSettingDialog = PenSettingDialogFragment.getInstance(ois, oos, roomId);
            penSettingDialog.show(getParentFragmentManager(), "penSetting");
            penSettingDialog.setDialogResult((roomId, penColor, penSize) -> {
                Log.d("PenSetting : ", roomId);
                Log.d("PenSetting : ", penColor);
                Log.d("PenSetting : ", penSize);
                myView.setColor(penColor);
                myView.setStrokeWidth(Integer.parseInt(penSize));
            });
        });

        drawBinding.btnRemove.setOnClickListener(v -> {
            EraserSettingDialogFragment eraserSettingDialog = EraserSettingDialogFragment.getInstance(ois, oos, roomId);
            eraserSettingDialog.show(getParentFragmentManager(), "eraserSetting");
            eraserSettingDialog.setDialogResult((roomId, penColor, penSize) -> {
                Log.d("EraserSetting : ", roomId);
                Log.d("EraserSetting : ", penColor);
                Log.d("EraserSetting : ", penSize);
                myView.setStrokeWidth(Integer.parseInt(penSize));
                myView.setColor(penColor);
            });
        });

        drawBinding.btnAllClear.setOnClickListener(v -> {
            ClearCanvas();
        });

        drawBinding.btnEditwordItem.setOnClickListener(v -> {
            EditQuizDialogFragment editQuizDialogFragment = new EditQuizDialogFragment();
            editQuizDialogFragment.show(getParentFragmentManager(), "editQuiz");
            editQuizDialogFragment.setDialogResult((word) -> {
                Log.d("EraserSetting : ", word);
                EditItem(word);
            });

            Toast.makeText(requireContext(), "편집 아이템 사용", Toast.LENGTH_SHORT).show();
        });

        drawBinding.btnRanwordItem.setOnClickListener(v -> {
            RandomItem();
            Toast.makeText(requireContext(), "랜덤 아이템 사용", Toast.LENGTH_SHORT).show();
        });

        drawBinding.btnDrawBack.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "뒤로 가기", Toast.LENGTH_SHORT).show();
        });

        drawBinding.btnDrawFront.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "앞으로 가기", Toast.LENGTH_SHORT).show();
        });

        chatBinding.btnUserItem.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "아이템 지우기", Toast.LENGTH_SHORT).show();
        });
    }

    // keyboard 사라지게 한다.
    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    // Text 입력중 Enter key 처리
    class TxtInputAction implements EditText.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            String msg = chatBinding.etChat.getText().toString();
            if (msg.length() == 0)
                return false;
            hideKeyboard(); // 호출하면 Software 키가 내려간다.
            chatBinding.etChat.setText("");
            new Thread() {
                @Override
                public void run() {
                    Log.d("game:", "여기가");
                    ChatMsg cb = new ChatMsg(userName, "200", msg);
                    SendChatMsg(cb);
                    DoReceive(); // Server에서 읽는 Thread 실행
                }
            }.start();
            return false;
        }
    }

    public void startTimerThread() {
        timerTask = new TimerTask() { // timerTask는 timer가 일할 내용을 기록하는 객체
            @Override
            public void run() {
                decreaseBar(); // timer가 동작할 내용을 갖는 함수 호출
            }
        };
        timer = new Timer(); // timer 생성
        timer.schedule(timerTask, 0, 1000); // timerTask라는 일을 갖는 timer를 0초 딜레이로 1000ms마다 실행
    }

    public void decreaseBar() {
        //thread구동과 마찬가지로 Runnable을 써주고
        getActivity().runOnUiThread( //progressBar는 ui에 해당하므로 runOnUiThread로 컨트롤해야한다
                () -> { //run을 해준다. 그러나 일반 thread처럼 .start()를 해줄 필요는 없다
                    countdown = binding.progressBar.getProgress();
                    if (countdown > 0) {
                        countdown = countdown - 1;
                    } else if (countdown == 0) {
                        timer.cancel();
                        Thread.interrupted();  // Thread 강제 종료
                        GameScoreDialogFragment gameScoreDialog = GameScoreDialogFragment.getInstance();
                        gameScoreDialog.show(getParentFragmentManager(), "gameScore");
                        gameScoreDialog.setDialogResult((roomId) -> {
                            Log.d("gameScore : ", roomId);
                        });
                    }
                    binding.progressBar.setProgress(countdown);
                }
        );
    }

    public void GameExit() {
        ChatMsg cm = new ChatMsg();
        new Thread() {
            public void run() {
                cm.setCode("302");
                cm.setRoomId(roomId);
                SendChatMsg(cm);
                DoReceive(); // Server에서 읽는 Thread 실행
            }
        }.start();
    }

    public void GameSetting(String presenter) {
        if (presenter.equals(userName)) { // 출제자
            Log.d("game:", presenter + ":" + userName);
            binding.btnGameStart.setVisibility(View.VISIBLE);
            binding.layoutChattingView.getRoot().setVisibility(View.INVISIBLE);
            binding.layoutChat.getRoot().setVisibility(View.INVISIBLE);
            binding.layoutDraw.getRoot().setVisibility(View.VISIBLE);
        } else { // 유저
            Log.d("game:", presenter + ":" + userName);
            binding.btnGameStart.setVisibility(View.INVISIBLE);
            binding.layoutChattingView.getRoot().setVisibility(View.VISIBLE);
            binding.layoutDraw.getRoot().setVisibility(View.INVISIBLE);
            binding.layoutChat.getRoot().setVisibility(View.VISIBLE);
        }
    }

    public void GameStart() {
        ChatMsg cm = new ChatMsg();
        new Thread() {
            public void run() {
                cm.setCode("500");
                cm.setRoomId(roomId);
                SendChatMsg(cm);
                DoReceive(); // Server에서 읽는 Thread 실행
            }
        }.start();
    }

    public void ClearCanvas() {
        ChatMsg cm = new ChatMsg();
        new Thread() {
            public void run() {
                cm.setCode("603");
                cm.setRoomId(roomId);
                SendChatMsg(cm);
                DoReceive(); // Server에서 읽는 Thread 실행
            }
        }.start();
    }

    public void RandomItem() {
        ChatMsg cm = new ChatMsg();
        new Thread() {
            public void run() {
                cm.setCode("601");
                cm.setUserName(userName);
                cm.setRoomId(roomId);
                cm.setData("ramdom");
                SendChatMsg(cm);
                DoReceive(); // Server에서 읽는 Thread 실행
            }
        }.start();
    }

    public void EditItem(String quiz) {
        ChatMsg cm = new ChatMsg();
        new Thread() {
            public void run() {
                cm.setCode("601");
                cm.setUserName(userName);
                cm.setRoomId(roomId);
                cm.setData("edit");
                cm.setQuiz(quiz);
                SendChatMsg(cm);
                DoReceive(); // Server에서 읽는 Thread 실행
            }
        }.start();
    }

    // Server Message 수신
    @Override
    public synchronized void DoReceive() {
        new Thread() {
            public void run() {
                ReadChatMsg();
            }
        }.start();
    }

    // SendChatMsg()
    @Override
    public synchronized void SendChatMsg(ChatMsg cm) {
        new Thread() {
            public void run() {
                // Java 호환성을 위해 각각의 Field를 따로따로 보낸다.
                try {
                    if (cm.getCode().equals("302") || cm.getCode().equals("500")) { // 퇴장
                        Log.d("send exit game:", cm.getCode());
                        Log.d("send exit game:", cm.getRoomId());
                        oos.writeObject(cm.getCode());
                        oos.writeObject(cm.getRoomId());
                    } else if (cm.getCode().equals("601")) {
                        oos.writeObject(cm.getCode());
                        oos.writeObject(cm.getRoomId());
                        if (cm.getData().equals("edit")) {
                            oos.writeObject(cm.getQuiz());
                        }
                    } else if (cm.getCode().equals("603")) {
                        oos.writeObject(cm.getCode());
                        oos.writeObject(cm.getRoomId());
                    } else if (cm.getCode().equals("200")) { // 200
                        Log.d("game:", cm.getCode());
                        Log.d("game:", cm.getUserName());
                        Log.d("game:", cm.getData());
                        oos.writeObject(cm.getCode());
                        oos.writeObject(cm.getUserName());
                        oos.writeObject(cm.getData());
                    } else {
                        oos.writeObject(cm.getCode());
                        oos.writeObject(cm.getUserName());
                        oos.writeObject(cm.getData());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public synchronized ChatMsg ReadChatMsg() {
        ChatMsg cm = new ChatMsg("", "", "");
        try {
            cm.setCode((String) ois.readObject());
            if (cm.getCode().equals("302")) { // 방 퇴장 성공
                Bundle args = new Bundle();
                MySocket mySocket = new MySocket(userName, this.ois, this.oos);
                args.putSerializable("obj", mySocket);

                getActivity().runOnUiThread(() -> Navigation.findNavController(requireView()).navigate(R.id.action_gameFragment_to_gameRoomFragment, args));
            } else if (cm.getCode().equals("200")) {
                Log.d("game:", cm.getCode());
                cm.setUserName((String) ois.readObject());
                cm.setData((String) ois.readObject());

                Log.d("game:", cm.getData());
                chatting.add(cm.getData());
                chattingRecyclerAdapter.setChattings(chatting);
            } else if (cm.getCode().equals("500")) {
                Log.d("game:", cm.getCode());
                cm.setRoomId((String) ois.readObject());

                gameStatus = 1;

                // 프로그래스바
                binding.progressBar.setMax(countdown); // 프로그래스바 시간 최대값 설정
                binding.progressBar.setProgress(countdown); // 현재 프로그래스바 시간 설정

                startTimerThread();
            } else if (cm.getCode().equals("601")) {
                cm.setData((String) ois.readObject());
                cm.setQuiz((String) ois.readObject());
                Toast.makeText(requireContext(), cm.getQuiz(), Toast.LENGTH_SHORT).show();
            } else if (cm.getCode().equals("603")) {
                myView.clearCanvas();
            } else {
                cm.setUserName((String) ois.readObject());
                cm.setData((String) ois.readObject());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cm;
    }

}