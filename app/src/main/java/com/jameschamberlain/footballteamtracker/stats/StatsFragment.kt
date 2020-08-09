package com.jameschamberlain.footballteamtracker.stats

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.databinding.FragmentStatsBinding

/**
 * A simple [Fragment] subclass.
 */
class StatsFragment : Fragment() {

    private lateinit var binding: FragmentStatsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val navView: BottomNavigationView = activity!!.findViewById(R.id.bottom_nav)
        navView.menu.getItem(2).isChecked = true
        navView.menu.getItem(0).setIcon(R.drawable.ic_home_outline)
        navView.menu.getItem(1).setIcon(R.drawable.ic_calendar_outline)
        navView.menu.getItem(2).setIcon(R.drawable.ic_analytics)
        navView.menu.getItem(3).setIcon(R.drawable.ic_strategy_outline)
        activity!!.findViewById<View>(R.id.bottom_nav).visibility = View.VISIBLE
        val containerLayout = activity!!.findViewById<FrameLayout>(R.id.fragment_container)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        val pixels = 56 * context!!.resources.displayMetrics.density
        params.setMargins(0, 0, 0, pixels.toInt())
        containerLayout.layoutParams = params

        binding = FragmentStatsBinding.inflate(layoutInflater)

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""

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