package com.jameschamberlain.footballteamtracker.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jameschamberlain.footballteamtracker.MainActivity
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentManagerLoginBinding
import com.jameschamberlain.footballteamtracker.data.AccountType

private const val TAG = "ManagerLoginFragment"

class ManagerLoginFragment : Fragment() {

    private var _binding: FragmentManagerLoginBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentManagerLoginBinding.inflate(inflater, container, false)

        binding.googleSignInButton.setOnClickListener {
            val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN)
        }

        binding.createNewTeamButton.setOnClickListener {
            val action = ManagerLoginFragmentDirections.actionManagerLoginFragmentToSetupNewTeamFragment()
            NavHostFragment
                    .findNavController(this@ManagerLoginFragment)
                    .navigate(action)
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
//            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                Toast.makeText(requireContext(), "Sign-in complete", Toast.LENGTH_SHORT).show()
                checkForTeam()
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...

                Toast.makeText(requireContext(), "Sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkForTeam() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
        Firebase.firestore.collection("teams").whereEqualTo("managerId", currentUserId).limit(1).get()
                .addOnSuccessListener { document ->
                    if (document != null && !document.isEmpty) {
                        Log.d(TAG, "Team found")
                        Utils.setupTeam(
                                AccountType.ADMIN,
                                document.documents[0].id,
                                document.documents[0].getString("code")!!,
                                requireActivity()
                        )
                        val action = ManagerLoginFragmentDirections.actionManagerLoginFragmentToMainActivity()
                        NavHostFragment
                                .findNavController(this@ManagerLoginFragment)
                                .navigate(action)
                    } else {
                        // Convert the whole Query Snapshot to a list
                        // of objects directly! No need to fetch each
                        // document.
                        Log.d(TAG, "No such document")
                        Toast.makeText(context, "No team detected", Toast.LENGTH_SHORT).show()
                        val action = ManagerLoginFragmentDirections.actionManagerLoginFragmentToSetupNewTeamFragment()
                        NavHostFragment
                                .findNavController(this@ManagerLoginFragment)
                                .navigate(action)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "get failed with ", e)
                    Toast.makeText(context, "Failed, try again", Toast.LENGTH_SHORT).show()
                }
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}