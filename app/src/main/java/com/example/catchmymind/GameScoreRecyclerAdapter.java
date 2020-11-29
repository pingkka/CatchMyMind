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

public class GameScoreRecyclerAdapter extends RecyclerView.Adapter<GameScoreRecyclerAdapter.ViewHolder> {
    private ArrayList<HashMap<String, String>> scores;

    public GameScoreRecyclerAdapter(ArrayList<HashMap<String, String>>items) {
        scores = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView score;

        ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tv_user_name);
            score = itemView.findViewById(R.id.tv_score);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.score_item, parent, false);
        GameScoreRecyclerAdapter.ViewHolder viewHolder = new GameScoreRecyclerAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.userName.setText(scores.get(position).get("user"));
        holder.score.setText(scores.get(position).get("score"));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }
}
