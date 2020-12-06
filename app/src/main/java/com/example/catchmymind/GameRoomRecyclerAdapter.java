package com.example.catchmymind;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class GameRoomRecyclerAdapter extends RecyclerView.Adapter<GameRoomRecyclerAdapter.ViewHolder> implements SocketInterface {
    private ArrayList<Room> rooms;
    private View view;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String userName;


    public GameRoomRecyclerAdapter(ArrayList<Room> list, String userName, ObjectInputStream ois, ObjectOutputStream oos) {
        this.rooms = list;
        this.ois = ois;
        this.oos = oos;
        this.userName = userName;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView roomName;
        TextView roomNumofPeo;
        Button btnEnterRoom;

        ViewHolder(View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.tv_room_name);
            roomNumofPeo = itemView.findViewById(R.id.tv_room_num_of_peo);
            btnEnterRoom = itemView.findViewById(R.id.btn_enter_room);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.room_info_item, parent, false);
        GameRoomRecyclerAdapter.ViewHolder viewHolder = new GameRoomRecyclerAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.roomName.setText(rooms.get(position).getRoomName());
        holder.roomNumofPeo.setText(rooms.get(position).getRoomNumofPeo());
        holder.btnEnterRoom.setOnClickListener(v -> {
            EnterRoom();

            // if enter 성공시 실행
            Bundle args = new Bundle();
            MySocket mySocket = new MySocket(userName, this.ois, this.oos);
            args.putSerializable("obj", mySocket);
            Navigation.findNavController(view).navigate(R.id.action_gameRoomFragment_to_gameFragment, args);
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public void EnterRoom() {
        new Thread() {
            public void run() {
                ChatMsg obj = new ChatMsg(userName, "301", "enter");
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

    @Override
    public synchronized ChatMsg ReadChatMsg() {
        ChatMsg cm = new ChatMsg("", "", "");
        try {
            cm.setCode((String) ois.readObject());
            cm.setUserName((String) ois.readObject());
            cm.setData((String) ois.readObject());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cm;
    }
}
