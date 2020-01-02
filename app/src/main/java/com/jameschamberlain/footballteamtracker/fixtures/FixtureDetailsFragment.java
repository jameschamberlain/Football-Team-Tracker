package com.jameschamberlain.footballteamtracker.fixtures;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jameschamberlain.footballteamtracker.FileUtils;
import com.jameschamberlain.footballteamtracker.Player;
import com.jameschamberlain.footballteamtracker.R;
import com.jameschamberlain.footballteamtracker.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class FixtureDetailsFragment extends Fragment {


    /**
     * The selected fixture.
     */
    private Fixture fixture;
    /**
     * The id of the selected fixture.
     */
    private int id;
    /**
     * The root view of the layout.
     */
    private View rootView;
    /**
     * A list of name of the team's players.
     */
    private ArrayList<String> playerNames = new ArrayList<>();
    /**
     * Adapter for the list of goalscorers.
     */
    private SimpleRecyclerAdapter goalsAdapter;
    /**
     * Adapter for the list of assists.
     */
    private SimpleRecyclerAdapter assistsAdapter;


    public FixtureDetailsFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().findViewById(R.id.nav_view).setVisibility(View.GONE);

        FrameLayout containerLayout = getActivity().findViewById(R.id.container);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)containerLayout.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        containerLayout.setLayoutParams(params);

        Bundle data = this.getArguments();
        this.fixture = Objects.requireNonNull(data).getParcelable("fixture");
        this.id = Team.getInstance().getFixtures().indexOf(this.fixture);

        rootView = inflater.inflate(R.layout.fragment_fixture_details, container, false);


        setHasOptionsMenu(true);
        Toolbar myToolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");


        for (Player player : Team.getInstance().getPlayers()) {
            playerNames.add(player.getName());
        }

        // Set the name of the home team.
        TextView homeTeamTextView = rootView.findViewById(R.id.home_team_text_view);
        homeTeamTextView.setText(fixture.getHomeTeam());

        // Set the score of the fixture.
        TextView scoreTextView = rootView.findViewById(R.id.score_text_view);
        scoreTextView.setText(fixture.getScore().toString());

        // Set the name of the away team.
        TextView awayTextView = rootView.findViewById(R.id.away_team_text_view);
        awayTextView.setText(fixture.getAwayTeam());

        // Set the date.
        TextView dateTextView = rootView.findViewById(R.id.date_text_view);
        dateTextView.setText(fixture.getExtendedDateString());

        // Set the time.
        TextView timeTextView = rootView.findViewById(R.id.time_text_view);
        timeTextView.setText(fixture.getTimeString());

        setupGoals();
        setupAssists();

        return rootView;
    }


    /**
     * Sets up the list of goalscorers.
     */
    private void setupGoals() {
        // Create an {@link TeamAdapter}, whose data source is a list of {@link Player}s. The
        // adapter knows how to create list items for each item in the list.
        goalsAdapter = new SimpleRecyclerAdapter(fixture.getGoalscorers(), true, getContext());

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list layout file.
        RecyclerView recyclerView = rootView.findViewById(R.id.goals_list);


        // Make the {@link ListView} use the {@link TeamAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Fixture} in the list.
        recyclerView.setAdapter(goalsAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /**
     * Sets up the list of assists.
     */
    private void setupAssists() {
        // Create an {@link SimpleRecyclerAdapter}, whose data source is a list of {@link Player}s. The
        // adapter knows how to create list items for each item in the list.
        assistsAdapter = new SimpleRecyclerAdapter(fixture.getAssists(), false, getContext());

        // Find the {@link RecyclerView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list layout file.
        RecyclerView recyclerView = rootView.findViewById(R.id.assists_list);


        // Make the {@link RecyclerView} use the {@link SimpleRecyclerAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Fixture} in the list.
        recyclerView.setAdapter(assistsAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    /**
     *
     * Helper function for loading new fragments correctly.
     *
     * @param fragment The fragment to be loaded.
     */
    private void loadFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("fixture", fixture.copyOf());
        // set arguments
        fragment.setArguments(bundle);
        // load fragment
        FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fixture_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // User chose the "Back" item, go back.
                FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
                return true;
            case R.id.action_edit:
                // User chose the "Edit" action, move to the edit page.
                loadFragment(new EditFixtureFragment());
                return true;
            case R.id.action_delete:
                // User chose the "Delete" action, delete the fixture.
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Delete fixture?")
                        .setMessage("Are you sure you would like to delete this fixture?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ArrayList<Fixture> fixtures = Team.getInstance().getFixtures();
                                fixtures.remove(id);
                                // Sort fixtures.
                                Collections.sort(fixtures);
                                // Update team stats.
                                Team.getInstance().updateTeamStats();
                                Team.getInstance().updatePlayerStats();
                                // Write the update to a file.
                                FileUtils.writeFixturesFile(fixtures);
                                FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                                if (fm.getBackStackEntryCount() > 0) {
                                    fm.popBackStack();
                                }
                            }
                        })
                        .setNegativeButton("No", null);
                AlertDialog dialog = alert.create();
                dialog.show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}
