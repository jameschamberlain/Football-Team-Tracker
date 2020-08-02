package com.jameschamberlain.footballteamtracker.setup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jameschamberlain.footballteamtracker.FileUtils
import com.jameschamberlain.footballteamtracker.MainActivity
import com.jameschamberlain.footballteamtracker.Team
import com.jameschamberlain.footballteamtracker.databinding.FragmentSetupNewTeam2Binding
import com.jameschamberlain.footballteamtracker.databinding.FragmentSetupNewTeamBinding

class SetupNewTeamFragment2 : Fragment() {

    private val TAG = "SetupNewFragment2.kt"
    private val db = Firebase.firestore

    private lateinit var binding: FragmentSetupNewTeam2Binding
    lateinit var userId: String
    private var isNewUser: Boolean = true
    private lateinit var teamName: String
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            checkFieldForEmptyValues()
        }
    }

    fun checkFieldForEmptyValues() {
        teamName = binding.editTextField.text.toString()
        binding.continueButton.isEnabled = teamName != ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentSetupNewTeam2Binding.inflate(layoutInflater)

        binding.continueButton.setOnClickListener {
            teamName = binding.editTextField.text.toString()
            val team: Team = Team.team
            team.name = teamName

            //TODO
            val teamMap = hashMapOf("name" to teamName)
            db.collection("users").document(userId).collection("teams").document(teamName.toLowerCase())
                    .set(teamMap)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully added") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }

            startActivity(Intent(activity, MainActivity::class.java))
        }
        binding.editTextField.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        binding.editTextField.addTextChangedListener(textWatcher)
        checkFieldForEmptyValues()


        binding.googleSignInButton.setOnClickListener {
            val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN)
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser!!
                Log.d(TAG, "User successfully signed in with ID: ${user.uid}")

//                val userMap = hashMapOf("id" to user.uid)
//                db.collection("users").
//                        .add(userMap)
//                        .addOnSuccessListener {
//                            Log.d(TAG, "DocumentSnapshot successfully added")
//                            userId = user.uid
//                        }
//                        .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
                userId = user.uid
                Toast.makeText(context, "Sign-in complete", Toast.LENGTH_SHORT).show()
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...

                Toast.makeText(context, "Sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }

}