package com.jameschamberlain.footballteamtracker.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jameschamberlain.footballteamtracker.stats.StatsFragment
import com.jameschamberlain.footballteamtracker.stats.TabFragment

class TabAdapter(private val fragment: StatsFragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = TabFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt("tabIsGoals", position)
        }
        return fragment
    }

}