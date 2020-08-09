package com.jameschamberlain.footballteamtracker.fixtures

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixtureNewBinding
import com.jameschamberlain.footballteamtracker.objects.Fixture
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "NewFixtureFragment"

class NewFixtureFragment : Fragment() {


    private val db = Firebase.firestore
    private lateinit var binding: FragmentFixtureNewBinding
    private val calendar = Calendar.getInstance()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        activity!!.findViewById<View>(R.id.bottom_nav).visibility = View.GONE
        val containerLayout = activity!!.findViewById<FrameLayout>(R.id.fragment_container)
        val params = containerLayout.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(0, 0, 0, 0)
        containerLayout.layoutParams = params

        binding = FragmentFixtureNewBinding.inflate(layoutInflater)

        binding.editTextField.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        setupDatePicker()
        updateDateLabel()
        setupTimePicker()
        updateTimeLabel()
        setupSaveButton()
        setupCancelButton()
        return binding.root
    }

    private fun setupDatePicker() {
        binding.dateTextView.setOnClickListener {
            DatePickerDialog(context!!,
                    OnDateSetListener { _, year, month, dayOfMonth ->
                        calendar[Calendar.YEAR] = year
                        calendar[Calendar.MONTH] = month
                        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                        updateDateLabel()
                    },
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH])
                    .show()
        }
    }

    private fun updateDateLabel() {
        val sdf = SimpleDateFormat("E, d MMM yyyy", Locale.UK)
        binding.dateTextView.text = sdf.format(calendar.time)
    }

    private fun setupTimePicker() {
        binding.timeTextView.setOnClickListener {
            TimePickerDialog(context,
                    OnTimeSetListener { _, hourOfDay, minute ->
                        calendar[Calendar.HOUR_OF_DAY] = hourOfDay
                        calendar[Calendar.MINUTE] = minute
                        updateTimeLabel()
                    },
                    calendar[Calendar.HOUR_OF_DAY],
                    calendar[Calendar.MINUTE],
                    true)
                    .show()
        }
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
                Utils.teamRef.collection("fixtures")

                        .add(Fixture(opponentName, binding.homeRadioButton.isChecked, calendar.timeInMillis))

                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully added") }

                        .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }

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