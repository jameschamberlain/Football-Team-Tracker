package com.jameschamberlain.footballteamtracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.jameschamberlain.footballteamtracker.onboarding.OnboardingActivity

private const val TAG = "LauncherActivity"

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
                && getSharedPreferences(Utils.preferencesPath, Context.MODE_PRIVATE)
                .getString("team_id", null) != null

        if (isLoggedIn) {
            Log.i(TAG, "User logged in, progressing to app")
            Utils.prepareTeam(this@LauncherActivity)
//            Utils.apply {
//                teamId = preferences.getString("team_id", null)!!
//                accountType = enumValueOf(preferences.getString("account_type", null)!!)
//                teamRef = Firebase.firestore.document("teams/${teamId}")
//                teamRef.get()
//                        .addOnSuccessListener { documentSnapshot ->
//                            Team.name = documentSnapshot.getString("name")!!
//                            val teamCode: String = documentSnapshot.getString("code")!!
//                            val editor = preferences.edit()
//                            editor.putString("team_code", teamCode)
//                            editor.apply()
//                            setupFixturesListener()
//                            startActivity(Intent(applicationContext, MainActivity::class.java))
//                        }
//                        .addOnFailureListener { e ->
//                            Log.e(TAG, "Get failed with ", e)
//                        }
//            }
        }
        else {
            startActivity(Intent(this@LauncherActivity, OnboardingActivity::class.java))
        }
    }
}