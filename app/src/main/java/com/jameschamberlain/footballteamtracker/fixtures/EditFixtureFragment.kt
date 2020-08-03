package com.jameschamberlain.footballteamtracker.fixtures

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team.Companion.team
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixtureEditBinding
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "EditFixtureFragment"

class EditFixtureFragment internal constructor() : Fragment() {


    private lateinit var binding: FragmentFixtureEditBinding

    private lateinit var fixture: Fixture

    private lateinit var fixtureId: String

    private val calendar = Calendar.getInstance()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val extras = this.arguments!!
        fixture = extras.getParcelable("fixture")!!
        fixtureId = extras.getString("id")!!

        binding = FragmentFixtureEditBinding.inflate(layoutInflater)

        val preferences: SharedPreferences = activity!!.getSharedPreferences("com.jameschamberlain.footballteamtracker", Context.MODE_PRIVATE)
        val teamName = preferences.getString("team_name", null)!!
        binding.homeTeamTextView.text = if (fixture.isHomeGame) teamName else fixture.opponent
        binding.awayTeamTextView.text = if (fixture.isHomeGame) fixture.opponent else teamName
        binding.scoreTextView.text = fixture.score.toString()
        binding.timeTextView.text = fixture.timeString()

        calendar.timeInMillis = fixture.dateTime

        setupScoreButton()
        setupDate()
        setupTime()

        val playerNames = ArrayList<String>()
//        for (player in team.players) {
//            playerNames.add(player.name)
//        }
        val adapter: ArrayAdapter<String> = ArrayAdapter(context!!, R.layout.item_player, playerNames)
        setupGoals(adapter)
        setupAssists(adapter)

        setupCancelButton()
        setupConfirmButton()

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
        val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(fixture.dateTime), ZoneId.systemDefault())
        binding.dateTextView.setOnClickListener {
            DatePickerDialog(
                    context!!,
                    OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        calendar[Calendar.YEAR] = year
                        calendar[Calendar.MONTH] = monthOfYear
                        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                        updateDateLabel()
                    },
                    date.year,
                    date.monthValue - 1,
                    date.dayOfMonth)
                    .show()
        }
        updateDateLabel()
    }

    private fun updateDateLabel() {
        val sdf = SimpleDateFormat("E, d MMM yyyy", Locale.UK)
        binding.dateTextView.text = sdf.format(calendar.time)
    }

    private fun setupTime() {
        val time = LocalDateTime.ofInstant(Instant.ofEpochMilli(fixture.dateTime), ZoneId.systemDefault())
        binding.timeTextView.setOnClickListener {
            TimePickerDialog(
                    context,
                    OnTimeSetListener { _, hourOfDay, minute ->
                        calendar[Calendar.HOUR_OF_DAY] = hourOfDay
                        calendar[Calendar.MINUTE] = minute
                        updateTimeLabel()
                    },
                    time.hour,
                    time.minute,
                    true).show()
        }
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

    private fun setupConfirmButton() {
        binding.confirmButton.setOnClickListener {

            fixture.goalscorers.sort()

            fixture.assists.sort()

//            team.updateTeamStats()
//            team.updatePlayerStats()
            val userId = FirebaseAuth.getInstance().currentUser?.uid!!
            val preferences: SharedPreferences = activity!!.getSharedPreferences("com.jameschamberlain.footballteamtracker", Context.MODE_PRIVATE)
            val teamName = preferences.getString("team_name", null)!!
            FirebaseFirestore.getInstance().collection("users")
                    .document(userId)
                    .collection("teams")
                    .document(teamName.toLowerCase(Locale.ROOT))
                    .collection("fixtures")
                    .document(fixtureId)
                    .set(fixture, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                        val fm = activity!!.supportFragmentManager
                        if (fm.backStackEntryCount > 0) {
                            fm.popBackStack()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error deleting document", e)
                        Toast.makeText(activity, "Error updating document", Toast.LENGTH_SHORT).show()
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