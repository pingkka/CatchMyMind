package com.example.catchmymind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class ChattingRecyclerAdapter extends RecyclerView.Adapter<ChattingRecyclerAdapter.ViewHolder> {
    private ArrayList<String> chattings;

    public ChattingRecyclerAdapter(ArrayList<String>items) {
        chattings = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView chat;

        ViewHolder(View itemView) {
            super(itemView);
            chat = itemView.findViewById(R.id.tv_chat);
        }
    }

    public void setChattings(ArrayList<String> items) {
        chattings = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.chatting_item, parent, false);
        ChattingRecyclerAdapter.ViewHolder viewHolder = new ChattingRecyclerAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.chat.setText(chattings.get(position));
    }

    @Override
    public int getItemCount() {
        return chattings.size();
    }
}
