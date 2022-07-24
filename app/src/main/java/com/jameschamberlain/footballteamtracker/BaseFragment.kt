package com.jameschamberlain.footballteamtracker

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class BaseFragment : Fragment() {

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_options_menu, menu)
    }

    fun completeTeamCodeAction() {
        val preferences: SharedPreferences =
                requireActivity().getSharedPreferences("com.jameschamberlain.footballteamtracker", Context.MODE_PRIVATE)
        val teamCode = preferences.getString("team_code", null)
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.team_code))
            setMessage(teamCode)
            setPositiveButton("Copy to clipboard") { _, _ ->
                val clipBoard: ClipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipBoard.setPrimaryClip(ClipData.newPlainText("team code", teamCode))
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("Dismiss", null)
            show()
        }
    }

}