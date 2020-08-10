package com.jameschamberlain.footballteamtracker.objects

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot


private const val TAG = "Team"

object Team {

    var teamName: String = ""

    var totalGames = 0
    var gamesPlayed = 0
    var wins = 0
    var draws = 0
    var losses = 0
    var goalsFor = 0
    var goalsAgainst = 0
    var goalDifference = 0

    private fun resetTeamStats() {
        totalGames = 0
        gamesPlayed = 0
        wins = 0
        draws = 0
        losses = 0
        goalsFor = 0
        goalsAgainst = 0
    }

    fun updateStats(documents: List<DocumentSnapshot>) {
        Log.d(TAG, "We're updating stats")
        resetTeamStats()
        for (document in documents) {
            val fixture = document.toObject(Fixture::class.java)!!
            totalGames += 1
            when (fixture.result) {
                FixtureResult.WIN -> {
                    gamesPlayed++
                    wins++
                }
                FixtureResult.DRAW -> {
                    gamesPlayed++
                    draws++
                }
                FixtureResult.LOSS -> {
                    gamesPlayed++
                    losses++
                }
                else -> {
                }
            }
            if (fixture.result != FixtureResult.UNPLAYED) {
                if (fixture.isHomeGame) {
                    goalsFor += fixture.score.home
                    goalsAgainst += fixture.score.away
                } else {
                    goalsFor += fixture.score.away
                    goalsAgainst += fixture.score.home
                }
            }
        }
        goalDifference = goalsFor - goalsAgainst
    }

}