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
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jameschamberlain.footballteamtracker.MainActivity
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentSetupNewTeamBinding
import com.jameschamberlain.footballteamtracker.objects.AccountType

private const val TAG = "SetupNewFragment"

class SetupNewTeamFragment : Fragment() {

    private var _binding: FragmentSetupNewTeamBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentSetupNewTeamBinding.inflate(inflater, container, false)


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
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser!!
                Log.d(TAG, "User successfully signed in with ID: ${user.uid}")
                Toast.makeText(context, "Sign-in complete", Toast.LENGTH_SHORT).show()
                checkForTeam()
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


    private fun checkForTeam() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
        Firebase.firestore.collection("teams").whereEqualTo("managerId", currentUserId).limit(1).get()
                .addOnSuccessListener { document ->
                    if (document != null && !document.isEmpty) {
                        Log.d(TAG, "Team found")
                        Toast.makeText(context, "Team found", Toast.LENGTH_SHORT).show()
                        Utils.setupTeam(
                                AccountType.ADMIN,
                                document.documents[0].id,
                                document.documents[0].getString("code")!!,
                                requireActivity()
                        )
                        startActivity(Intent(activity, MainActivity::class.java))
                    } else {
                        // Convert the whole Query Snapshot to a list
                        // of objects directly! No need to fetch each
                        // document.
                        Log.d(TAG, "No such document")
                        Toast.makeText(context, "No team detected", Toast.LENGTH_SHORT).show()
                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.nav_host_fragment, SetupNewTeamFragment2())
                        transaction.addToBackStack(null)
                        transaction.commit()
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