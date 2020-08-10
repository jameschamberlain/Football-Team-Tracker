package com.jameschamberlain.footballteamtracker.setup

import android.content.Intent
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
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentSetupNewTeam2Binding
import com.jameschamberlain.footballteamtracker.objects.AccountType
import kotlin.random.Random


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
            addTeamCode()

        }
        binding.editTextField.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        binding.editTextField.addTextChangedListener(textWatcher)
        checkFieldForEmptyValues()

        return binding.root
    }

    private fun addTeamCode() {
        val randomCode = String.format("%06d", Random.nextInt(999999))
        db.collection("teamCodes").document(randomCode).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        Log.w(TAG, "Document exists")
                        addTeamCode()
                    } else {
                        Log.d(TAG, "No such document")
                        val teamMap = hashMapOf("name" to teamName, "managerId" to userId, "code" to randomCode)
                        db.collection("teams")
                                .add(teamMap)
                                .addOnSuccessListener { documentRef ->
                                    Log.d(TAG, "DocumentSnapshot successfully added")
                                    val userNameMap = hashMapOf("teamId" to documentRef.id)
                                    db.collection("teamCodes").document(randomCode)
                                            .set(userNameMap)
                                            .addOnSuccessListener {
                                                Log.d(TAG, "DocumentSnapshot successfully added")
                                                Utils.setupTeamWithId(AccountType.ADMIN, documentRef.id, activity!!)
                                                startActivity(Intent(activity, MainActivity::class.java))
                                            }
                                            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
                                }
                                .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }

                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
    }

}