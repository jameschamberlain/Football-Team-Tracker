package com.jameschamberlain.footballteamtracker

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class MenuFragment : Fragment() {

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_team_code -> {
                val preferences: SharedPreferences =
                        requireActivity().getSharedPreferences("com.jameschamberlain.footballteamtracker", Context.MODE_PRIVATE)
                val teamCode = preferences.getString("team_code", null)
                MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.team_code))
                        .setMessage(teamCode)
                        .setPositiveButton("Copy to clipboard") { _, _ ->
                            val clipBoard: ClipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipBoard.setPrimaryClip(ClipData.newPlainText("team code", teamCode))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Dismiss", null)
                        .show()
                true
            }
//            R.id.action_settings -> {
//                Toast.makeText(context, "Settings clicked", Toast.LENGTH_SHORT).show()
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}