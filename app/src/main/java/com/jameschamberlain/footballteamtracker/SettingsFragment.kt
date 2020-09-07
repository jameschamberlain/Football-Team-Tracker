package com.jameschamberlain.footballteamtracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.firebase.ui.auth.AuthUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jameschamberlain.footballteamtracker.objects.AccountType


private const val TAG = "SettingsFragment"

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        Utils.hideBottomNav(requireActivity())

        if (Utils.accountType == AccountType.ADMIN) {
            preferenceScreen.removePreference(preferenceManager.findPreference("team_category"))
            setupAccountSettings()
        }
        else {
            preferenceScreen.removePreference(preferenceManager.findPreference("account_category"))
        }
    }

    private fun setupAccountSettings() {
        preferenceManager.findPreference<Preference>("log_out")?.setOnPreferenceClickListener {
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
            true
        }
    }

    override fun onStop() {
        super.onStop()
        Utils.showBottomNav(requireActivity())
    }

}