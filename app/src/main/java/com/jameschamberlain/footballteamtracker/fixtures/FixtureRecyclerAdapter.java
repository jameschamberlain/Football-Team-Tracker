package com.jameschamberlain.footballteamtracker.fixtures;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.jameschamberlain.footballteamtracker.R;
import com.jameschamberlain.footballteamtracker.Team;

public class FixtureRecyclerAdapter extends RecyclerView.Adapter<FixtureRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private Fragment parentFragment;

    FixtureRecyclerAdapter(Context mContext, Fragment fragment) {
        this.mContext = mContext;
        this.parentFragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fixture, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fixture currentFixture = Team.getInstance().getFixtures().get(position);
        holder.dateTextView.setText(currentFixture.getDateString());
        holder.timeTextView.setText(currentFixture.getTimeString());
        setResult(holder.resultTextView, currentFixture);
        holder.homeTeamTextView.setText(currentFixture.getHomeTeam());
        holder.scoreTextView.setText(currentFixture.getScore().toString());
        holder.awayTeamTextView.setText(currentFixture.getAwayTeam());

        holder.parentLayout.setOnClickListener(new fixtureOnClickListener(parentFragment, position));
    }

    @Override
    public int getItemCount() {
        return Team.getInstance().getFixtures().size();
    }

    private void setResult(TextView resultTextView, Fixture currentFixture) {
        FixtureResult result = currentFixture.getResult();

        switch (result) {
            case WIN:
                resultTextView.setText("W");
                resultTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorWin));
                break;
            case LOSE:
                resultTextView.setText("L");
                resultTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorLoss));
                break;
            case DRAW:
                resultTextView.setText("D");
                resultTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorDraw));
                break;
            default:
                resultTextView.setText("");
                resultTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorDraw));
                break;
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView dateTextView;
        TextView timeTextView;
        TextView resultTextView;
        TextView homeTeamTextView;
        TextView scoreTextView;
        TextView awayTeamTextView;
        RelativeLayout parentLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            timeTextView = itemView.findViewById(R.id.time_text_view);
            resultTextView = itemView.findViewById(R.id.result_text_view);
            homeTeamTextView = itemView.findViewById(R.id.home_team_text_view);
            scoreTextView = itemView.findViewById(R.id.score_text_view);
            awayTeamTextView = itemView.findViewById(R.id.away_team_text_view);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }


}
