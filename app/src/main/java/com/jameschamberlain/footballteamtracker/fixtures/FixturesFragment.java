package com.jameschamberlain.footballteamtracker.fixtures;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jameschamberlain.footballteamtracker.R;
import com.jameschamberlain.footballteamtracker.Team;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class FixturesFragment extends Fragment {

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


}
