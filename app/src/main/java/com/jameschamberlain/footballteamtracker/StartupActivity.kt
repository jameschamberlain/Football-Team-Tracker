package com.jameschamberlain.footballteamtracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.jameschamberlain.footballteamtracker.Team.Companion.team
import com.jameschamberlain.footballteamtracker.setup.SetupActivity


private const val TAG = "StartupActivity"

class StartupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var isSetup = false

        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        Log.e(TAG, "Is the current user signed in? $isLoggedIn")


        FileUtils.checkFiles(this)
        val teamName = FileUtils.readTeamFile()
        if (teamName!!.isNotEmpty()) {
            isSetup = true
        }
        if (isLoggedIn) {
            team.name = teamName
            val fixtures = FileUtils.readFixturesFile()
            team.fixtures = fixtures
            val teamMembers = FileUtils.readPlayersFile()
            team.players = teamMembers



            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, SetupActivity::class.java))
        }
    }
}