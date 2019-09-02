package com.jameschamberlain.footballteamtracker.fixtures;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jameschamberlain.footballteamtracker.R;

import java.util.ArrayList;

public class EditRecyclerAdapter extends RecyclerView.Adapter<EditRecyclerAdapter.ViewHolder> {

    private Fixture fixture;
    private ArrayList<String> players;
    private boolean isGoals;

    EditRecyclerAdapter(Fixture fixture, boolean isGoals) {
        this.fixture = fixture;
        if (isGoals) {
            this.players = fixture.getGoalscorers();
        }
        else {
            this.players = fixture.getAssists();
        }
        this.isGoals = isGoals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_edit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String player = players.get(position);
        holder.name.setText(player);
        holder.imageView.setOnClickListener(new EditOnClickListener(fixture, isGoals, player, this));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.clear_image_view);
            name = itemView.findViewById(R.id.name_text_view);
        }
    }
}
