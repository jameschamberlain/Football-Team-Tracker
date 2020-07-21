package com.jameschamberlain.footballteamtracker

import android.content.Context
import android.util.Log
import com.jameschamberlain.footballteamtracker.fixtures.Fixture
import com.jameschamberlain.footballteamtracker.fixtures.Score
import java.io.*
import java.time.LocalDateTime
import java.util.*

object FileUtils {
    private const val TAG = "FileUtils"
    private const val FIXTURES_FILE_NAME = "fixtures.txt"
    private const val TEAM_FILE_NAME = "team.txt"
    private const val PLAYERS_FILE_NAME = "players.txt"
    private var fixturesFile: File? = null
    private var teamFile: File? = null
    private var playersFile: File? = null
    private lateinit var fixturesPath: String
    private lateinit var teamPath: String
    private lateinit var playersPath: String


    fun checkFiles(context: Context) {
        fixturesPath = context.filesDir.toString() + FIXTURES_FILE_NAME
        fixturesFile = File(fixturesPath)
        if (fixturesFile!!.exists()) {
            findFile(fixturesFile!!, fixturesPath)
        }
        teamPath = context.filesDir.toString() + TEAM_FILE_NAME
        teamFile = File(teamPath)
        if (teamFile!!.exists()) {
            findFile(teamFile!!, teamPath)
        }
        playersPath = context.filesDir.toString() + PLAYERS_FILE_NAME
        playersFile = File(playersPath)
        if (playersFile!!.exists()) {
            findFile(playersFile!!, playersPath)
        }
    }

    /**
     * Reads a fixtures text file and stores its contents in a list
     */
    fun readFixturesFile(): ArrayList<Fixture> {
        // Create a new BufferedReader that will go onto read the file's contents
        val reader = findFile(fixturesFile!!, fixturesPath)

        // Stores the current line in the file
        var line: String?
        val fixtures = ArrayList<Fixture>()
        try {
            // While the current line is not empty, read it and store it in the list
            while (reader!!.readLine().also { line = it } != null) {
                val homeTeamName = line!!
                val awayTeamName = reader.readLine()
                val homeScore = reader.readLine().toInt()
                val awayScore = reader.readLine().toInt()
                val goalscorers = ArrayList<String>()
                while (reader.readLine().also { line = it } != "-") {
                    goalscorers.add(line!!)
                }
                val assists = ArrayList<String>()
                while (reader.readLine().also { line = it } != "-") {
                    assists.add(line!!)
                }
                val year = reader.readLine().toInt()
                val month = reader.readLine().toInt()
                val day = reader.readLine().toInt()
                val hour = reader.readLine().toInt()
                val minute = reader.readLine().toInt()
                fixtures.add(Fixture(homeTeamName, awayTeamName, Score(homeScore, awayScore),
                        goalscorers, assists, LocalDateTime.of(year, month, day, hour, minute)))
            }
            reader.close()
        } catch (e: NullPointerException) {
            Log.e(TAG, "Couldn't read fixtures file. The file may be empty")
        } catch (e: IOException) {
            Log.e(TAG, "Couldn't read fixtures file. The file may be empty")
        }
        Log.i(TAG, "Successfully read fixtures file")
        return fixtures
    }

    /**
     * Reads a team text file and returns its contents
     */
    fun readTeamFile(): String? {
        // Create a new BufferedReader that will go onto read the file's contents
        val reader = findFile(teamFile!!, teamPath)

        // Stores the current line in the file
        var line: String?
        var teamName: String? = ""
        try {
            // While the current line is not empty, read it and store it in the list
            while (reader!!.readLine().also { line = it } != null) {
                teamName = line
            }
            reader.close()
        } catch (e: NullPointerException) {
            Log.e(TAG, "Couldn't read team file. The file may be empty")
        } catch (e: IOException) {
            Log.e(TAG, "Couldn't read team file. The file may be empty")
        }
        Log.i(TAG, "Successfully read team file")
        return teamName
    }

    /**
     * Reads a players text file and returns its contents
     */
    fun readPlayersFile(): ArrayList<Player> {
        // Create a new BufferedReader that will go onto read the file's contents
        val reader = findFile(playersFile!!, playersPath)

        // Stores the current line in the file
        var line: String?
        val players = ArrayList<Player>()
        var playerName: String?
        try {
            // While the current line is not empty, read it and store it in the list
            while (reader!!.readLine().also { line = it } != null) {
                playerName = line
                players.add(Player(playerName!!))
            }
            reader.close()
        } catch (e: NullPointerException) {
            Log.e(TAG, "Couldn't read players file. The file may be empty")
        } catch (e: IOException) {
            Log.e(TAG, "Couldn't read players file. The file may be empty")
        }
        Log.i(TAG, "Successfully read team file")
        return players
    }

