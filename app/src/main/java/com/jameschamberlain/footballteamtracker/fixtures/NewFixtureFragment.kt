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
import com.jameschamberlain.footballteamtracker.Team.Companion.team
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixtureNewBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class NewFixtureFragment : Fragment() {

    private lateinit var binding: FragmentFixtureNewBinding
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        activity!!.findViewById<View>(R.id.nav_view).visibility = View.GONE
        val containerLayout = activity!!.findViewById<FrameLayout>(R.id.fragment_container)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(0, 0, 0, 0)
        containerLayout.layoutParams = params

        binding = FragmentFixtureNewBinding.inflate(layoutInflater)

        binding.dateTextView.setOnClickListener {
            DatePickerDialog(context!!, date, calendar[Calendar.YEAR], calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]).show()
        }

        binding.timeTextView.setOnClickListener {
            TimePickerDialog(context, time, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE],
                    true).show()
        }

        binding.editTextField.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        updateDateLabel()
        updateTimeLabel()
        setupSaveButton()
        setupCancelButton()
        return binding.root
    }

    private fun updateDateLabel() {
        val sdf = SimpleDateFormat("E, d MMM yyyy", Locale.UK)
        binding.dateTextView.text = sdf.format(calendar.time)
    }

    private fun updateTimeLabel() {
        val sdf = SimpleDateFormat("kk:mm", Locale.UK)
        binding.timeTextView.text = sdf.format(calendar.time)
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            if (binding.editTextField.text.toString() == "") {
                Toast.makeText(context,
                        "Enter a valid opponent name", Toast.LENGTH_SHORT).show()
            } else {
                val opponentName = binding.editTextField.text.toString()

                val year = calendar[Calendar.YEAR]
                val month = calendar[Calendar.MONTH] + 1
                val day = calendar[Calendar.DAY_OF_MONTH]
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val minute = calendar[Calendar.MINUTE]
                if (binding.homeRadioButton.isChecked) {
                    team.fixtures.add(Fixture(team.name, opponentName,
                            LocalDateTime.of(year, month, day, hour, minute)))
                } else {
                    team.fixtures.add(Fixture(opponentName, team.name,
                            LocalDateTime.of(year, month, day, hour, minute)))
                }
                team.fixtures.sort()
                writeFixturesFile(team.fixtures)
                team.numOfFixtures = team.fixtures.size
                val fragmentManager = activity!!.supportFragmentManager
                if (fragmentManager.backStackEntryCount > 0) {
                    fragmentManager.popBackStackImmediate()
                }
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