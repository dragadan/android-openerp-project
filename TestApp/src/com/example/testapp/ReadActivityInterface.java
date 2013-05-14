package com.example.testapp;

import java.util.HashMap;
import java.util.List;

import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TableRow;

public interface ReadActivityInterface {

	public void dataFetched(String[] fields, List<HashMap<String, Object>> data);			
}
