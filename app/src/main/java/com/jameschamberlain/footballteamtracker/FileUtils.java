package com.jameschamberlain.footballteamtracker;

import android.content.Context;
import android.util.Log;

import com.jameschamberlain.footballteamtracker.fixtures.Fixture;
import com.jameschamberlain.footballteamtracker.fixtures.Score;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FileUtils {

    private static final String TAG = "FileUtils";

    private static final String FIXTURES_FILE_NAME = "fixtures.txt";
    private static final String TEAM_FILE_NAME = "team.txt";
    private static final String PLAYERS_FILE_NAME = "players.txt";
    private static File fixturesFile;
    private static File teamFile;
    private static File playersFile;
    private static String fixturesPath;
    private static String teamPath;
    private static String playersPath;


    public static void checkFiles(Context context) {
        fixturesPath = context.getFilesDir() + FIXTURES_FILE_NAME;
        fixturesFile = new File(fixturesPath);
        if (!fixturesFile.exists()) {
            findFile(fixturesFile, fixturesPath);
        }

        teamPath = context.getFilesDir() + TEAM_FILE_NAME;
        teamFile = new File(teamPath);
        if (!teamFile.exists()) {
            findFile(teamFile, teamPath);
        }

        playersPath = context.getFilesDir() + PLAYERS_FILE_NAME;
        playersFile = new File(playersPath);
        if (!playersFile.exists()) {
            findFile(playersFile, playersPath);
        }
    }

    /**
     * Reads a fixtures text file and stores its contents in a list
     */
    public static ArrayList<Fixture> readFixturesFile() {
        // Create a new BufferedReader that will go onto read the file's contents
        BufferedReader reader = findFile(fixturesFile, fixturesPath);

        // Stores the current line in the file
        String line;

        ArrayList<Fixture> fixtures = new ArrayList<>();

        try {
            // While the current line is not empty, read it and store it in the list
            while ((line = reader.readLine()) != null) {
                String homeTeamName = line;
                String awayTeamName = reader.readLine();
                int homeScore = Integer.parseInt(reader.readLine());
                int awayScore = Integer.parseInt(reader.readLine());
                ArrayList<String> goalscorers = new ArrayList<>();
                while (!((line = reader.readLine()).equals("-"))) {
                    goalscorers.add(line);
                }
                ArrayList<String> assists = new ArrayList<>();
                while (!((line = reader.readLine()).equals("-"))) {
                    assists.add(line);
                }
                int year = Integer.parseInt(reader.readLine());
                int month = Integer.parseInt(reader.readLine());
                int day = Integer.parseInt(reader.readLine());
                int hour = Integer.parseInt(reader.readLine());
                int minute = Integer.parseInt(reader.readLine());

                fixtures.add(new Fixture(homeTeamName, awayTeamName, new Score(homeScore, awayScore),
                        goalscorers, assists, LocalDateTime.of(year, month, day, hour, minute)));
            }
            reader.close();
        } catch (NullPointerException | IOException e) {
            Log.e(TAG, "Couldn't read fixtures file. The file may be empty");
        }
        Log.i(TAG, "Successfully read fixtures file");
        return fixtures;
    }


    /**
     * Reads a team text file and returns its contents
     */
    public static String readTeamFile() {
        // Create a new BufferedReader that will go onto read the file's contents
        BufferedReader reader = findFile(teamFile, teamPath);

        // Stores the current line in the file
        String line;

        String teamName = "";

        try {
            // While the current line is not empty, read it and store it in the list
            while ((line = reader.readLine()) != null) {
                teamName = line;
            }
            reader.close();
        } catch (NullPointerException | IOException e) {
            Log.e(TAG, "Couldn't read team file. The file may be empty");
        }
        Log.i(TAG, "Successfully read team file");
        return teamName;
    }


    /**
     * Reads a players text file and returns its contents
     */
    public static ArrayList<Player> readPlayersFile() {
        // Create a new BufferedReader that will go onto read the file's contents
        BufferedReader reader = findFile(playersFile, playersPath);

        // Stores the current line in the file
        String line;

        ArrayList<Player> players = new ArrayList<>();
        String playerName;

        try {
            // While the current line is not empty, read it and store it in the list
            while ((line = reader.readLine()) != null) {
                playerName = line;
                players.add(new Player(playerName));
            }
            reader.close();
        } catch (NullPointerException | IOException e) {
            Log.e(TAG, "Couldn't read players file. The file may be empty");
        }
        Log.i(TAG, "Successfully read team file");
        return players;
    }


    public static BufferedReader findFile(File file, String path) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            Log.i(TAG, "Couldn't find file. Creating the file...");
            try {
                // If the file does not exist, try and create the file
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e2) {
                Log.e(TAG, "Couldn't create file");
            }
            Log.i(TAG, "Created file");
        }
        return reader;
    }


    /**
     * Write a team name to a file
     *
     * @param teamName The name of the team
     */
    public static void writeTeamFile(String teamName) {
        try {
            // Create a new BufferedWrite to write to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(teamPath), false));
            // For every item in the list, write it to the file and then add a line break
            writer.write(teamName);
            writer.close();
        } catch (FileNotFoundException f) {
            /* If the file cannot be found try to read the file.
                This will include creating the file if it does not already exist,
                then attempt to write to the file again
             */
            readTeamFile();
            writeTeamFile(teamName);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    /**
     * Write a list of players to a file
     *
     * @param players The list of players
     */
    public static void writePlayersFile(ArrayList<Player> players) {
        try {
            // Create a new BufferedWrite to write to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(playersPath), false));
            // For every player in the list, write it to the file and then add a line break
            for (Player player : players) {
                writer.write(player.getName());
                writer.newLine();
            }
            writer.close();
        } catch (FileNotFoundException f) {
            /* If the file cannot be found try to read the file.
                This will include creating the file if it does not already exist,
                then attempt to write to the file again
             */
            readPlayersFile();
            writePlayersFile(players);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    /**
     * Write a list of fixtures to a file
     *
     * @param fixtures The list of fixtures
     */
    public static void writeFixturesFile(ArrayList<Fixture> fixtures) {
        try {
            // Create a new BufferedWrite to write to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fixturesPath), false));
            // For every fixture in the list, write it to the file and then add a line break
            for (Fixture fixture : fixtures) {
                // Writes home team name
                writer.write(fixture.getHomeTeam());
                writer.newLine();
                // Writes away team name
                writer.write(fixture.getAwayTeam());
                writer.newLine();
                // Writes home score
                writer.write(Integer.toString(fixture.getScore().getHome()));
                writer.newLine();
                // Writes away score
                writer.write(Integer.toString(fixture.getScore().getAway()));
                writer.newLine();
                // Writes goalscorers
                ArrayList<String> goalscorers = fixture.getGoalscorers();
                for (String scorer : goalscorers) {
                    writer.write(scorer);
                    writer.newLine();
                }
                // Writes - to signify end of goalscorers
                writer.write("-");
                writer.newLine();
                // Writes assists
                ArrayList<String> assists = fixture.getAssists();
                for (String assist : assists) {
                    writer.write(assist);
                    writer.newLine();
                }
                // Writes - to signify end of assists
                writer.write("-");
                writer.newLine();
                // Writes year
                writer.write(Integer.toString(fixture.getDateTime().getYear()));
                writer.newLine();
                // Writes month
                writer.write(Integer.toString(fixture.getDateTime().getMonthValue()));
                writer.newLine();
                // Writes day
                writer.write(Integer.toString(fixture.getDateTime().getDayOfMonth()));
                writer.newLine();
                // Writes hour
                writer.write(Integer.toString(fixture.getDateTime().getHour()));
                writer.newLine();
                // Writes minute
                writer.write(Integer.toString(fixture.getDateTime().getMinute()));
                writer.newLine();
            }
            writer.close();
        } catch (FileNotFoundException f) {
            /* If the file cannot be found try to read the file.
                This will include creating the file if it does not already exist,
                then attempt to write to the file again
             */
            readFixturesFile();
            writeFixturesFile(fixtures);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

}
