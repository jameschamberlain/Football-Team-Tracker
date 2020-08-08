package com.jameschamberlain.footballteamtracker.objects

import java.io.Serializable

class Score : Serializable {
    var home: Int
    var away: Int

    internal constructor() {
        home = -1
        away = -1
    }

    constructor(home: Int, away: Int) {
        this.home = home
        this.away = away
    }

    override fun toString(): String {
        return if (home == -1 && away == -1) {
            "- : -"
        } else {
            "$home : $away"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Score) return false
        return home == other.home && away == other.away
    }

    override fun hashCode(): Int {
        var result = home
        result = 31 * result + away
        return result
    }
}