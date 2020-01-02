package com.jameschamberlain.footballteamtracker.stats;


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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.jameschamberlain.footballteamtracker.FileUtils;
import com.jameschamberlain.footballteamtracker.R;
import com.jameschamberlain.footballteamtracker.Team;
import com.jameschamberlain.footballteamtracker.fixtures.Fixture;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatsFragment extends Fragment {

    private Team team = Team.getInstance();

    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
        navView.getMenu().getItem(2).setChecked(true);

        navView.getMenu().getItem(0).setIcon(R.drawable.ic_home_outline);
        navView.getMenu().getItem(1).setIcon(R.drawable.ic_calendar_outline);
        navView.getMenu().getItem(2).setIcon(R.drawable.ic_analytics);
        navView.getMenu().getItem(3).setIcon(R.drawable.ic_strategy_outline);

        getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);

        FrameLayout containerLayout = getActivity().findViewById(R.id.container);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)containerLayout.getLayoutParams();
        float pixels =  56 * getContext().getResources().getDisplayMetrics().density;
        params.setMargins(0, 0, 0, (int) pixels);
        containerLayout.setLayoutParams(params);

        View rootView = inflater.inflate(R.layout.fragment_stats, container, false);

        setHasOptionsMenu(true);
        Toolbar myToolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");

        ViewPager viewPager = rootView.findViewById(R.id.view_pager);

        // Create an adapter that knows which fragment should be shown on each page
        TabAdapter adapter = new TabAdapter(getContext(), getChildFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Find the tab layout that shows the tabs
        TabLayout tabLayout = rootView.findViewById(R.id.tabs);

        // Connect the tab layout with the view pager
        tabLayout.setupWithViewPager(viewPager);

        LinearLayout noStatsLayout = rootView.findViewById(R.id.no_stats_layout);
        if (Team.getInstance().getPlayers().isEmpty()) {
            noStatsLayout.setVisibility(View.VISIBLE);
        }
        else {
            noStatsLayout.setVisibility(View.GONE);
        }

        // Inflate the layout for this fragment
        return rootView;
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
