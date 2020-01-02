package com.jameschamberlain.footballteamtracker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jameschamberlain.footballteamtracker.fixtures.Fixture;
import com.jameschamberlain.footballteamtracker.fixtures.FixtureDetailsFragment;
import com.jameschamberlain.footballteamtracker.fixtures.FixtureResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class HubFragment extends Fragment {

    private Team team = Team.getInstance();
    private View rootView;

    public HubFragment() {
        // Required empty public constructor.
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = fm.getBackStackEntryAt(0);
            fm.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
        navView.getMenu().getItem(0).setChecked(true);

        navView.getMenu().getItem(0).setIcon(R.drawable.ic_home);
        navView.getMenu().getItem(1).setIcon(R.drawable.ic_calendar_outline);
        navView.getMenu().getItem(2).setIcon(R.drawable.ic_analytics_outline);
        navView.getMenu().getItem(3).setIcon(R.drawable.ic_strategy_outline);

        getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);

        FrameLayout containerLayout = getActivity().findViewById(R.id.container);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)containerLayout.getLayoutParams();
        float pixels =  56 * Objects.requireNonNull(getContext()).getResources().getDisplayMetrics().density;
        params.setMargins(0, 0, 0, (int) pixels);
        containerLayout.setLayoutParams(params);

        rootView = inflater.inflate(R.layout.fragment_hub, container, false);

        setHasOptionsMenu(true);
        Toolbar myToolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");

        TextView teamNameTextView = rootView.findViewById(R.id.team_name_text_view);
        teamNameTextView.setText(team.getName());

        setupStatHighlights();

        if (team.getGamesPlayed() > 0) {
            setupForm();
            setupLatestResult();
        }

        if (!(team.getGamesPlayed() >= team.getFixtures().size())) {
            setupNextFixture();
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    private void setupStatHighlights() {
        Log.e("HubFragment", "We're setting progress!!!");
        TextView winsTextView = rootView.findViewById(R.id.win_text_view);
        winsTextView.setText(String.format(Locale.ENGLISH, "%d", team.getWins()));

        TextView lossesTextView = rootView.findViewById(R.id.lose_text_view);
        lossesTextView.setText(String.format(Locale.ENGLISH, "%d", team.getLosses()));

        TextView drawsTextView = rootView.findViewById(R.id.draw_text_view);
        drawsTextView.setText(String.format(Locale.ENGLISH, "%d", team.getDraws()));


        TextView goalsForTextView = rootView.findViewById(R.id.goals_for_text_view);
        goalsForTextView.setText(String.format(Locale.ENGLISH, "%d", team.getGoalsFor()));

        TextView goalsAgainstTextView = rootView.findViewById(R.id.goals_against_text_view);
        goalsAgainstTextView.setText(String.format(Locale.ENGLISH, "%d", team.getGoalsAgainst()));

        TextView goalDiffTextView = rootView.findViewById(R.id.goal_diff_text_view);
        goalDiffTextView.setText(String.format(Locale.ENGLISH, "%d", team.getGoalDifference()));


        Log.e("HubFragment", "W: " + team.getWins() + ", D: " + team.getDraws() + ", L:" + team.getLosses());
        final ProgressBar lossProgress = rootView.findViewById(R.id.progress_lose);
        double d = (double) team.getLosses() / (double) (team.getWins() + team.getDraws() + team.getLosses());
        final int progress = (int) (d * 100);
        lossProgress.post(new Runnable() {
            @Override
            public void run() {
                lossProgress.setProgress(progress);
            }
        });

        final ProgressBar drawProgress = rootView.findViewById(R.id.progress_draw);
        d = (double) team.getDraws() / (double) (team.getWins() + team.getDraws() + team.getLosses());
        final int progress2 = (int) (d * 100);
        drawProgress.post(new Runnable() {
            @Override
            public void run() {
                drawProgress.setProgress(progress2 + lossProgress.getProgress());
            }
        });

        Log.e("HubFragment", "Loss progress: " + lossProgress.getProgress());
    }

    private void setupNextFixture() {
        final Fixture fixture = team.getFixtures().get(team.getGamesPlayed());

        TextView dateTextView = rootView.findViewById(R.id.date_text_view);
        dateTextView.setText(fixture.getDateString());

        TextView timeTextView = rootView.findViewById(R.id.time_text_view);
        timeTextView.setText(fixture.getTimeString());

        TextView homeTeamTextView = rootView.findViewById(R.id.home_team_text_view);
        homeTeamTextView.setText(fixture.getHomeTeam());

        TextView awayTeamTextView = rootView.findViewById(R.id.away_team_text_view);
        awayTeamTextView.setText(fixture.getAwayTeam());

        LinearLayout fixtureLayout = rootView.findViewById(R.id.fixture_linear_layout);
        fixtureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("fixture", fixture);
                // set arguments
                Fragment fragment = new FixtureDetailsFragment();
                fragment.setArguments(bundle);
                // load fragment
                FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }


    private void setupLatestResult() {
        final Fixture fixture = team.getFixtures().get(team.getGamesPlayed()-1);

        TextView dateTextView = rootView.findViewById(R.id.result_date_text_view);
        dateTextView.setText(fixture.getDateString());

        TextView timeTextView = rootView.findViewById(R.id.result_time_text_view);
        timeTextView.setText(fixture.getTimeString());

        TextView homeTeamTextView = rootView.findViewById(R.id.result_home_team_text_view);
        homeTeamTextView.setText(fixture.getHomeTeam());

        TextView homeScoreTextView = rootView.findViewById(R.id.result_home_team_score_text_view);
        homeScoreTextView.setText(String.format(Locale.ENGLISH, "%d", fixture.getScore().getHome()));

        TextView awayScoreTextView = rootView.findViewById(R.id.result_away_team_score_text_view);
        awayScoreTextView.setText(String.format(Locale.ENGLISH, "%d", fixture.getScore().getAway()));

        TextView awayTeamTextView = rootView.findViewById(R.id.result_away_team_text_view);
        awayTeamTextView.setText(fixture.getAwayTeam());

        LinearLayout fixtureLayout = rootView.findViewById(R.id.result_linear_layout);
        fixtureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("fixture", fixture);
                // set arguments
                Fragment fragment = new FixtureDetailsFragment();
                fragment.setArguments(bundle);
                // load fragment
                FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private void setupForm() {
        setFormDrawable(
                (ImageView) rootView.findViewById(R.id.game5),
                team.getFixtures().get(team.getGamesPlayed() - 1).getResult());
        if (team.getGamesPlayed() > 1) {
            setFormDrawable(
                    (ImageView) rootView.findViewById(R.id.game4),
                    team.getFixtures().get(team.getGamesPlayed() - 2).getResult());
        }
        if (team.getGamesPlayed() > 2) {
            setFormDrawable(
                    (ImageView) rootView.findViewById(R.id.game3),
                    team.getFixtures().get(team.getGamesPlayed() - 3).getResult());
        }
        if (team.getGamesPlayed() > 3) {
            setFormDrawable(
                    (ImageView) rootView.findViewById(R.id.game2),
                    team.getFixtures().get(team.getGamesPlayed() - 4).getResult());
        }
        if (team.getGamesPlayed() > 4) {
            setFormDrawable(
                    (ImageView) rootView.findViewById(R.id.game1),
                    team.getFixtures().get(team.getGamesPlayed() - 5).getResult());
        }
    }

    private void setFormDrawable(ImageView view, FixtureResult result) {
        switch (result) {
            case WIN:
                view.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorWin));
                break;
            case LOSE:
                view.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorLoss));
                break;
            default:
                view.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorDraw));
                break;
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_rename_team) {
            // User chose the "Rename Team" action, show a window to allow this.
            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(getContext());
            final EditText editText = new EditText(getContext());
            editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            editText.setText(team.getName());
            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (20*scale + 0.5f);
            FrameLayout container = new FrameLayout(getContext());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, 0);
            editText.setLayoutParams(lp);
            container.addView(editText);
            alert.setTitle("Rename your team:")
                    .setView(container)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String input = editText.getText().toString();
                            for (Fixture fixture : team.getFixtures()) {
                                if (fixture.getHomeTeam().equals(team.getName())) {
                                    fixture.setHomeTeam(input);
                                }
                                else {
                                    fixture.setAwayTeam(input);
                                }
                            }
                            team.setName(input);
                            TextView teamNameTextView = rootView.findViewById(R.id.team_name_text_view);
                            teamNameTextView.setText(team.getName());
                            FileUtils.writeTeamFile(team.getName());
                        }
                    })
                    .setNegativeButton("Cancel", null);
            AlertDialog dialog = alert.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
