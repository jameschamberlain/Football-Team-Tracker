package com.jameschamberlain.footballteamtracker.stats;

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
import com.jameschamberlain.footballteamtracker.team.PlayerOnClickListener;

import java.util.ArrayList;
import java.util.Locale;

public class StatRecyclerAdapter extends RecyclerView.Adapter<StatRecyclerAdapter.ViewHolder> {

    private ArrayList<Player> players;
    private boolean isGoals;
    private Fragment parentFragment;

    StatRecyclerAdapter(ArrayList<Player> players, boolean isGoals, Fragment fragment) {
        this.players = players;
        this.isGoals = isGoals;
        this.parentFragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player currentPlayer = players.get(position);
        holder.rank.setText(String.format(Locale.ENGLISH, "%d", players.indexOf(currentPlayer) + 1));
        holder.name.setText(currentPlayer.getName());
        if (isGoals) {
            holder.value.setText(String.format(Locale.ENGLISH, "%d", currentPlayer.getGoals()));
        }
        else {
            holder.value.setText(String.format(Locale.ENGLISH, "%d", currentPlayer.getAssists()));
        }
        holder.parentLayout.setOnClickListener(new PlayerOnClickListener(parentFragment, currentPlayer));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView rank;
        TextView name;
        TextView value;
        RelativeLayout parentLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.rank_text_view);
            name = itemView.findViewById(R.id.name_text_view);
            value = itemView.findViewById(R.id.value_text_view);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

}
