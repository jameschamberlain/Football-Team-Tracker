package com.jameschamberlain.footballteamtracker.fixtures;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jameschamberlain.footballteamtracker.FileUtils;
import com.jameschamberlain.footballteamtracker.Player;
import com.jameschamberlain.footballteamtracker.R;
import com.jameschamberlain.footballteamtracker.Team;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

public class EditFixtureFragment extends Fragment {

    private Fixture fixture;

    /**
     * A list of name of the team's players.
     */
    private ArrayList<String> playerNames = new ArrayList<>();

    private View rootView;

    private ArrayAdapter<String> adapter;

    private TextView scoreTextView;

    private int id;

    private EditRecyclerAdapter goalsAdapter;

    private EditRecyclerAdapter assistsAdapter;

    private TextView dateTextView;
    private TextView timeTextView;
    private final Calendar CALENDAR = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            CALENDAR.set(Calendar.YEAR, year);
            CALENDAR.set(Calendar.MONTH, monthOfYear);
            CALENDAR.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }

    };

    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            CALENDAR.set(Calendar.HOUR_OF_DAY, hourOfDay);
            CALENDAR.set(Calendar.MINUTE, minute);
            updateTimeLabel();
        }
    };

    EditFixtureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        this.fixture = Objects.requireNonNull(bundle).getParcelable("fixture");
        this.id = Team.getInstance().getFixtures().indexOf(this.fixture);


        rootView = inflater.inflate(R.layout.fragment_fixture_edit, container, false);

        for (Player player : Team.getInstance().getPlayers()) {
            playerNames.add(player.getName());
        }

        adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.item_player, playerNames);

        // Set the name of the home team.
        TextView homeTeamTextView = rootView.findViewById(R.id.home_team_text_view);
        homeTeamTextView.setText(fixture.getHomeTeam());

        // Set the score of the fixture.
        scoreTextView = rootView.findViewById(R.id.score_text_view);
        scoreTextView.setText(fixture.getScore().toString());

        // Set the name of the away team.
        TextView awayTextView = rootView.findViewById(R.id.away_team_text_view);
        awayTextView.setText(fixture.getAwayTeam());

        // Set the time.
        TextView timeTextView = rootView.findViewById(R.id.time_text_view);
        timeTextView.setText(fixture.getTimeString());

        setupScoreButton();
        setupDate();
        setupTime();
        setupGoals();
        setupAssists();
        setupDeleteButton();
        setupCancelButton();
        setupConfirmButton();

        // Inflate the layout for this fragment
        return rootView;
    }


    private void setupScoreButton() {
    /*
    Add a click listener for the score button so the score can
    be updated.
    An alert will appear with two spinners: one for the home team
    and one for the away team.
     */
        Button updateScoreButton = rootView.findViewById(R.id.update_score_button);
        updateScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Update score:");

                LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.dialog_score, null);

                final NumberPicker homeScorePicker = view.findViewById(R.id.homeScorePicker);
                homeScorePicker.setMinValue(0);
                homeScorePicker.setMaxValue(20);

                final NumberPicker awayScorePicker = view.findViewById(R.id.awayScorePicker);
                awayScorePicker.setMinValue(0);
                awayScorePicker.setMaxValue(20);

                if (fixture.getResult() == FixtureResult.UNPLAYED) {
                    homeScorePicker.setValue(0);
                    awayScorePicker.setValue(0);
                }
                else {
                    homeScorePicker.setValue(fixture.getScore().getHome());
                    awayScorePicker.setValue(fixture.getScore().getAway());
                }

                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int newHomeScore = homeScorePicker.getValue();
                        int newAwayScore = awayScorePicker.getValue();
                        fixture.setScore(new Score(newHomeScore, newAwayScore));
                        scoreTextView.setText(fixture.getScore().toString());
                    }
                });

                alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.setView(view);
                AlertDialog dialog = alert.create();
                dialog.show();
            }
        });
    }


    private void setupDate() {
        dateTextView = rootView.findViewById(R.id.date_text_view);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // COMPLETE
                new DatePickerDialog(getContext(), date,
                        fixture.getDateTime().getYear(),
                        (fixture.getDateTime().getMonthValue() - 1),
                        fixture.getDateTime().getDayOfMonth())
                        .show();
            }
        });
        CALENDAR.set(Calendar.YEAR, fixture.getDateTime().getYear());
        CALENDAR.set(Calendar.MONTH, (fixture.getDateTime().getMonthValue() - 1));
        CALENDAR.set(Calendar.DAY_OF_MONTH, fixture.getDateTime().getDayOfMonth());

        updateDateLabel();
    }

    private void updateDateLabel() {
        String myFormat = "E, d MMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        dateTextView.setText(sdf.format(CALENDAR.getTime()));
    }


    private void setupTime() {
        timeTextView = rootView.findViewById(R.id.time_text_view);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(), time,
                        fixture.getDateTime().getHour(),
                        fixture.getDateTime().getMinute(),
                        true).show();
            }
        });

        CALENDAR.set(Calendar.HOUR_OF_DAY, fixture.getDateTime().getHour());
        CALENDAR.set(Calendar.MINUTE, fixture.getDateTime().getMinute());

        updateTimeLabel();
    }

    private void updateTimeLabel() {
        String myFormat = "kk:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        timeTextView.setText(sdf.format(CALENDAR.getTime()));
    }


    private void setupGoals() {

        ArrayList<String> goalscorers = fixture.getGoalscorers();

        // Create an {@link TeamAdapter}, whose data source is a list of {@link Player}s. The
        // adapter knows how to create list items for each item in the list.
        goalsAdapter = new EditRecyclerAdapter(fixture, true);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list layout file.
        final RecyclerView recyclerView = rootView.findViewById(R.id.goals_list);


        // Make the {@link ListView} use the {@link TeamAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Fixture} in the list.
        recyclerView.setAdapter(goalsAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /*
        Add a click listener for the goalscorers button so the goalscorers
        can be updated.
        An alert will appear with a list of the team's players.
         */
        Button addGoalscorerButton = rootView.findViewById(R.id.add_goalscorer_button);
        addGoalscorerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Add a goalscorer:");

                alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = adapter.getItem(which);
                        ArrayList<String> newGoalscorers = fixture.getGoalscorers();
                        newGoalscorers.add(name);
                        fixture.setGoalscorers(newGoalscorers);
                        goalsAdapter.notifyDataSetChanged();
                    }
                });

                AlertDialog dialog = alert.create();
                dialog.show();
            }
        });
    }


    private void setupAssists() {

        ArrayList<String> assists = fixture.getAssists();

        // Create an {@link TeamAdapter}, whose data source is a list of {@link Player}s. The
        // adapter knows how to create list items for each item in the list.
        assistsAdapter = new EditRecyclerAdapter(fixture, false);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list layout file.
        final RecyclerView recyclerView = rootView.findViewById(R.id.assists_list);


        // Make the {@link ListView} use the {@link TeamAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Fixture} in the list.
        recyclerView.setAdapter(assistsAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        /*
        Add a click listener for the assists button so the assists
        can be updated.
        An alert will appear with a list of the team's players.
         */
        Button addAssistButton = rootView.findViewById(R.id.add_assist_button);
        addAssistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Add an assist:");

                alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = adapter.getItem(which);
                        //addNewView(assistsLayout, name);
                        ArrayList<String> newAssists = fixture.getAssists();
                        newAssists.add(name);
                        fixture.setAssists(newAssists);
                        assistsAdapter.notifyDataSetChanged();
                    }
                });

                AlertDialog dialog = alert.create();
                dialog.show();
            }
        });
    }


    private void setupDeleteButton() {
        Button deleteButton = rootView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Delete fixture?")
                        .setMessage("Are you sure you would like to delete this fixture?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ArrayList<Fixture> fixtures = Team.getInstance().getFixtures();
                                fixtures.remove(id);
                                // Sort fixtures.
                                Collections.sort(fixtures);
                                // Write the update to a file.
                                FileUtils.writeFixturesFile(fixtures);
                                FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                                if (fm.getBackStackEntryCount() > 0) {
                                    fm.popBackStackImmediate();
                                    if (fm.getBackStackEntryCount() > 0) {
                                        fm.popBackStackImmediate();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("No", null);
                AlertDialog dialog = alert.create();
                dialog.show();
            }
        });
    }


    private void setupConfirmButton() {
        Button confirmButton = rootView.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update fixture.
                Team team = Team.getInstance();
                Fixture originalFixture = team.getFixtures().get(id);
                // Set the score.
                originalFixture.setScore(fixture.getScore());
                // Get date and time
                int year = CALENDAR.get(Calendar.YEAR);
                int month = CALENDAR.get(Calendar.MONTH) + 1;
                int day = CALENDAR.get(Calendar.DAY_OF_MONTH);
                int hour = CALENDAR.get(Calendar.HOUR_OF_DAY);
                int minute = CALENDAR.get(Calendar.MINUTE);
                originalFixture.setDateTime(LocalDateTime.of(year, month, day, hour, minute));
                // Sort & set goalscorers.
                Collections.sort(fixture.getGoalscorers());
                originalFixture.setGoalscorers(fixture.getGoalscorers());
                // Sort & set assists.
                Collections.sort(fixture.getAssists());
                originalFixture.setAssists(fixture.getAssists());
                // Sort fixtures.
                Collections.sort(team.getFixtures());
                // Update the team's stats.
                team.updateTeamStats();
                // Update the player's stats.
                team.updatePlayerStats();
                // Write the update to a file.
                FileUtils.writeFixturesFile(team.getFixtures());
                // Return to the previous screen.
                FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
            }
        });
    }


    private void setupCancelButton() {
        Button cancelButton = rootView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStackImmediate();
                }
            }
        });
    }
}
