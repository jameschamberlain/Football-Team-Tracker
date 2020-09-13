package com.jameschamberlain.footballteamtracker.fixtures

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.jameschamberlain.footballteamtracker.Utils
import com.jameschamberlain.footballteamtracker.databinding.FragmentFixtureNewBinding
import com.jameschamberlain.footballteamtracker.data.Fixture
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "NewFixtureFragment"

class NewFixtureFragment : Fragment() {


    private var _binding: FragmentFixtureNewBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFixtureNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Utils.hideBottomNav(requireActivity())

        binding.editTextField.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        setupDatePicker()
        updateDateLabel()
        setupTimePicker()
        updateTimeLabel()
        setupSaveButton()
        setupCancelButton()
    }

    private fun setupDatePicker() {
        binding.dateTextView.setOnClickListener {
            DatePickerDialog(requireContext(),
                    { _, year, month, dayOfMonth ->
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
                    { _, hourOfDay, minute ->
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

                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully added")
                            NavHostFragment.findNavController(this@NewFixtureFragment).navigateUp()
                        }

                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                            Toast.makeText(requireContext(), "Failed to create fixture, try again", Toast.LENGTH_SHORT).show()
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