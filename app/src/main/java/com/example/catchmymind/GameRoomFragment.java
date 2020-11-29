package com.example.catchmymind;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.catchmymind.databinding.FragmentGameRoomBinding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class GameRoomFragment extends Fragment implements Serializable {
    private ArrayList<Room> roomList = new ArrayList<>();
    private FragmentGameRoomBinding binding;

    private MySocket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public GameRoomFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        for (int i = 0; i < 8; i++) {
//            roomList.add(String.format("Room %d", i + 1));
//        }
        int num[] = {2, 4, 8, 4};
        for (int i = 0; i < 4; i++) {
            roomList.add(new Room(String.format("Room %d", i + 1), Integer.toString(num[i]), Integer.toString(i)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGameRoomBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvGameRoom.setLayoutManager(new LinearLayoutManager(requireContext()));
        GameRoomRecyclerAdapter gameRoomRecyclerAdapter = new GameRoomRecyclerAdapter(roomList);
        binding.rvGameRoom.setAdapter(gameRoomRecyclerAdapter);

        binding.btnBack.setOnClickListener(v -> {
            Logout();
        });

        binding.btnCreateRoom.setOnClickListener(view1 -> {
            RoomSettingDialogFragment roomSettingDialogFragment = RoomSettingDialogFragment.getInstance();

            roomSettingDialogFragment.show(getParentFragmentManager(), "Create Room");
            roomSettingDialogFragment.setDialogResult((roomName, roomNumofPeo, roomId) -> {
                Room room = new Room(roomName, roomNumofPeo, roomId);
                Log.d("GameRoom6 : ", room.getRoomName());
                Log.d("GameRoom6:", room.getRoomNumofPeo());
                Log.d("GameRoom6:", room.getRoomId());
                roomList.add(room);
                gameRoomRecyclerAdapter.notifyDataSetChanged();
            });
        });

    }

    public void Logout() {
        new Thread() {
            public void run() {
                try {
                    socket = MySocket.getInstance();
                    oos = socket.getMyOos();
                    oos.flush();
                    ois = socket.getMyOis();
                    ChatMsg obj = new ChatMsg(socket.getUserName(),"400", "bye");
                    SendChatMsg(obj);
                    DoReceive(); // Server에서 읽는 Thread 실행
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // Server Message 수신
    public void DoReceive() {
        new Thread() {
            public void run() {
                ReadChatMsg();
            }
        }.start();
    }

    // SendChatMsg()
    public void SendChatMsg(ChatMsg cm)  {
        new Thread() {
            public void run() {
                // Java 호환성을 위해 각각의 Field를 따로따로 보낸다.
                try {
                    oos.writeObject(cm.getCode());
                    oos.writeObject(cm.getUserName());
                    oos.writeObject(cm.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // ChatMsg 를 읽어서 Return, Java 호환성 문제로 field별로 수신해서 ChatMsg 로 만들어 Return
    public ChatMsg ReadChatMsg()  {
        ChatMsg cm = new ChatMsg("","","");
        try {
            // 여기가 문제
            cm.setCode((String) ois.readObject());
            cm.setUserName((String) ois.readObject());
            cm.setData((String) ois.readObject());

            if(cm.getCode().equals("400")) {
                getActivity().runOnUiThread(() -> Navigation.findNavController(requireView()).navigate(R.id.action_gameRoomFragment_to_appStartFragment));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cm;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}