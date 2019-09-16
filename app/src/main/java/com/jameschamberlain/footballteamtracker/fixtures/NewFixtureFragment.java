package com.jameschamberlain.footballteamtracker.fixtures;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jameschamberlain.footballteamtracker.FileUtils;
import com.jameschamberlain.footballteamtracker.R;
import com.jameschamberlain.footballteamtracker.Team;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

public class NewFixtureFragment extends Fragment {

    private Team team = Team.getInstance();
    private TextView dateTextView;
    private TextView timeTextView;
    private final Calendar CALENDAR = Calendar.getInstance();
    private View rootView;


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            CALENDAR.set(Calendar.YEAR, year);
            CALENDAR.set(Calendar.MONTH, monthOfYear);
            CALENDAR.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }

    };

    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            CALENDAR.set(Calendar.HOUR_OF_DAY, hourOfDay);
            CALENDAR.set(Calendar.MINUTE, minute);
            updateTimeLabel();
        }
    };

    private void updateDateLabel() {
        String myFormat = "E, d MMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        dateTextView.setText(sdf.format(CALENDAR.getTime()));
    }

    private void updateTimeLabel() {
        String myFormat = "kk:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        timeTextView.setText(sdf.format(CALENDAR.getTime()));
    }


    public NewFixtureFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().findViewById(R.id.nav_view).setVisibility(View.GONE);

        FrameLayout containerLayout = getActivity().findViewById(R.id.container);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)containerLayout.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        containerLayout.setLayoutParams(params);

        rootView = inflater.inflate(R.layout.fragment_fixture_new, container, false);

        final RadioGroup radioGroup = rootView.findViewById(R.id.radio_group);

        dateTextView = rootView.findViewById(R.id.date_text_view);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // COMPLETE
                new DatePickerDialog(getContext(), date, CALENDAR
                        .get(Calendar.YEAR), CALENDAR.get(Calendar.MONTH),
                        CALENDAR.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        timeTextView = rootView.findViewById(R.id.time_text_view);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(), time, CALENDAR
                        .get(Calendar.HOUR_OF_DAY), CALENDAR.get(Calendar.MINUTE),
                        true).show();
            }
        });

        final EditText editText = rootView.findViewById(R.id.edit_text_field);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        Button saveButton = rootView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(editText.getText()).matches("")) {
                    Toast.makeText(getContext(),
                            "Enter a valid opponent name", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("NewFixtureFragment", "Month: " + CALENDAR.get(Calendar.MONTH));
                    // Check whether home or away
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton selectedRadioButton = rootView.findViewById(selectedId);

                    // Get opponent name
                    String opponentName = String.valueOf(editText.getText());

                    // Get date and time
                    int year = CALENDAR.get(Calendar.YEAR);
                    int month = CALENDAR.get(Calendar.MONTH) + 1;
                    int day = CALENDAR.get(Calendar.DAY_OF_MONTH);
                    int hour = CALENDAR.get(Calendar.HOUR_OF_DAY);
                    int minute = CALENDAR.get(Calendar.MINUTE);

                    if (selectedRadioButton.getText().equals("Home")) {
                        team.getFixtures().add(new Fixture(team.getName(), opponentName,
                                LocalDateTime.of(year, month, day, hour, minute)));
                    } else {
                        team.getFixtures().add(new Fixture(opponentName, team.getName(),
                                LocalDateTime.of(year, month, day, hour, minute)));
                    }
                    Collections.sort(team.getFixtures());
                    FileUtils.writeFixturesFile(team.getFixtures());
                    team.setNumOfFixtures(team.getFixtures().size());
                    FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        fm.popBackStackImmediate();
                    }


                }
            }
        });

        updateDateLabel();
        updateTimeLabel();
        setupCancelButton();

        return rootView;
    }

    private void setupCancelButton() {
        Button cancelButton = rootView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
            }
        });
    }

}
