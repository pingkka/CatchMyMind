package com.example.catchmymind;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class GameRoomRecyclerAdapter extends RecyclerView.Adapter<GameRoomRecyclerAdapter.ViewHolder> implements SocketInterface {
    private Context context;
    private ArrayList<Room> rooms;
    private View view;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String userName;
    private String code;


    public GameRoomRecyclerAdapter(ArrayList<Room> list, String userName, ObjectInputStream ois, ObjectOutputStream oos) {
        this.rooms = list;
        this.ois = ois;
        this.oos = oos;
        this.userName = userName;

    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView roomName;
        TextView maxNumofPeo;
        Button btnEnterRoom;

        ViewHolder(View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.tv_room_name);
            maxNumofPeo = itemView.findViewById(R.id.tv_room_max_num_of_peo);
            btnEnterRoom = itemView.findViewById(R.id.btn_enter_room);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        view = inflater.inflate(R.layout.room_info_item, parent, false);
        GameRoomRecyclerAdapter.ViewHolder viewHolder = new GameRoomRecyclerAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.roomName.setText(rooms.get(position).getRoomName());
        holder.maxNumofPeo.setText(rooms.get(position).getMaxNumofPeo());
        holder.btnEnterRoom.setOnClickListener(v -> {
            EnterRoom(rooms.get(position).getRoomId());
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public void EnterRoom(String roomId) {
        ChatMsg cm = new ChatMsg();
        new Thread() {
            public void run() {
                cm.setCode("301");
                cm.setRoomId(roomId);
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
                    oos.writeObject(cm.getCode());
                    oos.writeObject(cm.getRoomId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public synchronized ChatMsg ReadChatMsg() {
        ChatMsg cm = new ChatMsg();
        try {
            cm.setCode((String) ois.readObject());

            if(cm.getCode().equals("303")) { // 방 입장 성공
                // if enter 성공시 실행
                Log.d("gameRoom: ", cm.getCode());
                Bundle args = new Bundle();
                MySocket mySocket = new MySocket(userName, this.ois, this.oos);
                args.putSerializable("obj", mySocket);

                ((Activity)context).runOnUiThread(() -> Navigation.findNavController(view).navigate(R.id.action_gameRoomFragment_to_gameFragment, args));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cm;
    }
}
