package com.jameschamberlain.footballteamtracker.fixtures;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.jameschamberlain.footballteamtracker.R;
import com.jameschamberlain.footballteamtracker.Team;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class Fixture implements Parcelable, Comparable<Fixture> {

    /**
     *  The name of the team playing at home.
     */
    private String homeTeam;
    /**
     *  The name of the tea playing away.
     */
    private String awayTeam;
    /**
     *  The final score.
     */
    private Score score;
    /**
     * A list of the goalscorers.
     */
    private ArrayList<String> goalscorers;
    /**
     * A list of the assists.
     */
    private ArrayList<String> assists;
    /**
     * The date and time.
     */
    private LocalDateTime dateTime;


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
    public Fixture(String homeTeam, String awayTeam, Score score, ArrayList<String> goalscorers, ArrayList<String> assists, LocalDateTime dateTime) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.score = score;
        this.goalscorers = goalscorers;
        this.assists = assists;
        this.dateTime = dateTime;
    }


    /**
     *
     * Constructor for a new fixture when it has not been played yet.
     *
     * @param homeTeam The name of the team playing at home.
     * @param awayTeam The name of the tea playing away.
     * @param dateTime The date and time.
     */
    public Fixture(String homeTeam, String awayTeam, LocalDateTime dateTime) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.score = new Score();
        this.goalscorers = new ArrayList<>();
        this.assists = new ArrayList<>();
        this.dateTime = dateTime;
    }


    /**
     *
     * Constructor for a new fixture from a parcel.
     *
     * @param in The fixture being transferred.
     */
    protected Fixture(Parcel in) {
        this.homeTeam = in.readString();
        this.awayTeam = in.readString();
        this.score = (Score) in.readSerializable();
        this.goalscorers = in.createStringArrayList();
        this.assists = in.createStringArrayList();
    }

    public static final Creator<Fixture> CREATOR = new Creator<Fixture>() {
        @Override
        public Fixture createFromParcel(Parcel in) {
            return new Fixture(in);
        }

        @Override
        public Fixture[] newArray(int size) {
            return new Fixture[size];
        }
    };

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Score getScore() {
        return score;
    }

    void setScore(Score score) {
        this.score = score;
    }

    public ArrayList<String> getGoalscorers() {
        return goalscorers;
    }

    void setGoalscorers(ArrayList<String> goalscorers) {
        this.goalscorers = goalscorers;
    }

    public ArrayList<String> getAssists() {
        return assists;
    }

    void setAssists(ArrayList<String> assists) {
        this.assists = assists;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateString() {
        int day = this.dateTime.getDayOfMonth();
        // Adding 1 because for some reason it's saying months start from 0.
        int month = this.dateTime.getMonthValue();
        return String.format(Locale.ENGLISH, "%02d", day) + "/"
                + String.format(Locale.ENGLISH, "%02d", month) + "/"
                + this.dateTime.getYear();
    }

    String getExtendedDateString() {
        String dayName = this.dateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
        int day = this.dateTime.getDayOfMonth();
        // Adding 1 because for some reason it's saying months start from 0.
        String month = this.dateTime.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault());
        return dayName + ", "
                + String.format(Locale.ENGLISH, "%02d", day) + " "
                + month + " "
                + this.dateTime.getYear();
    }

    public String getTimeString() {
        int hour = this.dateTime.getHour();
        int minute = this.dateTime.getMinute();
        return String.format(Locale.ENGLISH, "%02d", hour) + ":" + String.format(Locale.ENGLISH, "%02d", minute);
    }

    Fixture copyOf() {
        ArrayList<String> newGoalscorers = new ArrayList<>(goalscorers);
        ArrayList<String> newAssists = new ArrayList<>(assists);
        return new Fixture(this.homeTeam, this.awayTeam, this.score,
                newGoalscorers, newAssists, this.dateTime);

    }

    @NonNull
    @Override
    public String toString() {
        return "Home team: " + homeTeam + "\n"
                + "Away team: " + awayTeam + "\n"
                + "Score: " + score + "\n"
                + "Goalscorers: " + goalscorers + "\n"
                + "Assists: " + assists + "\n"
                + "Date: " + getDateString() + "\n"
                + "Time: " + getTimeString();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(homeTeam);
        dest.writeString(awayTeam);
        dest.writeSerializable(score);
        dest.writeStringList(goalscorers);
        dest.writeStringList(assists);
    }

    @Override
    public int compareTo(Fixture o) {
        return getDateTime().compareTo(o.getDateTime());
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj == this) return true;

        if (!(obj instanceof Fixture)) return false;

        Fixture f = (Fixture) obj;

        return homeTeam.equals(f.homeTeam) &&
                awayTeam.equals(f.awayTeam) &&
                score.equals(f.score) &&
                goalscorers.equals(f.goalscorers) &&
                assists.equals(f.assists) &&
                dateTime.isEqual(f.dateTime);
    }


    /**
     *
     * Works out if the result of a fixture was a win,
     * loss, draw or hasn't been played yet.
     *
     * @return The result of the fixture.
     */
    public FixtureResult getResult() {
        String teamName = Team.getInstance().getName();
        if (getHomeTeam().equals(teamName)) {
            if (getScore().getHome() > getScore().getAway()) {
                return FixtureResult.WIN;
            }
            else if (getScore().getHome() < getScore().getAway()) {
                return FixtureResult.LOSE;
            }
            // -1 means game hasn't been played yet.
            else if (getScore().getHome() == -1) {
                return FixtureResult.UNPLAYED;
            }
            else {
                return FixtureResult.DRAW;
            }
        }
        else {
            if (getScore().getHome() > getScore().getAway()) {
                return FixtureResult.LOSE;
            }
            else if (getScore().getHome() < getScore().getAway()) {
                return FixtureResult.WIN;
            }
            // -1 means game hasn't been played yet.
            else if (getScore().getHome() == -1) {
                return FixtureResult.UNPLAYED;
            }
            else {
                return FixtureResult.DRAW;
            }
        }
    }
}
