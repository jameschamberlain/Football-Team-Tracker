package com.jameschamberlain.footballteamtracker.setup

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jameschamberlain.footballteamtracker.MainActivity
import com.jameschamberlain.footballteamtracker.databinding.FragmentSetupNewTeam2Binding
import java.util.*


private const val TAG = "SetupNewFragment2.kt"

class SetupNewTeamFragment2 : Fragment() {

    private val db = Firebase.firestore

    private lateinit var binding: FragmentSetupNewTeam2Binding
    private val userId = FirebaseAuth.getInstance().currentUser?.uid!!
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

            val teamMap = hashMapOf("name" to teamName)
            db.collection("users").document(userId).collection("teams").document(teamName.toLowerCase(Locale.ROOT))
                    .set(teamMap)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully added")
                        val preferences: SharedPreferences =
                                activity!!.getSharedPreferences("com.jameschamberlain.footballteamtracker", MODE_PRIVATE)
                        val editor = preferences.edit()
                        editor.putString("team_name", teamName)
                        editor.apply()
                    }
                    .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }

            startActivity(Intent(activity, MainActivity::class.java))
        }
        binding.editTextField.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        binding.editTextField.addTextChangedListener(textWatcher)
        checkFieldForEmptyValues()

        return binding.root
    }

}