package com.jameschamberlain.footballteamtracker.fixtures

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.jameschamberlain.footballteamtracker.FileUtils.writeFixturesFile
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Team.Companion.instance
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class NewFixtureFragment : Fragment() {
    private val team = instance
    private lateinit var dateTextView: TextView
    private lateinit var timeTextView: TextView
    private val calendar = Calendar.getInstance()
    private lateinit var rootView: View
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

    private fun updateDateLabel() {
        val myFormat = "E, d MMM yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        dateTextView.text = sdf.format(calendar.time)
    }

    private fun updateTimeLabel() {
        val myFormat = "kk:mm"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        timeTextView.text = sdf.format(calendar.time)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity!!.findViewById<View>(R.id.nav_view).visibility = View.GONE
        val containerLayout = activity!!.findViewById<FrameLayout>(R.id.container)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(0, 0, 0, 0)
        containerLayout.layoutParams = params
        rootView = inflater.inflate(R.layout.fragment_fixture_new, container, false)
        val radioGroup = rootView.findViewById<RadioGroup>(R.id.radio_group)
        dateTextView = rootView.findViewById(R.id.fixture_date_text_view)
        dateTextView.setOnClickListener { // COMPLETE
            DatePickerDialog(context!!, date, calendar[Calendar.YEAR], calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]).show()
        }
        timeTextView = rootView.findViewById(R.id.fixture_time_text_view)
        timeTextView.setOnClickListener {
            TimePickerDialog(context, time, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE],
                    true).show()
        }
        val editText = rootView.findViewById<EditText>(R.id.edit_text_field)
        editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        val saveButton = rootView.findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            if (editText.text.toString() == "") {
                Toast.makeText(context,
                        "Enter a valid opponent name", Toast.LENGTH_SHORT).show()
            } else {
                // Check whether home or away
                val selectedId = radioGroup.checkedRadioButtonId
                val selectedRadioButton = rootView.findViewById<RadioButton>(selectedId)

                // Get opponent name
                val opponentName = editText.text.toString()

                // Get date and time
                val year = calendar[Calendar.YEAR]
                val month = calendar[Calendar.MONTH] + 1
                val day = calendar[Calendar.DAY_OF_MONTH]
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val minute = calendar[Calendar.MINUTE]
                if (selectedRadioButton.text == "Home") {
                    team.fixtures.add(Fixture(team.name, opponentName,
                            LocalDateTime.of(year, month, day, hour, minute)))
                } else {
                    team.fixtures.add(Fixture(opponentName, team.name,
                            LocalDateTime.of(year, month, day, hour, minute)))
                }
                team.fixtures.sort()
                writeFixturesFile(team.fixtures)
                team.numOfFixtures = team.fixtures.size
                val fm = activity!!.supportFragmentManager
                if (fm.backStackEntryCount > 0) {
                    fm.popBackStackImmediate()
                }
            }
        }
        updateDateLabel()
        updateTimeLabel()
        setupCancelButton()
        return rootView
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