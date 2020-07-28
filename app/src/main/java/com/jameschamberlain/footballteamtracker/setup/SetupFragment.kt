package com.jameschamberlain.footballteamtracker.setup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jameschamberlain.footballteamtracker.MainActivity
import com.jameschamberlain.footballteamtracker.R
import com.jameschamberlain.footballteamtracker.databinding.FragmentSetupBinding

class SetupFragment : Fragment() {

    private lateinit var binding: FragmentSetupBinding
    private lateinit var teamCode: String
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            checkFieldForEmptyValues()
        }
    }

    fun checkFieldForEmptyValues() {
        teamCode = binding.editTextField.text.toString()
        binding.continueButton.isEnabled = teamCode != ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentSetupBinding.inflate(layoutInflater)

        binding.continueButton.setOnClickListener {
            //TODO
            startActivity(Intent(activity, MainActivity::class.java))
        }

        binding.editTextField.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        binding.editTextField.addTextChangedListener(textWatcher)
        checkFieldForEmptyValues()

        binding.managerLoginButton.setOnClickListener {
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, ManagerLoginFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return binding.root
    }
}