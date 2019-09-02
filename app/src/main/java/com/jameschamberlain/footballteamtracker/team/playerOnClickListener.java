package com.jameschamberlain.footballteamtracker.team;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.jameschamberlain.footballteamtracker.Player;
import com.jameschamberlain.footballteamtracker.R;
import com.jameschamberlain.footballteamtracker.Team;

import java.util.Objects;

public class playerOnClickListener implements View.OnClickListener {

    private Fragment parentFragment;
    private Player currentPlayer;

    public playerOnClickListener(Fragment parentFragment, Player currentPlayer) {
        this.parentFragment = parentFragment;
        this.currentPlayer = currentPlayer;
    }

    @Override
    public void onClick(View v) {
        loadFragment(new PlayerDetailsFragment(), currentPlayer);
    }

    /**
     *
     * Helper function for loading new fragments correctly.
     *
     * @param fragment The fragment to be loaded.
     */
    private void loadFragment(Fragment fragment, Player player) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("player", player);
        // set arguments
        fragment.setArguments(bundle);
        // load fragment
        FragmentTransaction transaction = Objects.requireNonNull(parentFragment.getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
