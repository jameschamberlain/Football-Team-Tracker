package com.jameschamberlain.footballteamtracker.objects

import java.util.*
import kotlin.collections.ArrayList

class Team {
    lateinit var name: String
    var players: ArrayList<Player> = ArrayList()
        set(value) {
            field = value
            updatePlayerStats()
        }
    var fixtures: ArrayList<Fixture> = ArrayList()
        set(value) {
            field = value
            numOfFixtures = fixtures.size
            updateTeamStats()
        }
    var numOfFixtures = 0
    var gamesPlayed = 0
        private set
    var wins = 0
        private set
    var draws = 0
        private set
    var losses = 0
        private set
    var goalsFor = 0
        private set
    var goalsAgainst = 0
        private set
    var goalDifference = 0
        private set

    private fun resetTeamStats() {
        gamesPlayed = 0
        wins = 0
        draws = 0
        losses = 0
        goalsFor = 0
        goalsAgainst = 0
    }

    fun updateTeamStats() {
        resetTeamStats()
        for (fixture in fixtures) {
            when (fixture.result) {
                FixtureResult.WIN -> {
                    gamesPlayed++
                    wins++
                }
                FixtureResult.DRAW -> {
                    gamesPlayed++
                    draws++
                }
                FixtureResult.LOSE -> {
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

    fun updatePlayerStats() {
        for (player in players) {
            player.goals = 0
            player.assists = 0
        }
        for (fixture in fixtures) {
            for (player in players) {
                player.goals = player.goals + Collections.frequency(fixture.goalscorers, player.name)
                player.assists = player.assists + Collections.frequency(fixture.assists, player.name)
            }
        }
    }

    companion object {
        val team = Team()
    }
}