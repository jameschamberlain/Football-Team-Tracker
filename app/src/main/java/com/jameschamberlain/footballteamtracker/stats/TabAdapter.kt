package com.jameschamberlain.footballteamtracker.stats

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabAdapter(private val fragment: StatsFragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {TabFragment(true, fragment)}
            else -> {TabFragment(false, fragment)}
        }
    }

}