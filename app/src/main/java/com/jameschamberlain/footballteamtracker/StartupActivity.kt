package com.jameschamberlain.footballteamtracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jameschamberlain.footballteamtracker.objects.Team
import com.jameschamberlain.footballteamtracker.setup.SetupActivity

private const val TAG = "StartupActivity"

class StartupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

        if (isLoggedIn) {
            Log.i(TAG, "User logged in, progressing to main app")
            val preferences: SharedPreferences = getSharedPreferences("com.jameschamberlain.footballteamtracker", Context.MODE_PRIVATE)
            Utils.teamId = preferences.getString("team_id", null)!!
            Utils.teamRef = Firebase.firestore.document("teams/${Utils.teamId}")
            Utils.teamRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        Team.teamName = documentSnapshot.getString("name")!!
                        Utils.setupTeamListener()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Get failed with ", e)
                    }
        } else {
            startActivity(Intent(this, SetupActivity::class.java))
        }
    }
}