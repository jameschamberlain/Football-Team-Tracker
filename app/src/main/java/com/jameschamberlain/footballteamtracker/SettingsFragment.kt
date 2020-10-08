package com.jameschamberlain.footballteamtracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
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


private const val TAG = "SettingsFragment"

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Settings"

        setupAccountSettings()
        setupHelpAndFeedbackSettings()
    }

    private fun setupAccountSettings() {
        binding.logOutLayout.setOnClickListener {
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

    private fun setupHelpAndFeedbackSettings() {
        binding.contactDevLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.developer_email)))
            }
//            if (intent.resolveActivity(requireActivity().packageManager) != null)
            startActivity(intent)
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}