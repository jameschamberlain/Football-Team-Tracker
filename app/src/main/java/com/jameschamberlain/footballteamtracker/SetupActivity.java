package com.jameschamberlain.footballteamtracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.jameschamberlain.footballteamtracker.fixtures.Fixture;

import java.util.ArrayList;
import java.util.Objects;

public class SetupActivity extends AppCompatActivity {

    private String teamName;

    private EditText inputField;

    private Button continueButton;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            checkFieldForEmptyValues();
        }
    };

    void checkFieldForEmptyValues() {
        teamName = inputField.getText().toString();

        if (teamName.equals("")) {
            continueButton.setEnabled(false);
        } else {
            continueButton.setEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        //Objects.requireNonNull(getSupportActionBar()).hide(); //hide the title bar

        setContentView(R.layout.activity_setup);

        inputField = findViewById(R.id.edit_text_field);

        continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamName = inputField.getText().toString();
                Team team = Team.getInstance();
                team.setName(teamName);
                FileUtils.writeTeamFile(teamName);
                team.setFixtures(new ArrayList<Fixture>());
                team.setPlayers(new ArrayList<Player>());
                Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        inputField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        inputField.addTextChangedListener(textWatcher);
        checkFieldForEmptyValues();

    }
}
