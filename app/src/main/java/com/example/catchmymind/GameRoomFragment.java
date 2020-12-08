package com.example.catchmymind;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.catchmymind.databinding.FragmentGameRoomBinding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

public class GameRoomFragment extends Fragment implements Serializable, SocketInterface {
    private ArrayList<Room> roomList = new ArrayList<>();
    private FragmentGameRoomBinding binding;

    private String userName;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public GameRoomFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            MySocket mySocket = (MySocket) getArguments().getSerializable("obj");
            ois = mySocket.getOis();
            oos = mySocket.getOos();
            userName = mySocket.getUserName();
        }

        int num[] = {2, 4, 8, 4};
        int r_id = 1;
        for (int i = 0; i < 4; i++) {
            roomList.add(new Room("경진", String.format("Room %d", i + 1), num[i]+"명", Integer.toString(r_id++)));
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
        GameRoomRecyclerAdapter gameRoomRecyclerAdapter = new GameRoomRecyclerAdapter(roomList, userName, ois, oos);
        binding.rvGameRoom.setAdapter(gameRoomRecyclerAdapter);

        binding.btnBack.setOnClickListener(v -> {
            Logout();
        });

        binding.btnCreateRoom.setOnClickListener(view1 -> {
            RoomSettingDialogFragment roomSettingDialogFragment = RoomSettingDialogFragment.getInstance(userName, ois, oos);
            roomSettingDialogFragment.show(getParentFragmentManager(), "Create Room");
            roomSettingDialogFragment.setDialogResult((roomName, maxNumofPeo, roomId) -> {
                Room room = new Room(userName, roomName, maxNumofPeo, roomId);
                roomList.add(room);
                gameRoomRecyclerAdapter.setRooms(roomList);
//                gameRoomRecyclerAdapter.notifyDataSetChanged();
            });
        });

    }

    public void Logout() {
        new Thread() {
            public void run() {
                ChatMsg obj = new ChatMsg(userName, "400", "bye");
                SendChatMsg(obj);
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
    @Override
    public synchronized ChatMsg ReadChatMsg() {
        ChatMsg cm = new ChatMsg("", "", "");
        try {
            cm.setUserName((String) ois.readObject());
            cm.setCode((String) ois.readObject());
            cm.setData((String) ois.readObject());

            if (cm.getCode().equals("400")) {
                Log.d("gameRoom:", "들어왔니..?"); // 실행 안됨.
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