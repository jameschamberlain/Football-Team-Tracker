package com.jameschamberlain.footballteamtracker

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jameschamberlain.footballteamtracker.data.AccountType
import com.jameschamberlain.footballteamtracker.data.Team

private const val TAG = "Utils"

object Utils {

    /** Firebase id of the team **/
    lateinit var teamId: String

    const val preferencesPath = "com.jameschamberlain.footballteamtracker"


    /**
     * Sets up the app for a new team.
     */
    fun setupNewTeam(
            accountType: AccountType,
            teamId: String,
            teamCode: String,
            teamName: String,
            activity: Activity
    ) {
        this.teamId = teamId

        activity.getSharedPreferences(preferencesPath, MODE_PRIVATE)
                .edit().apply {
                    putString("account_type", accountType.toString())
                    putString("team_id", teamId)
                    putString("team_code", teamCode)
                    putString("team_name", teamName)
                    apply()
                }
        setupFixturesListener(activity)
    }

    /**
     * Sets up some global variables: [teamId]
     */
    fun prepareTeam(activity: Activity) {
        activity.getSharedPreferences(preferencesPath, MODE_PRIVATE).apply {
            teamId = getString("team_id", null)!!
            getTeamReference(activity).get()
                    .addOnSuccessListener { documentSnapshot ->
                        edit()
                            .putString("team_code", documentSnapshot.getString("code")!!)
                            .putString("team_name", documentSnapshot.getString("name")!!)
                            .apply()
                        setupFixturesListener(activity)
                        activity.startActivity(Intent(activity.applicationContext, MainActivity::class.java))
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Get failed with ", e)
                    }
        }
    }

    fun getTeamReference(activity: Activity): DocumentReference {
        teamId =  activity.getSharedPreferences(preferencesPath, MODE_PRIVATE).getString("team_id", null)!!
        return Firebase.firestore.document("teams/$teamId")
    }


    /**
     * Gets the team name from the shared preferences.
     */
    fun getAccountType(activity: Activity): AccountType {
        return enumValueOf(activity.getSharedPreferences(preferencesPath, MODE_PRIVATE).getString("account_type", null)!!)
    }


    /**
     * Gets the team name from the shared preferences.
     */
    fun getTeamName(activity: Activity): String {
        return activity.getSharedPreferences(preferencesPath, MODE_PRIVATE).getString("team_name", "")!!
    }


    /**
     * Sets up a listener for the fixtures document collection
     * and updates the stats as required.
     */
    private fun setupFixturesListener(activity: Activity) {
        Log.d(TAG, "Setting up snapshot listener")
        getTeamReference(activity).collection("fixtures").get()
        getTeamReference(activity).collection("fixtures").addSnapshotListener { snapshot, e ->

            Log.d(TAG, "Snapshot listener created")
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            Log.d(TAG, "Snapshot data changed")
            Team.updateStats(snapshot!!.documents)
        }
    }


}