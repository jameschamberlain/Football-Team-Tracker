package com.jameschamberlain.footballteamtracker.team;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jameschamberlain.footballteamtracker.FileUtils;
import com.jameschamberlain.footballteamtracker.Player;
import com.jameschamberlain.footballteamtracker.R;
import com.jameschamberlain.footballteamtracker.Team;
import com.jameschamberlain.footballteamtracker.fixtures.EditFixtureFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

public class PlayerDetailsFragment extends Fragment {

    private Player player;
    private View rootView;

    public PlayerDetailsFragment() {
        // Required empty constructor.
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle data = this.getArguments();
        player = Objects.requireNonNull(data).getParcelable("player");

        rootView = inflater.inflate(R.layout.fragment_player_details, container, false);

        setHasOptionsMenu(true);
        Toolbar myToolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");

        // Set the name of the player.
        TextView nameTextView = rootView.findViewById(R.id.name_text_view);
        nameTextView.setText(player.getName());

        // Set the number of goals the player has.
        TextView goalsTextView = rootView.findViewById(R.id.goals_text_view);
        goalsTextView.setText(String.format(Locale.ENGLISH, "%d", player.getGoals()));

        // Set the number of assists the player has.
        TextView assistsTextView = rootView.findViewById(R.id.assists_text_view);
        assistsTextView.setText(String.format(Locale.ENGLISH, "%d", player.getAssists()));

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.player_details_menu, menu);
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
            case R.id.action_delete:
                // User chose the "Delete" action, delete the fixture.
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Delete player?")
                        .setMessage("Are you sure you would like to delete this player?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ArrayList<Player> players = Team.getInstance().getPlayers();
                                players.remove(player);
                                // Sort fixtures.
                                Collections.sort(players);
                                // Write the update to a file.
                                FileUtils.writePlayersFile(players);
                                FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                                if (fm.getBackStackEntryCount() > 0) {
                                    fm.popBackStackImmediate();
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
