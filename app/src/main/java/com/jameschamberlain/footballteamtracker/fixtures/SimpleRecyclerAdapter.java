package com.jameschamberlain.footballteamtracker.fixtures;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jameschamberlain.footballteamtracker.R;

import java.util.ArrayList;
import java.util.Collections;

public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder> {

    private ArrayList<String> players;
    private ArrayList<String> uniquePlayers = new ArrayList<>();
    private boolean isGoals;
    Context context;

    SimpleRecyclerAdapter(ArrayList<String> players, boolean isGoals, Context context) {
        this.players = players;
        for (String player : players) {
            if (!(uniquePlayers.contains(player))) {
                uniquePlayers.add(player);
            }
        }
        this.isGoals = isGoals;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_stat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String player = uniquePlayers.get(position);
        holder.name.setText(player);
        if (isGoals) {
            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_football));
        }
        else {

            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_shoe));
        }
        holder.numOfGoals.setText("x" + Collections.frequency(players, player));
    }

    @Override
    public int getItemCount() {
        return uniquePlayers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView icon;
        TextView numOfGoals;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_text_view);
            icon = itemView.findViewById(R.id.icon);
            numOfGoals = itemView.findViewById(R.id.num_of_goals_text_view);
        }
    }
}
