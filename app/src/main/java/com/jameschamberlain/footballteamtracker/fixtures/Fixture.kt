package com.jameschamberlain.footballteamtracker.fixtures

import android.os.Parcel
import android.os.Parcelable
import com.jameschamberlain.footballteamtracker.Team.Companion.instance
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList

class Fixture() : Parcelable, Comparable<Fixture> {
    /**
     * The name of the team playing at home.
     */
    lateinit var homeTeam: String

    /**
     * The name of the tea playing away.
     */
    lateinit var awayTeam: String

    /**
     * The final score.
     */
    lateinit var score: Score

    /**
     * A list of the goalscorers.
     */
    lateinit var goalscorers: ArrayList<String>

    /**
     * A list of the assists.
     */
    lateinit var assists: ArrayList<String>

    /**
     * The date and time.
     */
    lateinit var dateTime: LocalDateTime

    /**
     *
     * Constructor for a new fixture.
     *
     * @param homeTeam The name of the team playing at home.
     * @param awayTeam The name of the tea playing away.
     * @param score The final score.
     * @param goalscorers A list of the goalscorers.
     * @param assists A list of the assists.
     * @param dateTime The date and time.
     */
    constructor(homeTeam: String, awayTeam: String, score: Score, goalscorers: ArrayList<String>, assists: ArrayList<String>, dateTime: LocalDateTime) : this() {
        this.homeTeam = homeTeam
        this.awayTeam = awayTeam
        this.score = score
        this.goalscorers = goalscorers
        this.assists = assists
        this.dateTime = dateTime
    }

    /**
     *
     * Constructor for a new fixture when it has not been played yet.
     *
     * @param homeTeam The name of the team playing at home.
     * @param awayTeam The name of the tea playing away.
     * @param dateTime The date and time.
     */
    constructor(homeTeam: String, awayTeam: String, dateTime: LocalDateTime) : this() {
        this.homeTeam = homeTeam
        this.awayTeam = awayTeam
        score = Score()
        goalscorers = ArrayList()
        assists = ArrayList()
        this.dateTime = dateTime
    }



    // Adding 1 because for some reason it's saying months start from 0.
    val dateString: String
        get() {
            val day = dateTime.dayOfMonth
            // Adding 1 because for some reason it's saying months start from 0.
            val month = dateTime.monthValue
            return (String.format(Locale.ENGLISH, "%02d", day) + "/"
                    + String.format(Locale.ENGLISH, "%02d", month) + "/"
                    + dateTime.year)
        }

    // Adding 1 because for some reason it's saying months start from 0.
    val extendedDateString: String
        get() {
            val dayName = dateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val day = dateTime.dayOfMonth
            // Adding 1 because for some reason it's saying months start from 0.
            val month = dateTime.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            return (dayName + ", "
                    + String.format(Locale.ENGLISH, "%02d", day) + " "
                    + month + " "
                    + dateTime.year)
        }

    val timeString: String
        get() {
            val hour = dateTime.hour
            val minute = dateTime.minute
            return String.format(Locale.ENGLISH, "%02d", hour) + ":" + String.format(Locale.ENGLISH, "%02d", minute)
        }

    fun copyOf(): Fixture {
        val newGoalscorers = ArrayList(goalscorers)
        val newAssists = ArrayList(assists)
        return Fixture(homeTeam, awayTeam, score,
                newGoalscorers, newAssists, dateTime)
    }

    override fun toString(): String {
        return """
            Home team: $homeTeam
            Away team: $awayTeam
            Score: $score
            Goalscorers: $goalscorers
            Assists: $assists
            Date: $dateString
            Time: $timeString
            """.trimIndent()
    }



    override fun compareTo(other: Fixture): Int {
        return dateTime.compareTo(other.dateTime)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Fixture) return false
        return homeTeam == other.homeTeam && awayTeam == other.awayTeam && score == other.score && goalscorers == other.goalscorers && assists == other.assists &&
                dateTime.isEqual(other.dateTime)
    }

    /**
     *
     * Works out if the result of a fixture was a win,
     * loss, draw or hasn't been played yet.
     *
     * @return The result of the fixture.
     */
    val result: FixtureResult
        get() {
            val teamName = instance.name
            return if (homeTeam == teamName) {
                when {
                    score.home > score.away -> FixtureResult.WIN
                    score.home < score.away -> FixtureResult.LOSE
                    score.home == -1 -> FixtureResult.UNPLAYED
                    else -> FixtureResult.DRAW
                }
            } else {
                when {
                    score.home > score.away -> FixtureResult.LOSE
                    score.home < score.away -> FixtureResult.WIN
                    score.home == -1 -> FixtureResult.UNPLAYED
                    else -> FixtureResult.DRAW
                }
            }
        }


    // ========== Parcelable implementation start ==========
    constructor(parcel: Parcel) : this() {
        homeTeam = parcel.readString().toString()
        awayTeam = parcel.readString().toString()
        score = parcel.readSerializable() as Score
        goalscorers = parcel.createStringArrayList() as ArrayList<String>
        assists = parcel.createStringArrayList() as ArrayList<String>
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(homeTeam)
        parcel.writeString(awayTeam)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Fixture> {
        override fun createFromParcel(parcel: Parcel): Fixture {
            return Fixture(parcel)
        }

        override fun newArray(size: Int): Array<Fixture?> {
            return arrayOfNulls(size)
        }
    }
    // ========== Parcelable implementation end ==========

    override fun hashCode(): Int {
        var result = homeTeam.hashCode()
        result = 31 * result + awayTeam.hashCode()
        result = 31 * result + score.hashCode()
        result = 31 * result + goalscorers.hashCode()
        result = 31 * result + assists.hashCode()
        result = 31 * result + dateTime.hashCode()
        return result
    }

}