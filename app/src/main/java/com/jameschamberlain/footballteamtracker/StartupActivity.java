package com.jameschamberlain.footballteamtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jameschamberlain.footballteamtracker.fixtures.Fixture;

import java.util.ArrayList;
import java.util.Collections;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isSetup = false;

        FileUtils.checkFiles(this);

        String teamName = FileUtils.readTeamFile();

        if (!teamName.isEmpty()) {
            isSetup = true;
        }

        if (isSetup) {
            ArrayList<Player> teamMembers = FileUtils.readPlayersFile();
            ArrayList<Fixture> fixtures = FileUtils.readFixturesFile();

            Team team = Team.getInstance();
            team.setName(teamName);
            team.setFixtures(fixtures);
            team.setPlayers(teamMembers);
            Intent newActivity = new Intent(this, MainActivity.class);
            this.startActivity(newActivity);
        } else {
            Intent newActivity = new Intent(this, SetupActivity.class);
            this.startActivity(newActivity);
        }

    }
}
