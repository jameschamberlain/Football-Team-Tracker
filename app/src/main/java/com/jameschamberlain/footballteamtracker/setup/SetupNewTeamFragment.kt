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
import com.jameschamberlain.footballteamtracker.FileUtils
import com.jameschamberlain.footballteamtracker.MainActivity
import com.jameschamberlain.footballteamtracker.Team
import com.jameschamberlain.footballteamtracker.databinding.FragmentSetupNewTeamBinding

class SetupNewTeamFragment : Fragment() {

    private lateinit var binding: FragmentSetupNewTeamBinding
    private lateinit var teamName: String
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            checkFieldForEmptyValues()
        }
    }

    fun checkFieldForEmptyValues() {
        teamName = binding.editTextField.text.toString()
        binding.continueButton.isEnabled = teamName != ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentSetupNewTeamBinding.inflate(layoutInflater)

        binding.continueButton.setOnClickListener {
            teamName = binding.editTextField.text.toString()
            val team: Team = Team.team
            team.name = teamName
            FileUtils.writeTeamFile(teamName)

            startActivity(Intent(activity, MainActivity::class.java))
        }
        binding.editTextField.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        binding.editTextField.addTextChangedListener(textWatcher)
        checkFieldForEmptyValues()

        return binding.root
    }

}