package com.jameschamberlain.footballteamtracker.fixtures;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Score implements Serializable {

    private int home;
    private int away;

    public Score() {
        this.home = -1;
        this.away = -1;
    }

    public Score(int home, int away) {
        this.home = home;
        this.away = away;
    }

    public int getHome() {
        return home;
    }

    public void setHome(int home) {
        this.home = home;
    }

    public int getAway() {
        return away;
    }

    public void setAway(int away) {
        this.away = away;
    }

    @Override
    public String toString() {
        if (home == -1 && away == -1) {
            return "- : -";
        } else {
            return home + " : " + away;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;

        if (!(obj instanceof Score)) return false;

        Score s = (Score) obj;

        return home == s.home && away == s.away;
    }
}
