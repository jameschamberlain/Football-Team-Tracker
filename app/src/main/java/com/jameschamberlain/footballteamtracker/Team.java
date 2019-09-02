package com.jameschamberlain.footballteamtracker;

import com.jameschamberlain.footballteamtracker.fixtures.Fixture;

import java.util.ArrayList;
import java.util.Collections;

public class Team {


    private static Team ourInstance;
    private String name;
    private ArrayList<Player> players;
    private ArrayList<Fixture> fixtures;
    private int numOfFixtures;
    private int gamesPlayed;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int goalDifference;

    public static Team getInstance() {
        if (ourInstance == null) {
            ourInstance = new Team();
        }
        return ourInstance;
    }

    private Team() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
        updatePlayerStats();
    }

    public ArrayList<Fixture> getFixtures() {
        return fixtures;
    }

    public void setFixtures(ArrayList<Fixture> fixtures) {
        this.fixtures = fixtures;
        this.numOfFixtures = fixtures.size();
        updateTeamStats();
    }

    public int getNumOfFixtures() {
        return numOfFixtures;
    }

    public void setNumOfFixtures(int numOfFixtures) {
        this.numOfFixtures = numOfFixtures;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    int getWins() {
        return wins;
    }

    int getDraws() {
        return draws;
    }

    int getLosses() {
        return losses;
    }

    int getGoalsFor() {
        return goalsFor;
    }

    int getGoalsAgainst() {
        return goalsAgainst;
    }

    int getGoalDifference() {
        return goalDifference;
    }

    private void resetTeamStats() {
        this.gamesPlayed = 0;
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
    }

    public void updateTeamStats() {
        resetTeamStats();
        for (Fixture fixture : fixtures) {
            switch (fixture.getResult()) {
                case WIN:
                    this.gamesPlayed++;
                    this.wins++;
                    break;
                case DRAW:
                    this.gamesPlayed++;
                    this.draws++;
                    break;

                case LOSE:
                    this.gamesPlayed++;
                    this.losses++;
                    break;
                default:
                    break;
            }
            if (fixture.getHomeTeam().equals(this.name)) {
                this.goalsFor += fixture.getScore().getHome();
                this.goalsAgainst += fixture.getScore().getAway();
            }
            else {
                this.goalsFor += fixture.getScore().getAway();
                this.goalsAgainst += fixture.getScore().getHome();
            }
        }
        this.goalDifference = this.goalsFor - this.goalsAgainst;
    }

    public void updatePlayerStats() {
        for (Player player : players) {
            player.setGoals(0);
            player.setAssists(0);
        }
        for (Fixture fixture : fixtures) {
            for (Player player : players) {
                player.setGoals(player.getGoals() + Collections.frequency(fixture.getGoalscorers(), player.getName()));
                player.setAssists(player.getAssists() + Collections.frequency(fixture.getAssists(), player.getName()));
            }
        }
    }
}
