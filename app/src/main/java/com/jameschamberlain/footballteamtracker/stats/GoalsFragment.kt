package com.jameschamberlain.footballteamtracker.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jameschamberlain.footballteamtracker.Player
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team.Companion.instance
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class GoalsFragment : Fragment() {
    private val team = instance
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_stat_list, container, false)
        val players = ArrayList(team.players)
        players.sortWith(Comparator { o1: Player, o2 -> o2.goals - o1.goals })


        // Create an {@link StatRecyclerAdapter}, whose data source is a list of {@link Stat}s. The
        // adapter knows how to create list items for each item in the list.
        val adapter = StatRecyclerAdapter(players, true, this@GoalsFragment)

        // Find the {@link RecyclerView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link RecyclerView} with the view ID called list, which is declared in the
        // word_list layout file.
        val recyclerView: RecyclerView = rootView.findViewById(R.id.list)

        // Make the {@link RecyclerView} use the {@link TabAdapter} we created above, so that the
        // {@link RecyclerView} will display list items for each {@link Stat} in the list.
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)


        // Inflate the layout for this fragment
        return rootView
    }
}