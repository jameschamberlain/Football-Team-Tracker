package com.jameschamberlain.footballteamtracker

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.widget.TextView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jameschamberlain.footballteamtracker.objects.Team

private const val TAG = "Utils"

object Utils {

    lateinit var teamId: String
    lateinit var teamRef: DocumentReference


    fun setupTeamPathWithId(teamId: String, activity: Activity) {
        this.teamId = teamId
        teamRef = Firebase.firestore.document("teams/$teamId")
        val preferences: SharedPreferences =
                activity.getSharedPreferences("com.jameschamberlain.footballteamtracker", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("team_id", teamId)
        editor.apply()
        getTeamNameTest()
        setupTeamListener()
    }

    fun getTeamNameTest(): String {
        teamRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    Team.teamName = documentSnapshot.getString("name")!!
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Get failed with ", e)
                }
        return Team.teamName
    }

    fun setupTeamPath(activity: Activity) {
        val preferences: SharedPreferences = activity.getSharedPreferences("com.jameschamberlain.footballteamtracker", MODE_PRIVATE)
        teamId = preferences.getString("team_id", null)!!
        teamRef = Firebase.firestore.document("teams/$teamId")
        getTeamNameTest()
        setupTeamListener()
    }

    fun setupTeamListener() {
        Log.d(TAG, "Setting up snapshot listener")
        teamRef.collection("fixtures").get()
        teamRef.collection("fixtures").addSnapshotListener { snapshot, e ->

            Log.d(TAG, "Snapshot listener created")
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            Log.d(TAG, "Snapshot data changed")
            Team.updateStats(snapshot!!.documents)
        }
    }



    fun setTeamNameTextView(textView: TextView) {
        teamRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        textView.text = document.getString("name")!!
                    } else {
                        // Convert the whole Query Snapshot to a list
                        // of objects directly! No need to fetch each
                        // document.
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Get failed with ", e)
                }
    }


}