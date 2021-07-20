package com.jameschamberlain.footballteamtracker.data

import android.os.Parcel
import android.os.Parcelable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class Fixture() : Parcelable, Comparable<Fixture> {

    /**
     * The name of the opponent.
     */
    lateinit var opponent: String

    /**
     * Whether the game is being played at home or not.
     */
    var isHomeGame = true
        set(value) {
            field = value
            score = score
        }

    /**
     * The final score.
     */
    var score: Score = Score()
        set(value) {
            field = value
            if (score.home != -1 && score.away != -1) {
                this.result = if (isHomeGame) {
                    when {
                        score.home > score.away -> FixtureResult.WIN
                        score.home < score.away -> FixtureResult.LOSS
                        score.home == -1 -> FixtureResult.UNPLAYED
                        else -> FixtureResult.DRAW
                    }
                } else {
                    when {
                        score.home > score.away -> FixtureResult.LOSS
                        score.home < score.away -> FixtureResult.WIN
                        score.home == -1 -> FixtureResult.UNPLAYED
                        else -> FixtureResult.DRAW
                    }
                }
            }
        }

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
    var dateTime by Delegates.notNull<Long>()

    /**
     * The result of the fixture
     */
    var result: FixtureResult = FixtureResult.UNPLAYED
        private set

    /**
     *
     * Constructor for a new fixture.
     *
     * @param opponent The name of the opponent.
     * @param isHomeGame Whether the game is at home or not.
     * @param score The final score.
     * @param goalscorers A list of the goalscorers.
     * @param assists A list of the assists.
     * @param dateTime The date and time.
     */
    constructor(opponent: String, isHomeGame: Boolean, score: Score, goalscorers: ArrayList<String>, assists: ArrayList<String>, dateTime: Long) : this() {
        this.opponent = opponent
        this.isHomeGame = isHomeGame
        this.score = score
        this.goalscorers = goalscorers
        this.assists = assists
        this.dateTime = dateTime
    }

    /**
     *
     * Constructor for a new fixture when it has not been played yet.
     *
     * @param opponent The name of the opponent.
     * @param isHomeGame Whether the game is at home or not.
     * @param dateTime The date and time.
     */
    constructor(opponent: String, isHomeGame: Boolean, dateTime: Long) : this() {
        this.opponent = opponent
        this.isHomeGame = isHomeGame
        score = Score()
        goalscorers = ArrayList()
        assists = ArrayList()
        this.dateTime = dateTime
    }


    // Adding 1 because for some reason it's saying months start from 0.
    fun dateString(): String {
        val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.systemDefault())
        val day = date.dayOfMonth
        // Adding 1 because for some reason it's saying months start from 0.
        val month = date.monthValue
        return (String.format(Locale.getDefault(), "%02d", day) + "/"
                + String.format(Locale.getDefault(), "%02d", month) + "/"
                + date.year)
    }

    // Adding 1 because for some reason it's saying months start from 0.
    fun extendedDateString(): String {
        val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.systemDefault())
        val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        val day = date.dayOfMonth
        // Adding 1 because for some reason it's saying months start from 0.
        val month = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        return (dayName + ", "
                + String.format(Locale.getDefault(), "%02d", day) + " "
                + month + " "
                + date.year)
    }

    fun timeString(): String {
        val time = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.systemDefault())
        val hour = time.hour
        val minute = time.minute
        return String.format(Locale.getDefault(), "%02d", hour) + ":" + String.format(Locale.getDefault(), "%02d", minute)
    }

    fun copyOf(): Fixture {
        val newGoalscorers = ArrayList(goalscorers)
        val newAssists = ArrayList(assists)
        return Fixture(opponent, isHomeGame, score,
                newGoalscorers, newAssists, dateTime)
    }

    override fun toString(): String {
        return """
            Opponent: $opponent
            Is home game: $isHomeGame
            Score: $score
            Result: $result
            Goalscorers: $goalscorers
            Assists: $assists
            Date: ${dateString()}
            Time: ${timeString()}
            """.trimIndent()
    }


    override fun compareTo(other: Fixture): Int {
        return dateTime.compareTo(other.dateTime)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Fixture) return false
        return opponent == other.opponent && isHomeGame == other.isHomeGame && score == other.score && goalscorers == other.goalscorers && assists == other.assists &&
                dateTime == other.dateTime
    }


    // ========== Parcelable implementation start ==========
    constructor(parcel: Parcel) : this() {
        opponent = parcel.readString().toString()
        isHomeGame = parcel.readInt() == 1
        score = parcel.readSerializable() as Score
        goalscorers = parcel.createStringArrayList() as ArrayList<String>
        assists = parcel.createStringArrayList() as ArrayList<String>
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(opponent)
        parcel.writeInt(if (isHomeGame) 1 else 0)
        parcel.writeSerializable(score)
        parcel.writeList(goalscorers as List<String>)
        parcel.writeList(assists as List<String>)

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
        var result = opponent.hashCode()
        result = 31 * result + isHomeGame.hashCode()
        result = 31 * result + score.hashCode()
        result = 31 * result + goalscorers.hashCode()
        result = 31 * result + assists.hashCode()
        result = 31 * result + dateTime.hashCode()
        return result
    }

}