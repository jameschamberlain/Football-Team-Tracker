package com.jameschamberlain.footballteamtracker.fixtures

import android.os.Bundle
import android.os.LocaleList
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.type.DateTime
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.data.Fixture
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixtureNewBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private const val TAG = "NewFixtureFragment"

class NewFixtureFragment : Fragment() {


    private var _binding: FragmentFixtureNewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFixtureNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.editTextField.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        setupDatePicker()
        setupTimePicker()
        setupSaveButton()
        setupCancelButton()
    }

    private fun setupDatePicker() {
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
//        val sdf = SimpleDateFormat("E, d MMM yyyy")
//        binding.dateTextView.text = sdf.format(calendar.time)

        val formatter = DateTimeFormatter.ofPattern("E, d MMM yyyy")
        binding.dateTextView.text = formatter.format(LocalDateTime.ofInstant(calendar.toInstant(), calendar.timeZone.toZoneId()))
    }

    private fun setupTimePicker() {
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

    private fun updateTimeLabel() {
//        val sdf = SimpleDateFormat("kk:mm")
//        binding.timeTextView.text = sdf.format(calendar.time)

        val formatter = DateTimeFormatter.ofPattern("kk:mm")
        binding.timeTextView.text = formatter.format(LocalDateTime.ofInstant(calendar.toInstant(), calendar.timeZone.toZoneId()))
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            if (binding.editTextField.text.toString() == "") {
                Toast.makeText(
                    context,
                    "Enter a valid opponent name", Toast.LENGTH_SHORT
                ).show()
            } else {
                val opponentName = binding.editTextField.text.toString()
                Utils.getTeamReference(requireActivity()).collection("fixtures")

                    .add(
                        Fixture(
                            opponentName,
                            binding.homeRadioButton.isChecked,
                            calendar.timeInMillis
                        )
                    )

                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully added")
                        NavHostFragment.findNavController(this@NewFixtureFragment).navigateUp()
                    }

                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                        Toast.makeText(
                            requireContext(),
                            "Failed to create fixture, try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun setupCancelButton() {
        binding.cancelButton.setOnClickListener {
            NavHostFragment.findNavController(this@NewFixtureFragment).navigateUp()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}