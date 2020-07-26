package com.jameschamberlain.footballteamtracker.team

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jameschamberlain.footballteamtracker.FileUtils.writeFixturesFile
import com.jameschamberlain.footballteamtracker.FileUtils.writePlayersFile
import com.jameschamberlain.footballteamtracker.FileUtils.writeTeamFile
import com.jameschamberlain.footballteamtracker.Player
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team.Companion.team
import com.jameschamberlain.footballteamtracker.databinding.FragmentTeamBinding

/**
 * A simple [Fragment] subclass.
 */
class TeamFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentTeamBinding

    private val maxNameLength = 15

    private lateinit var adapter: TeamRecyclerAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val navView: BottomNavigationView = activity!!.findViewById(R.id.nav_view)
        navView.menu.getItem(3).isChecked = true
        navView.menu.getItem(0).setIcon(R.drawable.ic_home_outline)
        navView.menu.getItem(1).setIcon(R.drawable.ic_calendar_outline)
        navView.menu.getItem(2).setIcon(R.drawable.ic_analytics_outline)
        navView.menu.getItem(3).setIcon(R.drawable.ic_strategy)
        activity!!.findViewById<View>(R.id.nav_view).visibility = View.VISIBLE
        val containerLayout = activity!!.findViewById<FrameLayout>(R.id.container)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        val pixels = 56 * context!!.resources.displayMetrics.density
        params.setMargins(0, 0, 0, pixels.toInt())
        containerLayout.layoutParams = params

        binding = FragmentTeamBinding.inflate(layoutInflater)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
        val players = team.players

        adapter = TeamRecyclerAdapter(players, this@TeamFragment)
        binding.playersRecyclerView.adapter = adapter
        binding.playersRecyclerView.layoutManager = LinearLayoutManager(activity)

        binding.fab.setOnClickListener(this)
        checkTeamHasPlayers()


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun checkTeamHasPlayers() {
        if (team.players.isEmpty()) {
            binding.noTeamLayout.visibility = View.VISIBLE
        } else {
            binding.noTeamLayout.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        val editText = EditText(v.context)
        editText.filters = arrayOf<InputFilter>(LengthFilter(maxNameLength))
        editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        editText.hint = getString(R.string.name)
        val scale = resources.displayMetrics.density
        val dpAsPixels = (20 * scale + 0.5f).toInt()
        val container = FrameLayout(context!!)
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, 0)
        editText.layoutParams = lp
        container.addView(editText)
        MaterialAlertDialogBuilder(context)
                .setTitle(getString(R.string.add_player))
                .setView(container)
                .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                    val input = editText.text.toString()
                    team.players.add(Player(input))
                    team.players.sort()
                    writePlayersFile(team.players)
                    checkTeamHasPlayers()
                    adapter.notifyDataSetChanged()
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
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