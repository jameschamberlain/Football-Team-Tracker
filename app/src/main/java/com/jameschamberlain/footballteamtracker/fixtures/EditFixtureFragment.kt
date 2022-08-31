package com.jameschamberlain.footballteamtracker.fixtures

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

        calendar.timeInMillis = originalFixture.dateTime

    }

    private fun setupUi() {
        binding.apply {
            homeTeamTextView.text = if (editedFixture.isHomeGame) Utils.getTeamName(requireActivity()) else editedFixture.opponent
            awayTeamTextView.text = if (editedFixture.isHomeGame) editedFixture.opponent else Utils.getTeamName(requireActivity())
            scoreTextView.text = editedFixture.score.toString()
        }

        calendar.timeInMillis = editedFixture.dateTime

        setupScoreButton()
        setupDate()
        setupTime()
        setupVenue()

        Utils.getTeamReference(requireActivity()).collection("players").get()
                .addOnSuccessListener {
                    it.documents.forEach { doc ->
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
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(calendar.timeInMillis)
                .build()

        datePicker.addOnPositiveButtonClickListener {
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            calendar.timeInMillis = it
            calendar[Calendar.HOUR_OF_DAY] = hour
            calendar[Calendar.MINUTE] = minute
            updateDateLabel()
        }
        binding.dateTextView.setOnClickListener {
            datePicker.show(requireActivity().supportFragmentManager, "datePicker")
        }
        updateDateLabel()
    }

    private fun updateDateLabel() {
        val formatter = DateTimeFormatter.ofPattern("E, d MMM yyyy")
        binding.dateTextView.text = formatter.format(LocalDateTime.ofInstant(calendar.toInstant(), calendar.timeZone.toZoneId()))
    }

    private fun setupTime() {
        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Select time")
                .setHour(calendar[Calendar.HOUR_OF_DAY])
                .setMinute(calendar[Calendar.MINUTE])
                .build()

        timePicker.addOnPositiveButtonClickListener {
            calendar[Calendar.HOUR_OF_DAY] = timePicker.hour
            calendar[Calendar.MINUTE] = timePicker.minute
            updateTimeLabel()
        }
        binding.timeTextView.setOnClickListener {
            timePicker.show(requireActivity().supportFragmentManager, "timePicker")
        }
        updateTimeLabel()
    }


    private fun setupVenue() {
        binding.venueTextView.text = if (originalFixture.venue != null) originalFixture.venue else "N/A"
    }

    private fun updateTimeLabel() {
        val formatter = DateTimeFormatter.ofPattern("kk:mm")
        binding.timeTextView.text = formatter.format(LocalDateTime.ofInstant(calendar.toInstant(), calendar.timeZone.toZoneId()))
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


    private fun updatePlayersStats() {
        playerNames.forEach { player ->
            val goalChange =
                (Collections.frequency(editedFixture.goalscorers, player) -
                        Collections.frequency(originalFixture.goalscorers, player)).toDouble()
            val assistChange =
                (Collections.frequency(editedFixture.assists, player) -
                        Collections.frequency(originalFixture.assists, player)).toDouble()

            if (goalChange != 0.0 || assistChange != 0.0)
                Utils.getTeamReference(requireActivity()).collection("players").document(player.lowercase(Locale.ROOT))
                    .update(mapOf(
                        "goals" to FieldValue.increment(goalChange),
                        "assists" to FieldValue.increment(assistChange)
                    ))
        }
    }

    private fun setupConfirmButton() {
        binding.confirmButton.setOnClickListener {
            editedFixture.goalscorers.sort()
            editedFixture.assists.sort()
            editedFixture.dateTime = calendar.timeInMillis

            updatePlayersStats()

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