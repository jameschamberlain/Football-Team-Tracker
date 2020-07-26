package com.jameschamberlain.footballteamtracker.fixtures

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jameschamberlain.footballteamtracker.FileUtils.writeFixturesFile
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team.Companion.team
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixtureEditBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class EditFixtureFragment internal constructor() : Fragment() {


    private lateinit var binding: FragmentFixtureEditBinding

    private lateinit var fixture: Fixture

    private val calendar = Calendar.getInstance()

    private var date = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = monthOfYear
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        updateDateLabel()
    }

    private var time = OnTimeSetListener { _, hourOfDay, minute ->
        calendar[Calendar.HOUR_OF_DAY] = hourOfDay
        calendar[Calendar.MINUTE] = minute
        updateTimeLabel()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val bundle = this.arguments
        fixture = bundle?.getParcelable("fixture")!!
        binding = FragmentFixtureEditBinding.inflate(layoutInflater)

        binding.homeTeamTextView.text = fixture.homeTeam
        binding.scoreTextView.text = fixture.score.toString()
        binding.awayTeamTextView.text = fixture.awayTeam
        binding.timeTextView.text = fixture.timeString

        setupScoreButton()
        setupDate()
        setupTime()

        val playerNames = ArrayList<String>()
        for (player in team.players) {
            playerNames.add(player.name)
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter(context!!, R.layout.item_player, playerNames)
        setupGoals(adapter)
        setupAssists(adapter)

        setupCancelButton()
        setupConfirmButton(team.fixtures[team.fixtures.indexOf(fixture)])

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupScoreButton() {
        /*
    Add a click listener for the score button so the score can
    be updated.
    An alert will appear with two spinners: one for the home team
    and one for the away team.
     */
        binding.updateScoreButton.setOnClickListener { v ->
            val view = layoutInflater.inflate(R.layout.dialog_score, null)
            val homeScorePicker = view.findViewById<NumberPicker>(R.id.home_score_picker)
            homeScorePicker.minValue = 0
            homeScorePicker.maxValue = 20
            val awayScorePicker = view.findViewById<NumberPicker>(R.id.away_score_picker)
            awayScorePicker.minValue = 0
            awayScorePicker.maxValue = 20
            if (fixture.result == FixtureResult.UNPLAYED) {
                homeScorePicker.value = 0
                awayScorePicker.value = 0
            } else {
                homeScorePicker.value = fixture.score.home
                awayScorePicker.value = fixture.score.away
            }
            MaterialAlertDialogBuilder(v.context)
                    .setTitle("Update score:")
                    .setPositiveButton("Confirm") { _, _ ->
                        val newHomeScore = homeScorePicker.value
                        val newAwayScore = awayScorePicker.value
                        fixture.score = Score(newHomeScore, newAwayScore)
                        binding.scoreTextView.text = fixture.score.toString()
                    }
                    .setNegativeButton("cancel") { dialog, _ -> dialog.dismiss() }
                    .setView(view)
                    .show()
        }
    }

    private fun setupDate() {
        binding.dateTextView.setOnClickListener {
            DatePickerDialog(context!!, date,
                    fixture.dateTime.year,
                    fixture.dateTime.monthValue - 1,
                    fixture.dateTime.dayOfMonth)
                    .show()
        }
        calendar[Calendar.YEAR] = fixture.dateTime.year
        calendar[Calendar.MONTH] = fixture.dateTime.monthValue - 1
        calendar[Calendar.DAY_OF_MONTH] = fixture.dateTime.dayOfMonth
        updateDateLabel()
    }

    private fun updateDateLabel() {
        val sdf = SimpleDateFormat("E, d MMM yyyy", Locale.UK)
        binding.dateTextView.text = sdf.format(calendar.time)
    }

    private fun setupTime() {
        binding.timeTextView.setOnClickListener {
            TimePickerDialog(context, time,
                    fixture.dateTime.hour,
                    fixture.dateTime.minute,
                    true).show()
        }
        calendar[Calendar.HOUR_OF_DAY] = fixture.dateTime.hour
        calendar[Calendar.MINUTE] = fixture.dateTime.minute
        updateTimeLabel()
    }

    private fun updateTimeLabel() {
        val sdf = SimpleDateFormat("kk:mm", Locale.UK)
        binding.timeTextView.text = sdf.format(calendar.time)
    }

    private fun setupGoals(adapter: ArrayAdapter<String>) {
        val goalsAdapter = EditRecyclerAdapter(fixture, true)
        binding.goalsRecyclerView.adapter = goalsAdapter
        binding.goalsRecyclerView.layoutManager = LinearLayoutManager(activity)

        /*
        Add a click listener for the goalscorers button so the goalscorers
        can be updated.
        An alert will appear with a list of the team's players.
         */
        val items = Array(team.players.size) { "" }
        for (x in 0 until team.players.size) {
            items[x] = team.players[x].name
        }
        binding.addGoalscorerButton.setOnClickListener { v ->
            MaterialAlertDialogBuilder(v.context)
                    .setTitle("Add a goalscorer:")
                    .setItems(items) { _, which ->
                        val name: String = adapter.getItem(which)!!
                        val newGoalscorers = fixture.goalscorers
                        newGoalscorers.add(name)
                        fixture.goalscorers = newGoalscorers
                        goalsAdapter.notifyDataSetChanged()
                    }
                    .show()
        }
    }

    private fun setupAssists(adapter: ArrayAdapter<String>) {
        val assistsAdapter = EditRecyclerAdapter(fixture, false)
        binding.assistsRecyclerView.adapter = assistsAdapter
        binding.assistsRecyclerView.layoutManager = LinearLayoutManager(activity)

        /*
        Add a click listener for the assists button so the assists
        can be updated.
        An alert will appear with a list of the team's players.
         */
        val items = Array(team.players.size) { "" }
        for (x in 0 until team.players.size) {
            items[x] = team.players[x].name
        }
        binding.addAssistButton.setOnClickListener { v ->
            MaterialAlertDialogBuilder(v.context)
                    .setTitle("Add an assist:")
                    .setItems(items) { _, which ->
                        val name: String = adapter.getItem(which)!!
                        val newAssists = fixture.assists
                        newAssists.add(name)
                        fixture.assists = newAssists
                        assistsAdapter.notifyDataSetChanged()
                    }
                    .show()
        }
    }

    private fun setupConfirmButton(originalFixture: Fixture) {
        binding.confirmButton.setOnClickListener {
            originalFixture.score = fixture.score
            // Get date and time
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH] + 1
            val day = calendar[Calendar.DAY_OF_MONTH]
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            originalFixture.dateTime = LocalDateTime.of(year, month, day, hour, minute)

            fixture.goalscorers.sort()
            originalFixture.goalscorers = fixture.goalscorers

            fixture.assists.sort()
            originalFixture.assists = fixture.assists

            team.fixtures.sort()

            team.updateTeamStats()
            team.updatePlayerStats()

            writeFixturesFile(team.fixtures)
            // Return to the previous screen.
            val fragmentManager = activity!!.supportFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack()
            }
        }
    }

    private fun setupCancelButton() {
        binding.cancelButton.setOnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack()
            }
        }
    }
}