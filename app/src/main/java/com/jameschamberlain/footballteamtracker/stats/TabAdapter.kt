package com.jameschamberlain.footballteamtracker.stats

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.jameschamberlain.footballteamtracker.R

class TabAdapter
/**
 * Create a new [TabAdapter] object.
 *
 * @param fm is the fragment manager that will keep each fragment's state in the adapter
 * across swipes.
 */ internal constructor(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val pageCount = 2
    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            GoalsFragment()
        } else {
            AssistsFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) {
            context.getString(R.string.goals)
        } else {
            context.getString(R.string.assists)
        }
    }

    /**
     * Return the total number of pages.
     */
    override fun getCount(): Int {
        return pageCount
    }

}