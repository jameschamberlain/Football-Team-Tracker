package com.jameschamberlain.footballteamtracker.fixtures

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixtureEditBinding
import com.jameschamberlain.footballteamtracker.objects.Fixture
import com.jameschamberlain.footballteamtracker.objects.FixtureResult
import com.jameschamberlain.footballteamtracker.objects.Score
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "EditFixtureFragment"

class EditFixtureFragment internal constructor() : Fragment() {


    private lateinit var binding: FragmentFixtureEditBinding

    private lateinit var newFixture: Fixture
    private lateinit var oldFixture: Fixture

    private lateinit var fixtureId: String

    private val calendar = Calendar.getInstance()

    private lateinit var teamName: String

    private var playerNames =  ArrayList<String>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val extras = this.arguments!!
        oldFixture = extras.getParcelable("fixture")!!
        newFixture = oldFixture.copyOf()
        fixtureId = extras.getString("id")!!

        binding = FragmentFixtureEditBinding.inflate(layoutInflater)

        teamName = Utils.getTeamNameTest()
        binding.homeTeamTextView.text = if (newFixture.isHomeGame) teamName else newFixture.opponent
        binding.awayTeamTextView.text = if (newFixture.isHomeGame) newFixture.opponent else teamName
        binding.scoreTextView.text = newFixture.score.toString()
        binding.timeTextView.text = newFixture.timeString()

        calendar.timeInMillis = newFixture.dateTime

        setupScoreButton()
        setupDate()
        setupTime()


        Utils.teamRef.collection("players").get()
                .addOnSuccessListener {
                    for (doc in it.documents) {
                        playerNames.add(doc.getString("name")!!)
                    }
                    val adapter: ArrayAdapter<String> = ArrayAdapter(context!!, R.layout.item_player, playerNames)
                    setupGoals(adapter)
                    setupAssists(adapter)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Get failed with ", e)
                }
//        val adapter: ArrayAdapter<String> = ArrayAdapter(context!!, R.layout.item_player, playerNames)
//        setupGoals(adapter)
//        setupAssists(adapter)

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
            if (newFixture.result == FixtureResult.UNPLAYED) {
                homeScorePicker.value = 0
                awayScorePicker.value = 0
            } else {
                homeScorePicker.value = newFixture.score.home
                awayScorePicker.value = newFixture.score.away
            }
            MaterialAlertDialogBuilder(v.context)
                    .setTitle("Update score:")
                    .setPositiveButton("Confirm") { _, _ ->
                        val newHomeScore = homeScorePicker.value
                        val newAwayScore = awayScorePicker.value
                        newFixture.score = Score(newHomeScore, newAwayScore)
                        binding.scoreTextView.text = newFixture.score.toString()
                    }
                    .setNegativeButton("cancel") { dialog, _ -> dialog.dismiss() }
                    .setView(view)
                    .show()
        }
    }

    private fun setupDate() {
        val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(newFixture.dateTime), ZoneId.systemDefault())
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
        val time = LocalDateTime.ofInstant(Instant.ofEpochMilli(newFixture.dateTime), ZoneId.systemDefault())
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
        val goalsAdapter = EditRecyclerAdapter(newFixture, true)
        binding.goalsRecyclerView.adapter = goalsAdapter
        binding.goalsRecyclerView.layoutManager = LinearLayoutManager(activity)

        /*
        Add a click listener for the goalscorers button so the goalscorers
        can be updated.
        An alert will appear with a list of the team's players.
         */
        val items = Array(playerNames.size) { "" }
        for (x in 0 until playerNames.size) {
            items[x] = playerNames[x]
        }
        binding.addGoalscorerButton.setOnClickListener { v ->
            MaterialAlertDialogBuilder(v.context)
                    .setTitle("Add a goalscorer:")
                    .setItems(items) { _, which ->
                        val name: String = adapter.getItem(which)!!
                        val newGoalscorers = newFixture.goalscorers
                        newGoalscorers.add(name)
                        newFixture.goalscorers = newGoalscorers
                        goalsAdapter.notifyDataSetChanged()
                    }
                    .show()
        }
    }

    private fun setupAssists(adapter: ArrayAdapter<String>) {
        val assistsAdapter = EditRecyclerAdapter(newFixture, false)
        binding.assistsRecyclerView.adapter = assistsAdapter
        binding.assistsRecyclerView.layoutManager = LinearLayoutManager(activity)

        /*
        Add a click listener for the assists button so the assists
        can be updated.
        An alert will appear with a list of the team's players.
         */
        val items = Array(playerNames.size) { "" }
        for (x in 0 until playerNames.size) {
            items[x] = playerNames[x]
        }
        binding.addAssistButton.setOnClickListener { v ->
            MaterialAlertDialogBuilder(v.context)
                    .setTitle("Add an assist:")
                    .setItems(items) { _, which ->
                        val name: String = adapter.getItem(which)!!
                        val newAssists = newFixture.assists
                        newAssists.add(name)
                        newFixture.assists = newAssists
                        assistsAdapter.notifyDataSetChanged()
                    }
                    .show()
        }
    }

    private fun setupConfirmButton() {
        binding.confirmButton.setOnClickListener {

            newFixture.goalscorers.sort()
            newFixture.assists.sort()

            for (scorer in oldFixture.goalscorers) {
                val playerId = scorer.toLowerCase(Locale.ROOT)
                Utils.teamRef.collection("players").document(playerId)
                        .update("goals", FieldValue.increment(-1))
            }
            for (scorer in oldFixture.assists) {
                val playerId = scorer.toLowerCase(Locale.ROOT)
                Utils.teamRef.collection("players").document(playerId)
                        .update("assists", FieldValue.increment(-1))
            }

            for (scorer in newFixture.goalscorers) {
                val playerId = scorer.toLowerCase(Locale.ROOT)
                Utils.teamRef.collection("players").document(playerId)
                        .update("goals", FieldValue.increment(1))
            }
            for (scorer in newFixture.assists) {
                val playerId = scorer.toLowerCase(Locale.ROOT)
                Utils.teamRef.collection("players").document(playerId)
                        .update("assists", FieldValue.increment(1))
            }

            Utils.teamRef.collection("fixtures").document(fixtureId)
                    .set(newFixture, SetOptions.merge())
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