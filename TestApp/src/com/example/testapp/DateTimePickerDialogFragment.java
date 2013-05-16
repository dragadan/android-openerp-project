package com.example.testapp;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class DateTimePickerDialogFragment extends DialogFragment implements
		TimePickerDialog.OnTimeSetListener, OnDateSetListener {
	private View asocView;

	public void setCallerView(View v){
		this.asocView = v;
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the time chosen by the user
    	Calendar c = Calendar.getInstance();
    	c.set(year, month, day);
    	String formattedDate = DateFormat.format("yyyy-MM-dd", c).toString();
		((TextView)asocView).setText(formattedDate);
	}
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// Do something with the time chosen by the user
		//((TextView)asocView).setText(String.valueOf(hourOfDay)+":"+String.valueOf(minute));
	}

	
}