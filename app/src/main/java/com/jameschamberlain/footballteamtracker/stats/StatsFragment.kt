package com.jameschamberlain.footballteamtracker.stats

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.jameschamberlain.footballteamtracker.MenuFragment
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentStatsBinding

/**
 * A simple [Fragment] subclass.
 */
class StatsFragment : MenuFragment() {

    private lateinit var binding: FragmentStatsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentStatsBinding.inflate(layoutInflater)

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        setHasOptionsMenu(true)

        binding.viewPager.adapter = TabAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.goals) else getString(R.string.assists)
        }.attach()


        // Inflate the layout for this fragment
        return binding.root
    }

    fun addNoStatsLayout() { binding.noStatsLayout.visibility = View.VISIBLE }

    fun removeNoStatsLayout() { binding.noStatsLayout.visibility = View.GONE }

}