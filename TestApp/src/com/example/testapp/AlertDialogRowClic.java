package com.example.testapp;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

public class AlertDialogRowClic extends DialogFragment {
	
	private Activity activity;
	private HashMap<String, Object> rowData;
	
	 /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static AlertDialogRowClic newInstance(Activity parent) {
        AlertDialogRowClic f = new AlertDialogRowClic();
        f.setParent(parent);
        return f;
    }
    
    public void setParent(Activity parent){
    	this.activity = parent;
    }
    
    public void setRowData(HashMap<String, Object> inData){
    	this.rowData = inData;
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Use the Builder class for convenient dialog construction
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		String[] options = { activity.getString(R.string.sView),
				activity.getString(R.string.sEdit) };
		builder.setItems(options, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(AlertDialogRowClic.this.activity instanceof TreeActivity){
					((TreeActivity)activity).loadForm(AlertDialogRowClic.this.rowData);
				}		
			}
		});
		
		// Create the AlertDialog object and return it
		return builder.create();
	}



	
}
