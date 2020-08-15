package com.jameschamberlain.footballteamtracker

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
                MaterialAlertDialogBuilder(context)
                        .setTitle(getString(R.string.team_code))
                        .setMessage("274883")
                        .setPositiveButton("Copy to clipboard") { _, _ ->

                        }
                        .setNegativeButton("Dismiss", null)
                        .show()
                true
            }
            R.id.action_settings -> {
                Toast.makeText(context, "Settings clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}