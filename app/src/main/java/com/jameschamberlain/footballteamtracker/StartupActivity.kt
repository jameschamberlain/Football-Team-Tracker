package com.jameschamberlain.footballteamtracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.jameschamberlain.footballteamtracker.setup.SetupActivity

private const val TAG = "StartupActivity"

class StartupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

        if (isLoggedIn) {
            Log.i(TAG, "User logged in, progressing to main app")
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, SetupActivity::class.java))
        }
    }
}