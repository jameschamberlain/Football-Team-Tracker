package com.jameschamberlain.footballteamtracker.fixtures

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jameschamberlain.footballteamtracker.FileUtils.writeFixturesFile
import com.jameschamberlain.footballteamtracker.FileUtils.writeTeamFile
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team.Companion.team
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixturesBinding

/**
 * A simple [Fragment] subclass.
 */
class FixturesFragment : Fragment() {


    private lateinit var binding: FragmentFixturesBinding


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
        binding = FragmentFixturesBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""


        val adapter = FixtureRecyclerAdapter(activity, this@FixturesFragment)
        binding.fixturesRecyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(activity)
        binding.fixturesRecyclerView.layoutManager = layoutManager
        binding.fab.setOnClickListener { loadFragment(NewFixtureFragment()) }

        //Scroll item 2 to 20 pixels from the top
        layoutManager.scrollToPositionWithOffset(team.gamesPlayed - 3, 0)
        binding.noFixturesLayout.visibility =
                if (team.fixtures.isEmpty()) View.VISIBLE else View.GONE

        // Inflate the layout for this fragment.
        return binding.root
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
            MaterialAlertDialogBuilder(context)
                    .setTitle(getString(R.string.rename_your_team))
                    .setView(container)
                    .setPositiveButton(getString(R.string.confirm)) { _, _ ->
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
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}