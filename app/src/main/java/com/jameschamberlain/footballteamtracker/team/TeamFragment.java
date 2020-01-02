package com.jameschamberlain.footballteamtracker.team;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jameschamberlain.footballteamtracker.FileUtils;
import com.jameschamberlain.footballteamtracker.Player;
import com.jameschamberlain.footballteamtracker.R;
import com.jameschamberlain.footballteamtracker.Team;
import com.jameschamberlain.footballteamtracker.fixtures.Fixture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeamFragment extends Fragment implements View.OnClickListener {

    private Team team = Team.getInstance();
    private final int NAME_MAX_LENGTH = 15;
    private View rootView;

    private TeamRecyclerAdapter adapter;

    public TeamFragment() {
        // Required empty public constructor.
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
        navView.getMenu().getItem(3).setChecked(true);

        navView.getMenu().getItem(0).setIcon(R.drawable.ic_home_outline);
        navView.getMenu().getItem(1).setIcon(R.drawable.ic_calendar_outline);
        navView.getMenu().getItem(2).setIcon(R.drawable.ic_analytics_outline);
        navView.getMenu().getItem(3).setIcon(R.drawable.ic_strategy);

        getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);

        FrameLayout containerLayout = getActivity().findViewById(R.id.container);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)containerLayout.getLayoutParams();
        float pixels =  56 * Objects.requireNonNull(getContext()).getResources().getDisplayMetrics().density;
        params.setMargins(0, 0, 0, (int) pixels);
        containerLayout.setLayoutParams(params);

        rootView = inflater.inflate(R.layout.fragment_team, container, false);

        setHasOptionsMenu(true);
        Toolbar myToolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");

        ArrayList<Player> players = team.getPlayers();

        // Create an {@link TeamRecyclerAdapter}, whose data source is a list of {@link Player}s. The
        // adapter knows how to create list items for each item in the list.
        adapter = new TeamRecyclerAdapter(players, TeamFragment.this);

        // Find the {@link RecyclerView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list layout file.
        RecyclerView recyclerView = rootView.findViewById(R.id.list);



        // Make the {@link RecyclerView} use the {@link TeamRecyclerAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Fixture} in the list.
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        checkTeamHasPlayers();


        // Inflate the layout for this fragment
        return rootView;
    }

    private void checkTeamHasPlayers() {
        LinearLayout noTeamLayout = rootView.findViewById(R.id.no_team_layout);
        if (team.getPlayers().isEmpty()) {
            noTeamLayout.setVisibility(View.VISIBLE);
        }
        else {
            noTeamLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        final EditText editText = new EditText(v.getContext());
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(NAME_MAX_LENGTH)});
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editText.setHint("Name");
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (20*scale + 0.5f);
        FrameLayout container = new FrameLayout(getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, 0);
        editText.setLayoutParams(lp);
        container.addView(editText);
        alert.setTitle("Add a player:")
                .setView(container)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = editText.getText().toString();
                        team.getPlayers().add(new Player(input));
                        Collections.sort(team.getPlayers());
                        FileUtils.writePlayersFile(team.getPlayers());
                        checkTeamHasPlayers();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", null);
        AlertDialog dialog = alert.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
