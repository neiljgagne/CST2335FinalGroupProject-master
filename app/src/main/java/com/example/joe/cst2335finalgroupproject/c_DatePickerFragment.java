package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by Nathan on 2017-12-06.
 */

public class c_DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Activity parentActivity;
    private EditText display;
    private Calendar calendar = Calendar.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(parentActivity, this, year, month, day);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.parentActivity = activity;
    }

    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        display.setText("");
        display.setText(c_CarTrackerActivity.DD_MM_YYYY.format(calendar.getTime()));
    }

    public void setDisplay(EditText editText) {
        this.display = editText;
    }
}