    private fun findFile(file: File?, path: String): BufferedReader? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader(path))
        } catch (e: FileNotFoundException) {
            Log.i(TAG, "Couldn't find file. Creating the file...")
            try {
                // If the file does not exist, try and create the file
                file?.parentFile?.mkdirs()
                file?.parentFile?.createNewFile()
            } catch (e2: IOException) {
                Log.e(TAG, "Couldn't create file")
            }
            Log.i(TAG, "Created file")
        }
        return reader
    }

    /**
     * Write a team name to a file
     *
     * @param teamName The name of the team
     */
    @JvmStatic
    fun writeTeamFile(teamName: String) {
        try {
            // Create a new BufferedWrite to write to the file
            val writer = BufferedWriter(FileWriter(File(teamPath), false))
            // For every item in the list, write it to the file and then add a line break
            writer.write(teamName)
            writer.close()
        } catch (f: FileNotFoundException) {
            /* If the file cannot be found try to read the file.
                This will include creating the file if it does not already exist,
                then attempt to write to the file again
             */
            readTeamFile()
            writeTeamFile(teamName)
        } catch (e: IOException) {
            Log.e(TAG, e.message!!)
        }
    }

    /**
     * Write a list of players to a file
     *
     * @param players The list of players
     */
    @JvmStatic
    fun writePlayersFile(players: ArrayList<Player>) {
        try {
            // Create a new BufferedWrite to write to the file
            val writer = BufferedWriter(FileWriter(File(playersPath), false))
            // For every player in the list, write it to the file and then add a line break
            for (player in players) {
                writer.write(player.name)
                writer.newLine()
            }
            writer.close()
        } catch (f: FileNotFoundException) {
            /* If the file cannot be found try to read the file.
                This will include creating the file if it does not already exist,
                then attempt to write to the file again
             */
            readPlayersFile()
            writePlayersFile(players)
        } catch (e: IOException) {
            Log.e(TAG, e.message!!)
        }
    }

    /**
     * Write a list of fixtures to a file
     *
     * @param fixtures The list of fixtures
     */
    @JvmStatic
    fun writeFixturesFile(fixtures: ArrayList<Fixture>) {
        try {
            // Create a new BufferedWrite to write to the file
            val writer = BufferedWriter(FileWriter(File(fixturesPath), false))
            // For every fixture in the list, write it to the file and then add a line break
            for (fixture in fixtures) {
                // Writes home team name
                writer.write(fixture.homeTeam)
                writer.newLine()
                // Writes away team name
                writer.write(fixture.awayTeam)
                writer.newLine()
                // Writes home score
                writer.write(fixture.score.home.toString())
                writer.newLine()
                // Writes away score
                writer.write(fixture.score.away.toString())
                writer.newLine()
                // Writes goalscorers
                val goalscorers = fixture.goalscorers
                for (scorer in goalscorers) {
                    writer.write(scorer)
                    writer.newLine()
                }
                // Writes - to signify end of goalscorers
                writer.write("-")
                writer.newLine()
                // Writes assists
                val assists = fixture.assists
                for (assist in assists) {
                    writer.write(assist)
                    writer.newLine()
                }
                // Writes - to signify end of assists
                writer.write("-")
                writer.newLine()
                // Writes year
                writer.write(fixture.dateTime.year.toString())
                writer.newLine()
                // Writes month
                writer.write(fixture.dateTime.monthValue.toString())
                writer.newLine()
                // Writes day
                writer.write(fixture.dateTime.dayOfMonth.toString())
                writer.newLine()
                // Writes hour
                writer.write(fixture.dateTime.hour.toString())
                writer.newLine()
                // Writes minute
                writer.write(fixture.dateTime.minute.toString())
                writer.newLine()
            }
            writer.close()
        } catch (f: FileNotFoundException) {
            /* If the file cannot be found try to read the file.
                This will include creating the file if it does not already exist,
                then attempt to write to the file again
             */
            readFixturesFile()
            writeFixturesFile(fixtures)
        } catch (e: IOException) {
            Log.e(TAG, e.message!!)
        }
    }
}