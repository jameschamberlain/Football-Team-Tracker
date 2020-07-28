package com.jameschamberlain.footballteamtracker.setup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OneShotPreDrawListener.add
import androidx.fragment.app.FragmentActivity
import com.jameschamberlain.footballteamtracker.*

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        if (savedInstanceState != null) return

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, SetupFragment())
            .commit()

    }
}