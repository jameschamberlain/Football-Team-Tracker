package com.jameschamberlain.footballteamtracker.fixtures

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jameschamberlain.footballteamtracker.FileUtils.writeFixturesFile
import com.jameschamberlain.footballteamtracker.FileUtils.writeTeamFile
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team.Companion.instance

/**
 * A simple [Fragment] subclass.
 */
class FixturesFragment : Fragment() {
    private val team = instance
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Select correct bottom nav item.
        val navView: BottomNavigationView = activity!!.findViewById(R.id.nav_view)
        navView.menu.getItem(1).isChecked = true
        navView.menu.getItem(0).setIcon(R.drawable.ic_home_outline)
        navView.menu.getItem(1).setIcon(R.drawable.ic_calendar)
        navView.menu.getItem(2).setIcon(R.drawable.ic_analytics_outline)
        navView.menu.getItem(3).setIcon(R.drawable.ic_strategy_outline)
        activity!!.findViewById<View>(R.id.nav_view).visibility = View.VISIBLE
        val containerLayout = activity!!.findViewById<FrameLayout>(R.id.container)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        val pixels = 56 * context!!.resources.displayMetrics.density
        params.setMargins(0, 0, 0, pixels.toInt())
        containerLayout.layoutParams = params
        val rootView = inflater.inflate(R.layout.fragment_fixtures, container, false)
        setHasOptionsMenu(true)
        val myToolbar: Toolbar = rootView.findViewById(R.id.toolbar)
        (activity as AppCompatActivity?)!!.setSupportActionBar(myToolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""


        // Create an {@link FixtureRecyclerAdapter}, whose data source is a list of {@link Fixture}s. The
        // adapter knows how to create list items for each item in the list.
        val adapter = FixtureRecyclerAdapter(activity, this@FixturesFragment)

        // Find the {@link RecyclerView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list layout file.
        val recyclerView: RecyclerView = rootView.findViewById(R.id.list)

        // Make the {@link RecyclerView} use the {@link FixtureRecyclerAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Fixture} in the list.
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        val fab: FloatingActionButton = rootView.findViewById(R.id.fab)
        fab.setOnClickListener { loadFragment(NewFixtureFragment()) }

        //Scroll item 2 to 20 pixels from the top
        val team = instance
        layoutManager.scrollToPositionWithOffset(team.gamesPlayed - 3, 0)
        val noFixturesLayout = rootView.findViewById<LinearLayout>(R.id.no_fixtures_layout)
        if (team.fixtures.isEmpty()) {
            noFixturesLayout.visibility = View.VISIBLE
        } else {
            noFixturesLayout.visibility = View.GONE
        }


        // Inflate the layout for this fragment.
        return rootView
    }

    /**
     *
     * Helper function for loading new fragments correctly.
     *
     * @param fragment The fragment to be loaded.
     */
    private fun loadFragment(fragment: Fragment) {
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_rename_team) {
            // User chose the "Rename Team" action, show a window to allow this.
            val alert = AlertDialog.Builder(context!!)
            val editText = EditText(context)
            editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            editText.setText(team.name)
            val scale = resources.displayMetrics.density
            val dpAsPixels = (20 * scale + 0.5f).toInt()
            val container = FrameLayout(context!!)
            val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, 0)
            editText.layoutParams = lp
            container.addView(editText)
            alert.setTitle("Rename your team:")
                    .setView(container)
                    .setPositiveButton("Confirm") { _, _ ->
                        val input = editText.text.toString()
                        for (fixture in team.fixtures) {
                            if (fixture.homeTeam == team.name) {
                                fixture.homeTeam = input
                            } else {
                                fixture.awayTeam = input
                            }
                        }
                        team.name = input
                        writeTeamFile(team.name)
                        writeFixturesFile(team.fixtures)
                    }
                    .setNegativeButton("Cancel", null)
            val dialog = alert.create()
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            dialog.show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}