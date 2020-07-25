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