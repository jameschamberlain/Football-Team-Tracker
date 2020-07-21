package com.jameschamberlain.footballteamtracker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class SetupActivity : AppCompatActivity() {
    private lateinit var teamName: String
    private lateinit var inputField: EditText
    private lateinit var continueButton: Button
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            checkFieldForEmptyValues()
        }
    }

    fun checkFieldForEmptyValues() {
        teamName = inputField.text.toString()
        continueButton.isEnabled = teamName != ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        //Objects.requireNonNull(getSupportActionBar()).hide(); //hide the title bar
        setContentView(R.layout.activity_setup)
        inputField = findViewById(R.id.edit_text_field)
        continueButton = findViewById(R.id.continue_button)
        continueButton.setOnClickListener {
            teamName = inputField.text.toString()
            val team: Team = Team.instance
            team.name = teamName
            FileUtils.writeTeamFile(teamName)
            team.fixtures = ArrayList()
            team.players = ArrayList()
            val intent = Intent(this@SetupActivity, MainActivity::class.java)
            startActivity(intent)
        }
        inputField.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        inputField.addTextChangedListener(textWatcher)
        checkFieldForEmptyValues()
    }
}