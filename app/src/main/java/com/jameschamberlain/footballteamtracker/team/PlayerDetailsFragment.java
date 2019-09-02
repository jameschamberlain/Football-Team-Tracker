package com.jameschamberlain.footballteamtracker.team;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jameschamberlain.footballteamtracker.FileUtils;
import com.jameschamberlain.footballteamtracker.Player;
import com.jameschamberlain.footballteamtracker.R;
import com.jameschamberlain.footballteamtracker.Team;

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

        getActivity().findViewById(R.id.nav_view).setVisibility(View.GONE);

        FrameLayout containerLayout = getActivity().findViewById(R.id.container);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)containerLayout.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        containerLayout.setLayoutParams(params);

        Bundle data = this.getArguments();
        player = Objects.requireNonNull(data).getParcelable("player");

        rootView = inflater.inflate(R.layout.fragment_player_details, container, false);

        // Set the name of the player.
        TextView nameTextView = rootView.findViewById(R.id.name_text_view);
        nameTextView.setText(player.getName());

        // Set the number of goals the player has.
        TextView goalsTextView = rootView.findViewById(R.id.goals_text_view);
        goalsTextView.setText(String.format(Locale.ENGLISH, "%d", player.getGoals()));

        // Set the number of assists the player has.
        TextView assistsTextView = rootView.findViewById(R.id.assists_text_view);
        assistsTextView.setText(String.format(Locale.ENGLISH, "%d", player.getAssists()));

        setupDeleteButton();

        // Inflate the layout for this fragment
        return rootView;
    }


    private void setupDeleteButton() {
        Button deleteButton = rootView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
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
            }
        });
    }

}
