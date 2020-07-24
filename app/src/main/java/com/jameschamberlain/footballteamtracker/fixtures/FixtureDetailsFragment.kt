package com.jameschamberlain.footballteamtracker.fixtures

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jameschamberlain.footballteamtracker.FileUtils.writeFixturesFile
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team.Companion.team
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixtureDetailsBinding
import java.util.*

class FixtureDetailsFragment : Fragment() {
    /**
     * The selected fixture.
     */
    private lateinit var fixture: Fixture

    /**
     * The id of the selected fixture.
     */
    private var fixtureId = 0

    /**
     * The root view of the layout.
     */
    private lateinit var binding: FragmentFixtureDetailsBinding

    /**
     * A list of name of the team's players.
     */
    private val playerNames = ArrayList<String>()

    /**
     * Adapter for the list of goalscorers.
     */
    private lateinit var goalsAdapter: SimpleRecyclerAdapter

    /**
     * Adapter for the list of assists.
     */
    private lateinit var assistsAdapter: SimpleRecyclerAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity!!.findViewById<View>(R.id.nav_view).visibility = View.GONE
        val containerLayout = activity!!.findViewById<FrameLayout>(R.id.container)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(0, 0, 0, 0)
        containerLayout.layoutParams = params
        val data = this.arguments
        if (data != null) {
            fixture = data.getParcelable("fixture")!!
        }
        fixtureId = team.fixtures.indexOf(fixture)

        binding = FragmentFixtureDetailsBinding.inflate(layoutInflater)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        for (player in team.players) {
            playerNames.add(player.name)
        }

        binding.homeTeamTextView.text = fixture.homeTeam
        binding.scoreTextView.text = fixture.score.toString()
        binding.awayTeamTextView.text = fixture.awayTeam
        binding.dateTextView.text = fixture.extendedDateString

        binding.timeTextView.text = fixture.timeString
        setupGoals()
        setupAssists()
        return binding.root
    }

    /**
     * Sets up the list of goalscorers.
     */
    private fun setupGoals() {
        goalsAdapter = SimpleRecyclerAdapter(fixture.goalscorers, true, context!!)
        binding.goalsRecyclerView.adapter = goalsAdapter
        binding.goalsRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    /**
     * Sets up the list of assists.
     */
    private fun setupAssists() {
        assistsAdapter = SimpleRecyclerAdapter(fixture.assists, false, context!!)
        binding.assistsRecyclerView.adapter = assistsAdapter
        binding.assistsRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    /**
     *
     * Helper function for loading new fragments correctly.
     *
     * @param fragment The fragment to be loaded.
     */
    private fun loadFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putParcelable("fixture", fixture.copyOf())
        // set arguments
        fragment.arguments = bundle
        // load fragment
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fixture_details_menu, menu)
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
            R.id.action_edit -> {
                // User chose the "Edit" action, move to the edit page.
                loadFragment(EditFixtureFragment())
                true
            }
            R.id.action_delete -> {
                // User chose the "Delete" action, delete the fixture.
                MaterialAlertDialogBuilder(context, R.style.CustomDialog)
                        .setTitle("Delete this fixture?")
                        .setPositiveButton("Delete") { _, _ ->
                            val fixtures = team.fixtures
                            fixtures.removeAt(fixtureId)
                            // Sort fixtures.
                            fixtures.sort()
                            // Update team stats.
                            team.updateTeamStats()
                            team.updatePlayerStats()
                            // Write the update to a file.
                            writeFixturesFile(fixtures)
                            val fm = activity!!.supportFragmentManager
                            if (fm.backStackEntryCount > 0) {
                                fm.popBackStack()
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                true
            }
            else ->                 // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
        }
    }
}