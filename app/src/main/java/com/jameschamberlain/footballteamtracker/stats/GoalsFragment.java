package com.jameschamberlain.footballteamtracker.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jameschamberlain.footballteamtracker.Player;
import com.jameschamberlain.footballteamtracker.R;
import com.jameschamberlain.footballteamtracker.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class GoalsFragment extends Fragment {


    private Team team = Team.getInstance();

    public GoalsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_stat_list, container, false);

        ArrayList<Player> players = new ArrayList<>(team.getPlayers());

        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return o2.getGoals() - o1.getGoals();
            }
        });


        // Create an {@link StatRecyclerAdapter}, whose data source is a list of {@link Stat}s. The
        // adapter knows how to create list items for each item in the list.
        StatRecyclerAdapter adapter = new StatRecyclerAdapter(players, true,  GoalsFragment.this);

        // Find the {@link RecyclerView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link RecyclerView} with the view ID called list, which is declared in the
        // word_list layout file.
        final RecyclerView recyclerView = rootView.findViewById(R.id.list);

        // Make the {@link RecyclerView} use the {@link TabAdapter} we created above, so that the
        // {@link RecyclerView} will display list items for each {@link Stat} in the list.
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        // Inflate the layout for this fragment
        return rootView;
    }
}
