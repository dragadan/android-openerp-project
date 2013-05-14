package com.example.testapp;

import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.widget.TableRow;

public class TableRowCustom extends TableRow{

	private OnClickListener shortClicListener;
	private Context context;
	private int id;
	private HashMap<String, Object> rowData;
	
	public TableRowCustom(Context context) {
		super(context);
		this.context = context;
		this.shortClicListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TableRowCustom.this.setBackgroundResource(android.R.drawable.list_selector_background);
				((TreeActivity)TableRowCustom.this.context).rowShortClicked(TableRowCustom.this);
				
			}
		};
		
		this.setOnClickListener(shortClicListener);	
	}
	
	public void setRowData(HashMap<String, Object> inData){
		this.rowData = inData;
	}
	
	public HashMap<String, Object> getRowData(){
		return this.rowData;
	}


}
