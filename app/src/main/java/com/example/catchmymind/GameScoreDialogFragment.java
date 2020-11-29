package com.example.catchmymind;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GameScoreDialogFragment extends DialogFragment implements Serializable {
    private static ArrayList<HashMap<String, String>> score;
    GameScoreResult result;

    private MySocket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public GameScoreDialogFragment(){}

    public GameScoreDialogFragment(ArrayList<HashMap<String, String>> score) {
        this.score = score;
    }

    public static GameScoreDialogFragment getInstance() {
        GameScoreDialogFragment gameScoreDialogFragment = new GameScoreDialogFragment(score);
        return gameScoreDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_score_dialog, container);
        Button btn_exit= (Button) view.findViewById(R.id.btn_exit);
        RecyclerView rv_socre = (RecyclerView) view.findViewById(R.id.rv_socre);

        rv_socre.setLayoutManager(new LinearLayoutManager(requireContext()));
        GameScoreRecyclerAdapter gameScoreRecyclerAdapter = new GameScoreRecyclerAdapter(score);
        rv_socre.setAdapter(gameScoreRecyclerAdapter);

        btn_exit.setOnClickListener(view12 -> {

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

    // 서버 통신, code, username, roomId
    public void ExitRoom() {
        new Thread() {
            public void run() {
                try {
                    socket = MySocket.getInstance();
                    oos = socket.getMyOos();
                    oos.flush();
                    ois = socket.getMyOis();
                    ChatMsg obj = new ChatMsg();
                    obj.setCode("300");
                    obj.setUserName(socket.getUserName());
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

    // SendChatMsg() : 방이름, 인원수를 서버에게 전달
    public void SendChatMsg(ChatMsg cm)  {
        new Thread() {
            public void run() {
                // Java 호환성을 위해 각각의 Field를 따로따로 보낸다.
                try {
                    oos.writeObject(cm.code);
                    oos.writeObject(cm.userName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // ChatMsg 를 읽어서 Return, Java 호환성 문제로 field별로 수신해서 ChatMsg 로 만들어 Return
    public ChatMsg ReadChatMsg()  {
        ChatMsg cm = new ChatMsg();
        try {
            // 여기가 문제
            cm.code = (String) ois.readObject();
            cm.roomId = (String) ois.readObject();
            Log.d("ReadChatMsg : ", cm.getCode());
            Log.d("ReadChatMsg : ", cm.getUserName());
            // cm.roomId 수신
            result.finish(cm.roomId);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cm;
    }

}