package com.jameschamberlain.footballteamtracker.fixtures

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.adapters.EditFixtureStatAdapter
import com.jameschamberlain.footballteamtracker.data.Fixture
import com.jameschamberlain.footballteamtracker.data.FixtureResult
import com.jameschamberlain.footballteamtracker.data.Score
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixtureEditBinding
import com.jameschamberlain.footballteamtracker.viewmodels.FixturesViewModel
import com.jameschamberlain.footballteamtracker.viewmodels.FixturesViewModelFactory
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "EditFixtureFragment"

class EditFixtureFragment internal constructor() : Fragment() {


    private var _binding: FragmentFixtureEditBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var editedFixture: Fixture
    private lateinit var originalFixture: Fixture

    private lateinit var fixtureId: String

    private val calendar = Calendar.getInstance()

    private var playerNames = ArrayList<String>()

    private val model: FixturesViewModel by activityViewModels { FixturesViewModelFactory(Utils.getTeamReference(requireActivity())) }

    private val args: EditFixtureFragmentArgs by navArgs()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFixtureEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fixtureId = args.fixtureId

        originalFixture = model.getSelectedFixture().value!!
        editedFixture = originalFixture.copyOf()
        setupUi()

    }

    private fun setupUi() {
        binding.apply {
            homeTeamTextView.text = if (editedFixture.isHomeGame) Utils.getTeamName(requireActivity()) else editedFixture.opponent
            awayTeamTextView.text = if (editedFixture.isHomeGame) editedFixture.opponent else Utils.getTeamName(requireActivity())
            scoreTextView.text = editedFixture.score.toString()
            timeTextView.text = editedFixture.timeString()
        }

        calendar.timeInMillis = editedFixture.dateTime

        setupScoreButton()
        setupDate()
        setupTime()

        Utils.getTeamReference(requireActivity()).collection("players").get()
                .addOnSuccessListener {
                    for (doc in it.documents) {
                        playerNames.add(doc.getString("name")!!)
                    }
                    val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.item_player, playerNames)
                    setupGoals(adapter)
                    setupAssists(adapter)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Get failed with ", e)
                }

        setupCancelButton()
        setupConfirmButton()
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
            val homeScorePicker = view.findViewById<NumberPicker>(R.id.home_score_picker).apply {
                minValue = 0
                maxValue = 20
                value = if (editedFixture.result == FixtureResult.UNPLAYED) 0 else editedFixture.score.home
            }
            val awayScorePicker = view.findViewById<NumberPicker>(R.id.away_score_picker).apply {
                minValue = 0
                maxValue = 20
                value = if (editedFixture.result == FixtureResult.UNPLAYED) 0 else editedFixture.score.away
            }
            MaterialAlertDialogBuilder(v.context)
                    .setTitle("Update score:")
                    .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                        val newHomeScore = homeScorePicker.value
                        val newAwayScore = awayScorePicker.value
                        editedFixture.score = Score(newHomeScore, newAwayScore)
                        binding.scoreTextView.text = editedFixture.score.toString()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                    .setView(view)
                    .show()
        }
    }

    private fun setupDate() {
        val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(editedFixture.dateTime), ZoneId.systemDefault())
        binding.dateTextView.setOnClickListener {
            DatePickerDialog(
                    requireContext(),
                    { _, year, monthOfYear, dayOfMonth ->
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
        val time = LocalDateTime.ofInstant(Instant.ofEpochMilli(editedFixture.dateTime), ZoneId.systemDefault())
        binding.timeTextView.setOnClickListener {
            TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
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
        val goalsAdapter = EditFixtureStatAdapter(editedFixture, true)
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
                        val newGoalscorers = editedFixture.goalscorers
                        newGoalscorers.add(name)
                        editedFixture.goalscorers = newGoalscorers
                        goalsAdapter.notifyDataSetChanged()
                    }
                    .show()
        }
    }

    private fun setupAssists(adapter: ArrayAdapter<String>) {
        val assistsAdapter = EditFixtureStatAdapter(editedFixture, false)
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
                        val newAssists = editedFixture.assists
                        newAssists.add(name)
                        editedFixture.assists = newAssists
                        assistsAdapter.notifyDataSetChanged()
                    }
                    .show()
        }
    }


    private fun updatePlayersStats(playerList: ArrayList<String>, field: String, updateVal: Double) {
        for (player in playerList) {
            val playerId = player.lowercase(Locale.ROOT)
            Utils.getTeamReference(requireActivity()).collection("players").document(playerId)
                    .update(field, FieldValue.increment(updateVal))
        }
    }

    private fun setupConfirmButton() {
        binding.confirmButton.setOnClickListener {
            editedFixture.goalscorers.sort()
            editedFixture.assists.sort()
            editedFixture.dateTime = calendar.timeInMillis

            updatePlayersStats(originalFixture.goalscorers, "goals", -1.0)
            updatePlayersStats(originalFixture.assists, "assists", -1.0)
            updatePlayersStats(editedFixture.goalscorers, "goals", 1.0)
            updatePlayersStats(editedFixture.assists, "assists", 1.0)

            Utils.getTeamReference(requireActivity()).collection("fixtures").document(fixtureId)
                    .set(editedFixture, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                        model.selectFixture(editedFixture)
                        NavHostFragment.findNavController(this@EditFixtureFragment).navigateUp()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error deleting document", e)
                        Toast.makeText(activity, "Failed to edit fixture, try again", Toast.LENGTH_SHORT).show()
                    }
        }
    }

    private fun setupCancelButton() {
        binding.cancelButton.setOnClickListener {
            NavHostFragment.findNavController(this@EditFixtureFragment).navigateUp()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}