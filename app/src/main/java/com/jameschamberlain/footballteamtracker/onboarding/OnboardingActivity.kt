package com.jameschamberlain.footballteamtracker.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jameschamberlain.footballteamtracker.*
import com.jameschamberlain.footballteamtracker.Utils.inTransaction

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        if (savedInstanceState == null) {
            supportFragmentManager.inTransaction {
                add(R.id.nav_host_fragment, OnboardingFragment())
            }
        }

    }
}