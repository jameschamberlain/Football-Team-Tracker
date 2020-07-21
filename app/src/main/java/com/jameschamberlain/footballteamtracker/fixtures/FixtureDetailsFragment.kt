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
import com.jameschamberlain.footballteamtracker.FileUtils.writeFixturesFile
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team.Companion.instance
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
    private lateinit var rootView: View

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
        fixtureId = instance.fixtures.indexOf(fixture)
        rootView = inflater.inflate(R.layout.fragment_fixture_details, container, false)
        setHasOptionsMenu(true)
        val myToolbar: Toolbar = rootView.findViewById(R.id.toolbar)
        (activity as AppCompatActivity?)!!.setSupportActionBar(myToolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        for (player in instance.players) {
            playerNames.add(player.name)
        }

        // Set the name of the home team.
        val homeTeamTextView = rootView.findViewById<TextView>(R.id.home_team_text_view)
        homeTeamTextView.text = fixture.homeTeam

        // Set the score of the fixture.
        val scoreTextView = rootView.findViewById<TextView>(R.id.score_text_view)
        scoreTextView.text = fixture.score.toString()

        // Set the name of the away team.
        val awayTextView = rootView.findViewById<TextView>(R.id.away_team_text_view)
        awayTextView.text = fixture.awayTeam

        // Set the date.
        val dateTextView = rootView.findViewById<TextView>(R.id.date_text_view)
        dateTextView.text = fixture.extendedDateString

        // Set the time.
        val timeTextView = rootView.findViewById<TextView>(R.id.time_text_view)
        timeTextView.text = fixture.timeString
        setupGoals()
        setupAssists()
        return rootView
    }

    /**
     * Sets up the list of goalscorers.
     */
    private fun setupGoals() {
        // Create an {@link TeamAdapter}, whose data source is a list of {@link Player}s. The
        // adapter knows how to create list items for each item in the list.
        goalsAdapter = SimpleRecyclerAdapter(fixture.goalscorers, true, context!!)

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list layout file.
        val recyclerView: RecyclerView = rootView.findViewById(R.id.goals_list)


        // Make the {@link ListView} use the {@link TeamAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Fixture} in the list.
        recyclerView.adapter = goalsAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    /**
     * Sets up the list of assists.
     */
    private fun setupAssists() {
        // Create an {@link SimpleRecyclerAdapter}, whose data source is a list of {@link Player}s. The
        // adapter knows how to create list items for each item in the list.
        assistsAdapter = SimpleRecyclerAdapter(fixture.assists, false, context!!)

        // Find the {@link RecyclerView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list layout file.
        val recyclerView: RecyclerView = rootView.findViewById(R.id.assists_list)


        // Make the {@link RecyclerView} use the {@link SimpleRecyclerAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Fixture} in the list.
        recyclerView.adapter = assistsAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
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
                val alert = AlertDialog.Builder(context)
                alert.setTitle("Delete fixture?")
                        .setMessage("Are you sure you would like to delete this fixture?")
                        .setPositiveButton("Yes") { _, _ ->
                            val fixtures = instance.fixtures
                            fixtures.removeAt(fixtureId)
                            // Sort fixtures.
                            fixtures.sort()
                            // Update team stats.
                            instance.updateTeamStats()
                            instance.updatePlayerStats()
                            // Write the update to a file.
                            writeFixturesFile(fixtures)
                            val fm = activity!!.supportFragmentManager
                            if (fm.backStackEntryCount > 0) {
                                fm.popBackStack()
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