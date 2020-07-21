package com.jameschamberlain.footballteamtracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

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
            val teamMembers = FileUtils.readPlayersFile()
            val fixtures = FileUtils.readFixturesFile()
            val team = Team.instance
            team.name = teamName
            team.fixtures = fixtures
            team.players = teamMembers
            val newActivity = Intent(this, MainActivity::class.java)
            this.startActivity(newActivity)
        } else {
            val newActivity = Intent(this, SetupActivity::class.java)
            this.startActivity(newActivity)
        }
    }
}