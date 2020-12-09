package com.example.catchmymind;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.catchmymind.databinding.GameScoreDialogBinding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GameScoreDialogFragment extends DialogFragment implements Serializable {
    private static ArrayList<HashMap<String, String>> score = new ArrayList<>();
    private GameScoreDialogBinding binding;
    GameScoreResult result;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String roomId;

    public GameScoreDialogFragment() {
    }

    public GameScoreDialogFragment(ArrayList<HashMap<String, String>> score, ObjectInputStream ois, ObjectOutputStream oos, String roomId) {
        this.score = score;
        this.ois = ois;
        this.oos = oos;
        this.roomId = roomId;
    }

    public static GameScoreDialogFragment getInstance(ObjectInputStream ois, ObjectOutputStream oos, String roomId) {
        GameScoreDialogFragment gameScoreDialogFragment = new GameScoreDialogFragment(score, ois, oos, roomId);
        return gameScoreDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = GameScoreDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.rvSocre.setLayoutManager(new LinearLayoutManager(requireContext()));
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user", "이수연");
        hashMap.put("score", "1");
        hashMap.put("user1", "민경진");
        hashMap.put("score1", "2");
        score.add(hashMap);
        GameScoreRecyclerAdapter gameScoreRecyclerAdapter = new GameScoreRecyclerAdapter(score);
        binding.rvSocre.setAdapter(gameScoreRecyclerAdapter);

        binding.btnExit.setOnClickListener(view12 -> {
            GameExit();
            dismiss();
        });

        return view;
    }

    public void setDialogResult(GameScoreResult dialogResult) {
        result = dialogResult;
    }

    public interface GameScoreResult {
        void finish(String roomId);
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.score_setting_width);
        int height = getResources().getDimensionPixelSize(R.dimen.score_setting_height);
        getDialog().getWindow().setLayout(width, height);
    }

    public synchronized void GameExit() {
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

    // Server Message 수신
    public synchronized void DoReceive() {
        new Thread() {
            public void run() {
                ReadChatMsg();
            }
        }.start();
    }

    // SendChatMsg() : 방이름, 인원수를 서버에게 전달
    public synchronized void SendChatMsg(ChatMsg cm) {
        new Thread() {
            public void run() {
                // Java 호환성을 위해 각각의 Field를 따로따로 보낸다.
                try {
                    oos.writeObject(cm.getCode());
                    oos.writeObject(cm.getUserName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // ChatMsg 를 읽어서 Return, Java 호환성 문제로 field별로 수신해서 ChatMsg 로 만들어 Return
    public synchronized ChatMsg ReadChatMsg() {
        ChatMsg cm = new ChatMsg();
        try {
            cm.setCode((String) ois.readObject());
            cm.setRoomId((String) ois.readObject());
            Log.d("ReadChatMsg : ", cm.getCode());
            Log.d("ReadChatMsg : ", cm.getUserName());
            if(cm.getCode().equals("302"))
                result.finish(cm.getCode());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cm;
    }


}