package com.jameschamberlain.footballteamtracker.data

import android.os.Parcel
import android.os.Parcelable

class Player() : Parcelable, Comparable<Player> {
    lateinit var name: String
    var goals: Int = 0
    var assists: Int = 0

    constructor(parcel: Parcel) : this() {
        name = parcel.readString().toString()
        goals = parcel.readInt()
        assists = parcel.readInt()
    }


    constructor(name: String) : this() {
        this.name = name
        goals = 0
        assists = 0
    }

    override fun toString(): String {
        return "Name: $name, Goals: $goals, Assists: $assists"
    }

    override fun compareTo(other: Player): Int {
        return name.compareTo(other.name)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(goals)
        parcel.writeInt(assists)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player {
            return Player(parcel)
        }

        override fun newArray(size: Int): Array<Player?> {
            return arrayOfNulls(size)
        }
    }
}