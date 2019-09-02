package com.jameschamberlain.footballteamtracker.stats;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.jameschamberlain.footballteamtracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatsFragment extends Fragment {

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

        ViewPager viewPager = rootView.findViewById(R.id.view_pager);

        // Create an adapter that knows which fragment should be shown on each page
        TabAdapter adapter = new TabAdapter(getContext(), getChildFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Find the tab layout that shows the tabs
        TabLayout tabLayout = rootView.findViewById(R.id.tabs);

        // Connect the tab layout with the view pager
        tabLayout.setupWithViewPager(viewPager);

        // Inflate the layout for this fragment
        return rootView;
    }

}
