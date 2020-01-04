package com.jameschamberlain.footballteamtracker.fixtures;

import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jameschamberlain.footballteamtracker.FileUtils;
import com.jameschamberlain.footballteamtracker.R;
import com.jameschamberlain.footballteamtracker.Team;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class FixturesFragment extends Fragment {

    private Team team = Team.getInstance();

    public FixturesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Select correct bottom nav item.
        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
        navView.getMenu().getItem(1).setChecked(true);

        navView.getMenu().getItem(0).setIcon(R.drawable.ic_home_outline);
        navView.getMenu().getItem(1).setIcon(R.drawable.ic_calendar);
        navView.getMenu().getItem(2).setIcon(R.drawable.ic_analytics_outline);
        navView.getMenu().getItem(3).setIcon(R.drawable.ic_strategy_outline);


        getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);

        FrameLayout containerLayout = getActivity().findViewById(R.id.container);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)containerLayout.getLayoutParams();
        float pixels =  56 * Objects.requireNonNull(getContext()).getResources().getDisplayMetrics().density;
        params.setMargins(0, 0, 0, (int) pixels);
        containerLayout.setLayoutParams(params);

        View rootView = inflater.inflate(R.layout.fragment_fixtures, container, false);

        setHasOptionsMenu(true);
        Toolbar myToolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");


        // Create an {@link FixtureRecyclerAdapter}, whose data source is a list of {@link Fixture}s. The
        // adapter knows how to create list items for each item in the list.
        FixtureRecyclerAdapter adapter = new FixtureRecyclerAdapter(getActivity(), FixturesFragment.this);

        // Find the {@link RecyclerView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list layout file.
        final RecyclerView recyclerView = rootView.findViewById(R.id.list);

        // Make the {@link RecyclerView} use the {@link FixtureRecyclerAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Fixture} in the list.
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new NewFixtureFragment());
            }
        });

        //Scroll item 2 to 20 pixels from the top
        Team team = Team.getInstance();
        layoutManager.scrollToPositionWithOffset(team.getGamesPlayed()-3, 0);


        LinearLayout noFixturesLayout = rootView.findViewById(R.id.no_fixtures_layout);
        if (team.getFixtures().isEmpty()) {
            noFixturesLayout.setVisibility(View.VISIBLE);
        }
        else {
            noFixturesLayout.setVisibility(View.GONE);
        }


        // Inflate the layout for this fragment.
        return rootView;
    }

    /**
     *
     * Helper function for loading new fragments correctly.
     *
     * @param fragment The fragment to be loaded.
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
                            FileUtils.writeFixturesFile(team.getFixtures());
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
