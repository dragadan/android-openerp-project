package com.example.testapp;
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

import java.util.Calendar;

public class DateTimePickerDialog extends DialogFragment implements
		OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private final Boolean mTimePick;
    private View mParentView;
    private int mHour;
    private int mMinute;
    private Calendar mCal;


    DateTimePickerDialog(View v,Boolean tpick){
        this.mParentView = v;
        this.mTimePick = tpick;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
    }

    @Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the time chosen by the user
    	mCal = Calendar.getInstance();
    	mCal.set(year, month, day);
        if(mTimePick){
            TimePickerDialog tp = new TimePickerDialog(getActivity(), this, mHour, mMinute, true);
            tp.show();
        }
        else{
            String formattedDate = DateFormat.format("yyyy-MM-dd", mCal).toString();
            ((TextView) mParentView).setText(formattedDate);
        }
	}


    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        mCal.set(Calendar.HOUR_OF_DAY,hour);
        mCal.set(Calendar.MINUTE,minute);
        String formattedDate = DateFormat.format("yyyy-MM-dd hh:mm", mCal).toString();
        ((TextView) mParentView).setText(formattedDate);
    }
}