package com.jameschamberlain.footballteamtracker;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jameschamberlain.footballteamtracker.fixtures.FixturesFragment;
import com.jameschamberlain.footballteamtracker.stats.StatsFragment;
import com.jameschamberlain.footballteamtracker.team.TeamFragment;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    BottomNavigationView navView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_hub:
                    loadFragment(new HubFragment());
                    navView.getMenu().getItem(0).setIcon(R.drawable.ic_home);
                    navView.getMenu().getItem(1).setIcon(R.drawable.ic_calendar_outline);
                    navView.getMenu().getItem(2).setIcon(R.drawable.ic_analytics_outline);
                    navView.getMenu().getItem(3).setIcon(R.drawable.ic_strategy_outline);
                    return true;
                case R.id.navigation_fixtures:
                    loadFragment(new FixturesFragment());
                    navView.getMenu().getItem(0).setIcon(R.drawable.ic_home_outline);
                    navView.getMenu().getItem(1).setIcon(R.drawable.ic_calendar);
                    navView.getMenu().getItem(2).setIcon(R.drawable.ic_analytics_outline);
                    navView.getMenu().getItem(3).setIcon(R.drawable.ic_strategy_outline);
                    return true;
                case R.id.navigation_stats:
                    loadFragment(new StatsFragment());
                    navView.getMenu().getItem(0).setIcon(R.drawable.ic_home_outline);
                    navView.getMenu().getItem(1).setIcon(R.drawable.ic_calendar_outline);
                    navView.getMenu().getItem(2).setIcon(R.drawable.ic_analytics);
                    navView.getMenu().getItem(3).setIcon(R.drawable.ic_strategy_outline);
                    return true;
                case R.id.navigation_team:
                    loadFragment(new TeamFragment());
                    navView.getMenu().getItem(0).setIcon(R.drawable.ic_home_outline);
                    navView.getMenu().getItem(1).setIcon(R.drawable.ic_calendar_outline);
                    navView.getMenu().getItem(2).setIcon(R.drawable.ic_analytics_outline);
                    navView.getMenu().getItem(3).setIcon(R.drawable.ic_strategy);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navView.getMenu().getItem(0).setIcon(R.drawable.ic_home);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new HubFragment());
        transaction.commit();
    }
}
