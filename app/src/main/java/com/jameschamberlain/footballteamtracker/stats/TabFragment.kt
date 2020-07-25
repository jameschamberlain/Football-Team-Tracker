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
import com.jameschamberlain.footballteamtracker.Team
import com.jameschamberlain.footballteamtracker.Team.Companion.team
import com.jameschamberlain.footballteamtracker.databinding.FragmentStatListBinding
import java.util.*

class TabFragment(private val isGoals: Boolean) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentStatListBinding = FragmentStatListBinding.inflate(layoutInflater)

        val players = ArrayList(team.players)

        val adapter: StatRecyclerAdapter = if (isGoals) {
            players.sortWith(Comparator { o1: Player, o2 -> o2.goals - o1.goals })
            StatRecyclerAdapter(players, true, this@TabFragment)
        }
        else {
            players.sortWith(Comparator { o1: Player, o2 -> o2.assists - o1.assists })
            StatRecyclerAdapter(players, false, this@TabFragment)
        }

        binding.playersRecyclerView.adapter = adapter
        binding.playersRecyclerView.layoutManager = LinearLayoutManager(activity)


        // Inflate the layout for this fragment
        return binding.root

    }
}