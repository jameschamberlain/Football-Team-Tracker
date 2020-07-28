package com.jameschamberlain.footballteamtracker.team

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jameschamberlain.footballteamtracker.FileUtils.writePlayersFile
import com.jameschamberlain.footballteamtracker.Player
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team.Companion.team
import com.jameschamberlain.footballteamtracker.databinding.FragmentPlayerDetailsBinding
import java.util.*

class PlayerDetailsFragment : Fragment() {

    private lateinit var binding: FragmentPlayerDetailsBinding

    private lateinit var player: Player

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        activity!!.findViewById<View>(R.id.nav_view).visibility = View.GONE
        val containerLayout = activity!!.findViewById<FrameLayout>(R.id.fragment_container)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(0, 0, 0, 0)
        containerLayout.layoutParams = params

        val data = this.arguments
        player = data!!.getParcelable("player")!!

        binding = FragmentPlayerDetailsBinding.inflate(layoutInflater)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""

        binding.nameTextView.text = player.name
        binding.goalsTextView.text = String.format(Locale.ENGLISH, "%d", player.goals)
        binding.assistsTextView.text = String.format(Locale.ENGLISH, "%d", player.assists)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.player_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // User chose the "Back" item, go back.
                val fm = activity!!.supportFragmentManager
                if (fm.backStackEntryCount > 0) {
                    fm.popBackStack()
                }
                true
            }
            R.id.action_delete -> {
                // User chose the "Delete" action, delete the fixture.
                MaterialAlertDialogBuilder(context)
                        .setTitle(getString(R.string.delete_this_player))
                        .setPositiveButton(getString(R.string.delete)) { _, _ ->
                            val players = team.players
                            players.remove(player)
                            // Sort fixtures.
                            players.sort()
                            // Write the update to a file.
                            writePlayersFile(players)
                            val fm = activity!!.supportFragmentManager
                            if (fm.backStackEntryCount > 0) {
                                fm.popBackStack()
                            }
                        }
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show()
                true
            }
            else ->                 // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
        }
    }
}