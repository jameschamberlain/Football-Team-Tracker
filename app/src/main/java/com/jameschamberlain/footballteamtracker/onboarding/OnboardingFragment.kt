package com.jameschamberlain.footballteamtracker.onboarding

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
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jameschamberlain.footballteamtracker.MainActivity
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentSetupBinding
import com.jameschamberlain.footballteamtracker.data.AccountType

private const val TAG = "SetupFragment"

class OnboardingFragment : Fragment() {

    private var _binding: FragmentSetupBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var teamCode: String
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            checkFieldForEmptyValues()
        }
    }

    fun checkFieldForEmptyValues() {
        teamCode = binding.editTextField.text.toString()
        binding.continueButton.isEnabled = teamCode != ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentSetupBinding.inflate(inflater, container, false)

        val auth = FirebaseAuth.getInstance()
        auth.signInAnonymously()
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInAnonymously:success")
                        binding.editTextField.addTextChangedListener(textWatcher)
                        checkFieldForEmptyValues()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInAnonymously:failure", task.exception)
                        Toast.makeText(context, "Internet connection required for setup",
                                Toast.LENGTH_LONG).show()
                    }

                    // ...
                }

        binding.editTextField.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        binding.continueButton.setOnClickListener {
            teamCode = binding.editTextField.text.toString()
            Firebase.firestore.collection("teamCodes").document(teamCode).get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            val teamId = documentSnapshot.getString("teamId")!!

                            Firebase.firestore.collection("teams").document(teamId).get()
                                    .addOnSuccessListener { document ->
                                        if (document != null) {
                                            Log.d(TAG, "Team found")
                                            Toast.makeText(context, "Team found", Toast.LENGTH_SHORT).show()
                                            Utils.setupTeam(
                                                    AccountType.USER,
                                                    teamId,
                                                    document.getString("code")!!,
                                                    requireActivity()
                                            )
                                            val action = OnboardingFragmentDirections.actionOnboardingFragment2ToMainActivity()
                                            NavHostFragment
                                                    .findNavController(this@OnboardingFragment)
                                                    .navigate(action)
                                        } else {
                                            // Convert the whole Query Snapshot to a list
                                            // of objects directly! No need to fetch each
                                            // document.
                                            Log.d(TAG, "No such document")
                                            Toast.makeText(context, "No team detected", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "get failed with ", e)
                                        Toast.makeText(context, "Failed, try again", Toast.LENGTH_SHORT).show()
                                    }
                        }
                    }
        }

        binding.managerLoginButton.setOnClickListener {
            val action = OnboardingFragmentDirections.actionOnboardingFragment2ToManagerLoginFragment()
            NavHostFragment
                    .findNavController(this@OnboardingFragment)
                    .navigate(action)
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}