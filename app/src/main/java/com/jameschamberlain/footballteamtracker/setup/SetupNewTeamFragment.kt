package com.jameschamberlain.footballteamtracker.setup

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
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.databinding.FragmentSetupNewTeamBinding

private const val TAG = "SetupNewFragment"

class SetupNewTeamFragment : Fragment() {

    private lateinit var binding: FragmentSetupNewTeamBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentSetupNewTeamBinding.inflate(layoutInflater)


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

                val transaction = activity!!.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, SetupNewTeamFragment2())
                transaction.addToBackStack(null)
                transaction.commit()
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