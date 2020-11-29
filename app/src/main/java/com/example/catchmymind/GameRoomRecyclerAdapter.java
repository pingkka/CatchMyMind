package com.example.catchmymind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GameRoomRecyclerAdapter extends RecyclerView.Adapter<GameRoomRecyclerAdapter.ViewHolder> {
    private ArrayList<Room> rooms;

    public GameRoomRecyclerAdapter(ArrayList<Room> list){
        rooms = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView roomName;
        TextView roomNumofPeo;

        ViewHolder(View itemView){
            super(itemView);
            roomName = itemView.findViewById(R.id.tv_room_name);
            roomNumofPeo = itemView.findViewById(R.id.tv_room_num_of_peo);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.room_info_item, parent, false) ;
        GameRoomRecyclerAdapter.ViewHolder viewHolder = new GameRoomRecyclerAdapter.ViewHolder(view) ;

        return viewHolder ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.roomName.setText(rooms.get(position).roomName);
        holder.roomNumofPeo.setText(rooms.get(position).roomNumofPeo);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }
}
