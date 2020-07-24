package com.jameschamberlain.footballteamtracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jameschamberlain.footballteamtracker.Team.Companion.team

class StartupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var isSetup = false
        FileUtils.checkFiles(this)
        val teamName = FileUtils.readTeamFile()
        if (teamName!!.isNotEmpty()) {
            isSetup = true
        }
        if (isSetup) {
            team.name = teamName
            val teamMembers = FileUtils.readPlayersFile()
            team.players = teamMembers
            val fixtures = FileUtils.readFixturesFile()
            team.fixtures = fixtures
            val newActivity = Intent(this, MainActivity::class.java)
            this.startActivity(newActivity)
        } else {
            val newActivity = Intent(this, SetupActivity::class.java)
            this.startActivity(newActivity)
        }
    }
}