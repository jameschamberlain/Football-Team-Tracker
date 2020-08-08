package com.jameschamberlain.footballteamtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jameschamberlain.footballteamtracker.fixtures.FixturesFragment
import com.jameschamberlain.footballteamtracker.hub.HubFragment
import com.jameschamberlain.footballteamtracker.stats.StatsFragment
import com.jameschamberlain.footballteamtracker.team.TeamFragment

class MainActivity : AppCompatActivity() {
    private lateinit var navView: BottomNavigationView
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_hub -> {
                loadFragment(HubFragment())
                navView.menu.getItem(0).setIcon(R.drawable.ic_home)
                navView.menu.getItem(1).setIcon(R.drawable.ic_calendar_outline)
                navView.menu.getItem(2).setIcon(R.drawable.ic_analytics_outline)
                navView.menu.getItem(3).setIcon(R.drawable.ic_strategy_outline)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_fixtures -> {
                loadFragment(FixturesFragment())
                navView.menu.getItem(0).setIcon(R.drawable.ic_home_outline)
                navView.menu.getItem(1).setIcon(R.drawable.ic_calendar)
                navView.menu.getItem(2).setIcon(R.drawable.ic_analytics_outline)
                navView.menu.getItem(3).setIcon(R.drawable.ic_strategy_outline)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_stats -> {
                loadFragment(StatsFragment())
                navView.menu.getItem(0).setIcon(R.drawable.ic_home_outline)
                navView.menu.getItem(1).setIcon(R.drawable.ic_calendar_outline)
                navView.menu.getItem(2).setIcon(R.drawable.ic_analytics)
                navView.menu.getItem(3).setIcon(R.drawable.ic_strategy_outline)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_team -> {
                loadFragment(TeamFragment())
                navView.menu.getItem(0).setIcon(R.drawable.ic_home_outline)
                navView.menu.getItem(1).setIcon(R.drawable.ic_calendar_outline)
                navView.menu.getItem(2).setIcon(R.drawable.ic_analytics_outline)
                navView.menu.getItem(3).setIcon(R.drawable.ic_strategy)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navView.menu.getItem(0).setIcon(R.drawable.ic_home)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, HubFragment())
        transaction.commit()
    }
}