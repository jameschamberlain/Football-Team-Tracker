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

    /** Path to the current team document in Firestore i.e. teams/[teamId] **/
    lateinit var teamRef: DocumentReference

    /** User account type: admin or user **/
    lateinit var accountType: AccountType

    const val preferencesPath = "com.jameschamberlain.footballteamtracker"


    /**
     * Sets up the app for a new team.
     */
    fun setupNewTeam(
            accountType: AccountType,
            teamId: String,
            teamCode: String,
            activity: Activity
    ) {
        this.teamId = teamId
        this.accountType = accountType
        teamRef = Firebase.firestore.document("teams/$teamId")

        activity.getSharedPreferences(preferencesPath, MODE_PRIVATE)
                .edit().apply {
                    putString("account_type", accountType.toString())
                    putString("team_id", teamId)
                    putString("team_code", teamCode)
                    apply()
                }

        getTeamName()
        setupFixturesListener()
    }

    /**
     * Sets up some global variables: [teamId], [accountType]
     */
    fun prepareTeam(activity: Activity) {
        activity.getSharedPreferences(preferencesPath, MODE_PRIVATE).apply {
            teamId = getString("team_id", null)!!
            accountType = enumValueOf(getString("account_type", null)!!)
            teamRef = Firebase.firestore.document("teams/${teamId}")
            teamRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        Team.name = documentSnapshot.getString("name")!!
                        val teamCode: String = documentSnapshot.getString("code")!!
                        edit().putString("team_code", teamCode).apply()
                        setupFixturesListener()
                        activity.startActivity(Intent(activity.applicationContext, MainActivity::class.java))
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Get failed with ", e)
                    }
        }
    }


    /**
     * Sets up a listener for the team name and
     * updates it locally as required.
     */
    private fun getTeamName() {
        teamRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    Team.name = documentSnapshot.getString("name")!!
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Get failed with ", e)
                }
    }


    /**
     * Sets up a listener for the fixtures document collection
     * and updates the stats as required.
     */
    private fun setupFixturesListener() {
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


}