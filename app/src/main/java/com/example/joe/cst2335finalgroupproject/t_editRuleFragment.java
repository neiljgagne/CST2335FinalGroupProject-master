package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Joe on 2017-12-14.
 */

public class t_editRuleFragment extends Fragment {

    Activity callingActivity;
    Bundle info;

    EditText et;
    View v;

    t_ThermostatRule rule;
    String ruleText;
    int rulePosition;
    long ruleDatabaseID;

    Spinner daySpinner;
    Spinner hourSpinner;
    Spinner minuteSpinner;
    Spinner tempSpinner;

    Button discardButton;
    Button saveAsNewButton;
    Button saveButton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callingActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        info = getArguments();

        v = inflater.inflate(R.layout.t_edit_rule, container, false);

        ruleText = info.getString("rule");
        rulePosition = info.getInt("listPosition");
        ruleDatabaseID = info.getLong("databaseID");

        rule = t_ThermostatRule.valueOf(ruleText);

        initializeDaySpinner();
        initializeHourSpinner();
        initializeMinuteSpinner();
        initializeTempSpinner();

        initializeDiscardButton();
        initializeSaveAsNewButton();
        initializeSaveButton();

        et = v.findViewById(R.id.manual_text_entry);
        et.setText(ruleText);

        return v;
    }

    public void initializeDiscardButton() {
        discardButton = v.findViewById(R.id.t_discardEditChangesButton);
        switch (callingActivity.getLocalClassName()) {
            case "t_ThermostatProgramActivity": //landscape view - frameLayout in same activity as list
                discardButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //add confimration dialogalert
                        ((t_ThermostatProgramActivity) callingActivity).discardMethod();
                        callingActivity.getFragmentManager()
                                .beginTransaction()
                                .remove(t_editRuleFragment.this)
                                .commit();
                    }
                });
            case "t_DetailView":    //portrait view - frameLayout was loaded into a new activity t_DetailView
                discardButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //add confimration dialogalert
                        callingActivity.setResult(t_ThermostatProgramActivity.DISCARD_RESULT);
                        callingActivity.finish();
                    }
                });
        }
    }

    public void initializeSaveAsNewButton() {
        saveAsNewButton = v.findViewById(R.id.t_saveNewRuleEditChangesButton);

        switch (callingActivity.getLocalClassName()) {
            case "t_ThermostatProgramActivity": //landscape view - frameLayout in same activity as list
                saveAsNewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Bundle extrasBundle = new Bundle();
                        extrasBundle.putString("ruleToAdd", rule.toString());
                        ((t_ThermostatProgramActivity) callingActivity).edit_SaveAsNew(extrasBundle);
                        callingActivity.getFragmentManager()
                                .beginTransaction()
                                .remove(t_editRuleFragment.this)
                                .commit();
                    }
                });
            case "t_DetailView":    //portrait view - frameLayout was loaded into a new activity t_DetailView
                saveAsNewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Bundle extrasBundle = new Bundle();
                        extrasBundle.putString("ruleToAdd", rule.toString());
                        Intent resultIntent = new Intent().putExtras(extrasBundle);
                        callingActivity.setResult(t_ThermostatProgramActivity.SAVE_AS_NEW_RESULT, resultIntent);
                        callingActivity.finish();   //finish closes this empty activity on phones.
                    }
                });
        }
    }

    public void initializeSaveButton() {
        saveButton = v.findViewById(R.id.t_saveEditChangesButton);

        switch (callingActivity.getLocalClassName()) {
            case "t_ThermostatProgramActivity": //landscape view - frameLayout in same activity as list
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Bundle extrasBundle = new Bundle();
                        extrasBundle.putString("ruleToAdd", rule.toString());
                        extrasBundle.putInt("deletePosition", rulePosition);
                        extrasBundle.putString("ruleToDelete", info.getString("rule"));
                        extrasBundle.putLong("deleteID", ruleDatabaseID);
                        ((t_ThermostatProgramActivity) callingActivity).edit_SaveChanges(extrasBundle);
                        callingActivity.getFragmentManager()
                                .beginTransaction()
                                .remove(t_editRuleFragment.this)
                                .commit();
                    }
                });
            case "t_DetailView":    //portrait view - frameLayout was loaded into a new activity t_DetailView
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Bundle extrasBundle = new Bundle();
                        extrasBundle.putString("ruleToAdd", rule.toString());
                        extrasBundle.putInt("listPosition", rulePosition);
                        extrasBundle.putString("ruleToDelete", info.getString("rule"));
                        extrasBundle.putLong("deleteID", ruleDatabaseID);
                        Intent resultIntent = new Intent().putExtras(extrasBundle);
                        callingActivity.setResult(t_ThermostatProgramActivity.SAVE_RESULT, resultIntent);
                        callingActivity.finish();   //finish closes this empty activity on phones.
                    }
                });
        }
    }

    public void initializeDaySpinner() {

        daySpinner = v.findViewById(R.id.day_spinner);
        List<String> days = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.t_daysArray)));
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(v.getContext(), android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);
        daySpinner.setSelection(dayAdapter.getPosition(rule.getDay()));
        daySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rule.setDay(adapterView.getItemAtPosition(i).toString());
                et.setText(rule.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void initializeHourSpinner() {

        hourSpinner = v.findViewById(R.id.hour_spinner);
        Integer[] hours_array = new Integer[24];
        for (int i = 0; i < hours_array.length; i++) {
            hours_array[i] = i;
        }
        List<Integer> hours = new ArrayList<>(Arrays.asList(hours_array));
        ArrayAdapter<Integer> hourAdapter = new ArrayAdapter<>(v.getContext(), android.R.layout.simple_spinner_item, hours);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSpinner.setAdapter(hourAdapter);
        hourSpinner.setSelection(hourAdapter.getPosition(rule.getHour()));
        hourSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rule.setHour(Integer.valueOf(adapterView.getItemAtPosition(i).toString()));
                et.setText(rule.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void initializeMinuteSpinner() {

        minuteSpinner = v.findViewById(R.id.minute_spinner);
        Integer[] minute_array = new Integer[4];
        for (int i = 0; i < minute_array.length; i++) {
            minute_array[i] = i * 15;
        }
        List<Integer> minutes = new ArrayList<>(Arrays.asList(minute_array));
        ArrayAdapter<Integer> minuteAdapter = new ArrayAdapter<>(v.getContext(), android.R.layout.simple_spinner_item, minutes);
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minuteSpinner.setAdapter(minuteAdapter);
        minuteSpinner.setSelection(minuteAdapter.getPosition(rule.getMinute()));
        minuteSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rule.setMinute(Integer.valueOf(adapterView.getItemAtPosition(i).toString()));
                et.setText(rule.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void initializeTempSpinner() {
        tempSpinner = v.findViewById(R.id.temp_spinner);
        Integer[] temp_array = new Integer[26];
        for (int i = 0; i < temp_array.length; i++) {
            temp_array[i] = i + 10;
        }
        List<Integer> temps = new ArrayList<>(Arrays.asList(temp_array));
        ArrayAdapter<Integer> tempAdapter = new ArrayAdapter<>(v.getContext(), android.R.layout.simple_spinner_item, temps);
        tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tempSpinner.setAdapter(tempAdapter);
        tempSpinner.setSelection(tempAdapter.getPosition(rule.getTemp()));
        tempSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rule.setTemp(Integer.valueOf(adapterView.getItemAtPosition(i).toString()));
                et.setText(rule.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
