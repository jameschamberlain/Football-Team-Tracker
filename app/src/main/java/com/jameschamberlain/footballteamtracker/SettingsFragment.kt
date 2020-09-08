package com.jameschamberlain.footballteamtracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.firebase.ui.auth.AuthUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jameschamberlain.footballteamtracker.databinding.FragmentSettingsBinding
import com.jameschamberlain.footballteamtracker.objects.AccountType


private const val TAG = "SettingsFragment"

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Utils.hideBottomNav(requireActivity())

        setHasOptionsMenu(true)

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Settings"

        if (Utils.accountType == AccountType.ADMIN) {
            binding.teamLayout.visibility = View.GONE
            setupAccountSettings()
        }
        else {
            binding.accountLayout.visibility = View.GONE
        }
    }

    private fun setupAccountSettings() {
        binding.logOutTextView.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.log_out_dialog_title))
                    .setPositiveButton("Log out") { _, _ ->
                        AuthUI.getInstance()
                                .signOut(requireContext())
                                .addOnSuccessListener {
                                    Log.d(TAG, "Successfully logged user out")
                                    Toast.makeText(context, "Successfully logged out", Toast.LENGTH_SHORT).show()
                                    val preferences: SharedPreferences =
                                            requireActivity().getSharedPreferences("com.jameschamberlain.footballteamtracker", Context.MODE_PRIVATE)
                                    val editor = preferences.edit()
                                    editor.clear()
                                    editor.apply()
                                    val restartIntent = Intent(requireContext(), LauncherActivity::class.java)
                                    restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(restartIntent)
                                    requireActivity().finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error logging user out", e)
                                    Toast.makeText(context, "Failed to log out", Toast.LENGTH_SHORT).show()
                                }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
        }
    }

    override fun onStop() {
        super.onStop()
        Utils.showBottomNav(requireActivity())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // User chose the "Back" item, go back.
                NavHostFragment.findNavController(this@SettingsFragment).navigateUp()
                true
            }
            else ->                 // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
        }
    }

}