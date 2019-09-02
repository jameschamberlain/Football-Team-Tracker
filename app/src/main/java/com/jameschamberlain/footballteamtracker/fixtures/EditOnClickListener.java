package com.jameschamberlain.footballteamtracker.fixtures;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class EditOnClickListener implements View.OnClickListener {

    private Fixture fixture;
    private boolean isGoals;
    private String player;
    private RecyclerView.Adapter adapter;

    EditOnClickListener(Fixture fixture, boolean isGoals, String player, RecyclerView.Adapter adapter) {
        this.fixture = fixture;
        this.isGoals = isGoals;
        this.player = player;
        this.adapter = adapter;
    }

    @Override
    public void onClick(View v) {
        if (isGoals) {
            fixture.getGoalscorers().remove(player);
        }
        else {
            fixture.getAssists().remove(player);
        }
        adapter.notifyDataSetChanged();
    }
}
