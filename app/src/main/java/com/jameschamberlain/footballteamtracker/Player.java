package com.jameschamberlain.footballteamtracker;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Player implements Parcelable, Comparable<Player> {

    private String name;
    private int goals;
    private int assists;

    public Player(String name) {
        this.name = name;
        this.goals = 0;
        this.assists = 0;
    }

    protected Player(Parcel in) {
        name = in.readString();
        goals = in.readInt();
        assists = in.readInt();
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    @NonNull
    @Override
    public String toString() {
        return "Name: " + this.name + ", Goals: " + this.goals + ", Assists: " + this.assists;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(goals);
        dest.writeInt(assists);
    }

    @Override
    public int compareTo(Player o) {
        return getName().compareTo(o.getName());
    }
}
