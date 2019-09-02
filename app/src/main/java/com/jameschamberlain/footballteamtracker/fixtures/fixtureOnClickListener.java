package com.jameschamberlain.footballteamtracker.fixtures;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.jameschamberlain.footballteamtracker.R;
import com.jameschamberlain.footballteamtracker.Team;

import java.util.Objects;

public class fixtureOnClickListener implements View.OnClickListener {

    private static final int FIXTURE_REQUEST = 1;
    private Fragment parentFragment;
    private Fixture currentFixture;

    fixtureOnClickListener(Fragment parentFragment, int position) {
        this.parentFragment = parentFragment;
        currentFixture = Team.getInstance().getFixtures().get(position);
    }

    @Override
    public void onClick(View v) {
        loadFragment(new FixtureDetailsFragment(), currentFixture);
    }

    /**
     *
     * Helper function for loading new fragments correctly.
     *
     * @param fragment The fragment to be loaded.
     */
    private void loadFragment(Fragment fragment, Fixture fixture) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("fixture", fixture);
        // set arguments
        fragment.setArguments(bundle);
        //This is required to communicate between two fragments. Similar to startActivityForResult
        fragment.setTargetFragment(parentFragment, FIXTURE_REQUEST);
        // load fragment
        FragmentTransaction transaction = Objects.requireNonNull(parentFragment.getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
