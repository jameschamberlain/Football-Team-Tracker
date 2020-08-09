package com.jameschamberlain.footballteamtracker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jameschamberlain.footballteamtracker.fixtures.FixturesFragment
import com.jameschamberlain.footballteamtracker.hub.HubFragment
import com.jameschamberlain.footballteamtracker.stats.StatsFragment
import com.jameschamberlain.footballteamtracker.team.TeamFragment

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {


    private lateinit var navView: BottomNavigationView


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.hub -> {
                loadFragment(HubFragment())
                navView.menu.getItem(0).setIcon(R.drawable.ic_home)
                navView.menu.getItem(1).setIcon(R.drawable.ic_calendar_outline)
                navView.menu.getItem(2).setIcon(R.drawable.ic_analytics_outline)
                navView.menu.getItem(3).setIcon(R.drawable.ic_strategy_outline)
                return@OnNavigationItemSelectedListener true
            }
            R.id.fixtures -> {
                loadFragment(FixturesFragment())

                navView.menu.getItem(0).setIcon(R.drawable.ic_home_outline)
                navView.menu.getItem(1).setIcon(R.drawable.ic_calendar)
                navView.menu.getItem(2).setIcon(R.drawable.ic_analytics_outline)
                navView.menu.getItem(3).setIcon(R.drawable.ic_strategy_outline)
                return@OnNavigationItemSelectedListener true
            }
            R.id.stats -> {
                loadFragment(StatsFragment())

                navView.menu.getItem(0).setIcon(R.drawable.ic_home_outline)
                navView.menu.getItem(1).setIcon(R.drawable.ic_calendar_outline)
                navView.menu.getItem(2).setIcon(R.drawable.ic_analytics)
                navView.menu.getItem(3).setIcon(R.drawable.ic_strategy_outline)
                item.isChecked = true
                return@OnNavigationItemSelectedListener true
            }
            R.id.team -> {
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
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navView = findViewById(R.id.bottom_nav)

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navView.menu.getItem(0).setIcon(R.drawable.ic_home)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, HubFragment())
        transaction.commit()
    }
}