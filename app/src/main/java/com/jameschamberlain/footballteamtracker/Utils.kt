package com.jameschamberlain.footballteamtracker

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jameschamberlain.footballteamtracker.data.AccountType
import com.jameschamberlain.footballteamtracker.data.Team

private const val TAG = "Utils"

object Utils {

    lateinit var teamId: String
    lateinit var teamRef: DocumentReference
    lateinit var accountType: AccountType


    fun setupTeam(
            accountType: AccountType,
            teamId: String,
            teamCode: String,
            activity: Activity
    ) {
        this.teamId = teamId
        teamRef = Firebase.firestore.document("teams/$teamId")
        val preferences: SharedPreferences =
                activity.getSharedPreferences("com.jameschamberlain.footballteamtracker", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("account_type", accountType.toString())
        editor.putString("team_id", teamId)
        editor.putString("team_code", teamCode)
        editor.apply()
        this.accountType = accountType
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


    /**
     * Allows calls like
     *
     * `supportFragmentManager.inTransaction { add(...) }`
     */
    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }


}