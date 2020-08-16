package com.jameschamberlain.footballteamtracker

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jameschamberlain.footballteamtracker.objects.AccountType
import com.jameschamberlain.footballteamtracker.objects.Team

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



    fun updateTeamNameTextView(textView: TextView) {
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

    fun showBottomNav(activity: Activity) {
        activity.findViewById<View>(R.id.nav_view).visibility = View.VISIBLE
        val containerLayout = activity.findViewById<FrameLayout>(R.id.nav_host_fragment)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        val pixels = 56 * activity.applicationContext.resources.displayMetrics.density
        params.setMargins(0, 0, 0, pixels.toInt())
        containerLayout.layoutParams = params
    }


}