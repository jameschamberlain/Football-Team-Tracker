package com.jameschamberlain.footballteamtracker.fixtures

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jameschamberlain.footballteamtracker.FileUtils.writeFixturesFile
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team.Companion.instance
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class EditFixtureFragment internal constructor() : Fragment() {
    private lateinit var fixture: Fixture

    /**
     * A list of name of the team's players.
     */
    private val playerNames = ArrayList<String>()
    private lateinit var rootView: View
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var scoreTextView: TextView
    private var fixtureId = 0
    private lateinit var goalsAdapter: EditRecyclerAdapter
    private lateinit var assistsAdapter: EditRecyclerAdapter
    private lateinit var dateTextView: TextView
    private lateinit var timeTextView: TextView
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
        if (bundle != null) {
            fixture = bundle.getParcelable("fixture")!!
        }
        fixtureId = instance.fixtures.indexOf(fixture)
        rootView = inflater.inflate(R.layout.fragment_fixture_edit, container, false)
        for (player in instance.players) {
            playerNames.add(player.name)
        }
        adapter = ArrayAdapter(context!!, R.layout.item_player, playerNames)

        // Set the name of the home team.
        val homeTeamTextView = rootView.findViewById<TextView>(R.id.home_team_text_view)
        homeTeamTextView.text = fixture.homeTeam

        // Set the score of the fixture.
        scoreTextView = rootView.findViewById(R.id.score_text_view)
        scoreTextView.text = fixture.score.toString()

        // Set the name of the away team.
        val awayTextView = rootView.findViewById<TextView>(R.id.away_team_text_view)
        awayTextView.text = fixture.awayTeam

        // Set the time.
        val timeTextView = rootView.findViewById<TextView>(R.id.time_text_view)
        timeTextView.text = fixture.timeString
        setupScoreButton()
        setupDate()
        setupTime()
        setupGoals()
        setupAssists()
        setupCancelButton()
        setupConfirmButton()

        // Inflate the layout for this fragment
        return rootView
    }

    private fun setupScoreButton() {
        /*
    Add a click listener for the score button so the score can
    be updated.
    An alert will appear with two spinners: one for the home team
    and one for the away team.
     */
        val updateScoreButton = rootView.findViewById<Button>(R.id.update_score_button)
        updateScoreButton.setOnClickListener { v ->
            val alert = AlertDialog.Builder(v.context)
            alert.setTitle("Update score:")
            val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.dialog_score, null)
            val homeScorePicker = view.findViewById<NumberPicker>(R.id.homeScorePicker)
            homeScorePicker.minValue = 0
            homeScorePicker.maxValue = 20
            val awayScorePicker = view.findViewById<NumberPicker>(R.id.awayScorePicker)
            awayScorePicker.minValue = 0
            awayScorePicker.maxValue = 20
            if (fixture.result == FixtureResult.UNPLAYED) {
                homeScorePicker.value = 0
                awayScorePicker.value = 0
            } else {
                homeScorePicker.value = fixture.score.home
                awayScorePicker.value = fixture.score.away
            }
            alert.setPositiveButton("Confirm") { _, _ ->
                val newHomeScore = homeScorePicker.value
                val newAwayScore = awayScorePicker.value
                fixture.score = Score(newHomeScore, newAwayScore)
                scoreTextView.text = fixture.score.toString()
            }
            alert.setNegativeButton("cancel") { dialog, _ -> dialog.dismiss() }
            alert.setView(view)
            val dialog = alert.create()
            dialog.show()
        }
    }

    private fun setupDate() {
        dateTextView = rootView.findViewById(R.id.date_text_view)
        dateTextView.setOnClickListener { // COMPLETE
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
        val myFormat = "E, d MMM yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        dateTextView.text = sdf.format(calendar.time)
    }

    private fun setupTime() {
        timeTextView = rootView.findViewById(R.id.time_text_view)
        timeTextView.setOnClickListener {
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
        val myFormat = "kk:mm"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        timeTextView.text = sdf.format(calendar.time)
    }

    private fun setupGoals() {
        // Create an {@link TeamAdapter}, whose data source is a list of {@link Player}s. The
        // adapter knows how to create list items for each item in the list.
        goalsAdapter = EditRecyclerAdapter(fixture, true)

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list layout file.
        val recyclerView: RecyclerView = rootView.findViewById(R.id.goals_list)


        // Make the {@link ListView} use the {@link TeamAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Fixture} in the list.
        recyclerView.adapter = goalsAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        /*
        Add a click listener for the goalscorers button so the goalscorers
        can be updated.
        An alert will appear with a list of the team's players.
         */
        val addGoalscorerButton = rootView.findViewById<Button>(R.id.add_goalscorer_button)
        addGoalscorerButton.setOnClickListener { v ->
            val alert = AlertDialog.Builder(v.context)
            alert.setTitle("Add a goalscorer:")
            alert.setNegativeButton("cancel") { dialog, _ -> dialog.dismiss() }
            alert.setAdapter(adapter) { _, which ->
                val name: String = adapter.getItem(which)!!
                val newGoalscorers = fixture.goalscorers
                newGoalscorers.add(name)
                fixture.goalscorers = newGoalscorers
                goalsAdapter.notifyDataSetChanged()
            }
            val dialog = alert.create()
            dialog.show()
        }
    }

    private fun setupAssists() {
        // Create an {@link TeamAdapter}, whose data source is a list of {@link Player}s. The
        // adapter knows how to create list items for each item in the list.
        assistsAdapter = EditRecyclerAdapter(fixture, false)

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list layout file.
        val recyclerView: RecyclerView = rootView.findViewById(R.id.assists_list)


        // Make the {@link ListView} use the {@link TeamAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Fixture} in the list.
        recyclerView.adapter = assistsAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)


        /*
        Add a click listener for the assists button so the assists
        can be updated.
        An alert will appear with a list of the team's players.
         */
        val addAssistButton = rootView.findViewById<Button>(R.id.add_assist_button)
        addAssistButton.setOnClickListener { v ->
            val alert = AlertDialog.Builder(v.context)
            alert.setTitle("Add an assist:")
            alert.setNegativeButton("cancel") { dialog, _ -> dialog.dismiss() }
            alert.setAdapter(adapter) { _, which ->
                val name: String = adapter.getItem(which)!!
                //addNewView(assistsLayout, name);
                val newAssists = fixture.assists
                newAssists.add(name)
                fixture.assists = newAssists
                assistsAdapter.notifyDataSetChanged()
            }
            val dialog = alert.create()
            dialog.show()
        }
    }

    private fun setupConfirmButton() {
        val confirmButton = rootView.findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener { // Update fixture.
            val team = instance
            val originalFixture = team.fixtures[fixtureId]
            // Set the score.
            originalFixture.score = fixture.score
            // Get date and time
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH] + 1
            val day = calendar[Calendar.DAY_OF_MONTH]
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            originalFixture.dateTime = LocalDateTime.of(year, month, day, hour, minute)
            // Sort & set goalscorers.
            fixture.goalscorers.sort()
            originalFixture.goalscorers = fixture.goalscorers
            // Sort & set assists.
            fixture.assists.sort()
            originalFixture.assists = fixture.assists
            // Sort fixtures.
            team.fixtures.sort()
            // Update the team's stats.
            team.updateTeamStats()
            // Update the player's stats.
            team.updatePlayerStats()
            // Write the update to a file.
            writeFixturesFile(team.fixtures)
            // Return to the previous screen.
            val fm = activity!!.supportFragmentManager
            if (fm.backStackEntryCount > 0) {
                fm.popBackStack()
            }
        }
    }

    private fun setupCancelButton() {
        val cancelButton = rootView.findViewById<Button>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            val fm = activity!!.supportFragmentManager
            if (fm.backStackEntryCount > 0) {
                fm.popBackStack()
            }
        }
    }
}