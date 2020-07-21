package com.jameschamberlain.footballteamtracker.team

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.jameschamberlain.footballteamtracker.FileUtils.writePlayersFile
import com.jameschamberlain.footballteamtracker.Player
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team.Companion.instance
import java.util.*

class PlayerDetailsFragment : Fragment() {
    private lateinit var player: Player
    private lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val data = this.arguments
        player = data!!.getParcelable("player")!!
        rootView = inflater.inflate(R.layout.fragment_player_details, container, false)
        setHasOptionsMenu(true)
        val myToolbar: Toolbar = rootView.findViewById(R.id.toolbar)
        (activity as AppCompatActivity?)!!.setSupportActionBar(myToolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""

        // Set the name of the player.
        val nameTextView = rootView.findViewById<TextView>(R.id.name_text_view)
        nameTextView.text = player.name

        // Set the number of goals the player has.
        val goalsTextView = rootView.findViewById<TextView>(R.id.goals_text_view)
        goalsTextView.text = String.format(Locale.ENGLISH, "%d", player.goals)

        // Set the number of assists the player has.
        val assistsTextView = rootView.findViewById<TextView>(R.id.assists_text_view)
        assistsTextView.text = String.format(Locale.ENGLISH, "%d", player.assists)

        // Inflate the layout for this fragment
        return rootView
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
                val alert = AlertDialog.Builder(context)
                alert.setTitle("Delete player?")
                        .setMessage("Are you sure you would like to delete this player?")
                        .setPositiveButton("Yes") { _, _ ->
                            val players = instance.players
                            players.remove(player)
                            // Sort fixtures.
                            players.sort()
                            // Write the update to a file.
                            writePlayersFile(players)
                            val fm = activity!!.supportFragmentManager
                            if (fm.backStackEntryCount > 0) {
                                fm.popBackStackImmediate()
                            }
                        }
                        .setNegativeButton("No", null)
                val dialog = alert.create()
                dialog.show()
                true
            }
            else ->                 // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
        }
    }
}