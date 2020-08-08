package com.jameschamberlain.footballteamtracker

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val TAG = "Utils"

object Utils {

    private lateinit var teamId: String
    lateinit var teamRef: DocumentReference
    var teamName: String = ""

    fun setupTeamPathWithId(teamId: String, activity: Activity) {
        this.teamId = teamId
        teamRef = Firebase.firestore.document("teams/$teamId")
        val preferences: SharedPreferences =
                activity.getSharedPreferences("com.jameschamberlain.footballteamtracker", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("team_id", teamId)
        editor.apply()
    }

    fun getTeamNameTest(): String {
        teamRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    teamName = documentSnapshot.getString("name")!!
                }
        return teamName
    }

    fun setupTeamPath(activity: Activity) {
        val preferences: SharedPreferences = activity.getSharedPreferences("com.jameschamberlain.footballteamtracker", MODE_PRIVATE)
        teamId = preferences.getString("team_id", null)!!
        teamRef = Firebase.firestore.document("teams/$teamId")
    }

//    fun setupTeamListener() {
//        docRef.addSnapshotListener { snapshot, e ->
//            if (e != null) {
//                Log.w(TAG, "Listen failed.", e)
//                return@addSnapshotListener
//            }
//        }
//    }

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