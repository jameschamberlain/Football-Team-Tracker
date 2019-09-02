package com.jameschamberlain.footballteamtracker.team;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.jameschamberlain.footballteamtracker.Player;
import com.jameschamberlain.footballteamtracker.R;

import java.util.ArrayList;
import java.util.Locale;

public class TeamRecyclerAdapter extends RecyclerView.Adapter<TeamRecyclerAdapter.ViewHolder> {


    private ArrayList<Player> players;
    private Fragment parentFragment;

    TeamRecyclerAdapter(ArrayList<Player> players, Fragment fragment) {
        this.players = players;
        this.parentFragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player currentPlayer = players.get(position);
        holder.name.setText(currentPlayer.getName());
        holder.goals.setText(String.format(Locale.ENGLISH, "Goals: %d", currentPlayer.getGoals()));
        holder.assists.setText(String.format(Locale.ENGLISH, "Assists: %d", currentPlayer.getAssists()));
        holder.parentLayout.setOnClickListener(new playerOnClickListener(parentFragment, currentPlayer));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView goals;
        TextView assists;
        RelativeLayout parentLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_text_view);
            goals = itemView.findViewById(R.id.goals_text_view);
            assists = itemView.findViewById(R.id.assists_text_view);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }


}
