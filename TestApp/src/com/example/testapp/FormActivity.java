package com.example.testapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.openerp.CreateAsyncTask;
import com.openerp.FieldsGetAndM2PopulateAT;

public class FormActivity extends FragmentActivity implements
		ReadActivityInterface, FieldsGetActivityInterface, OnClickListener {
	private final static int SPACING_VERTICAL = 35;
	private final static int SPACING_HORIZONTAL = 10;
	private ArrayList<View> formViews;
	private ScrollView svRec;
	private LinearLayout llRec;
	private LinearLayout mainFrame;
	private LinearLayout llTop;
	private Button btSave;
	private HashMap<String, Object> values;
	private CreateAsyncTask crAsTa;
	private String[] fields;
	private HashMap<String, Object> edtRecord;
	private FieldsGetAndM2PopulateAT fgAsTa;

	public enum OoType {
		BOOLEAN, INTEGER, FLOAT, CHAR, TEXT, DATE, DATETIME, BINARY, SELECTION, ONE2ONE, MANY2ONE, ONE2MANY, MANY2MANY, RELATED,
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.inicialiceVars();
		this.inicialiceLayout();

	}

	@SuppressWarnings("unchecked")
	private void inicialiceVars() {
		this.edtRecord = null;
		this.fields = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey("fields")) {
				this.fields = extras.getStringArray("fields");
			}
			if (extras.containsKey("edtRecord")) {
				this.edtRecord = (HashMap<String, Object>) extras
						.get("edtRecord");
			}
		}
		this.values = new HashMap<String, Object>();
		this.fgAsTa = new FieldsGetAndM2PopulateAT(this, this.fields);
		this.fgAsTa.execute(this.fields);
		new HashMap<String, String>();
	}

	private void inicialiceLayout() {

		// Layout params definition
		LinearLayout.LayoutParams llpMatch = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		LinearLayout.LayoutParams llpWrap = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		// Create layouts and apply params
		this.mainFrame = new LinearLayout(this);
		this.mainFrame.setOrientation(LinearLayout.VERTICAL);

		this.btSave = new Button(this);
		this.btSave.setText(R.string.sSave);
		this.btSave.setOnClickListener(this);

		this.llTop = new LinearLayout(this);
		this.llTop.setLayoutParams(llpWrap);
		this.llTop.setPadding(SPACING_HORIZONTAL, SPACING_VERTICAL, 0, 0);

		this.svRec = new ScrollView(this);

		this.llRec = new LinearLayout(this);
		this.llRec.setLayoutParams(llpWrap);
		this.llRec.setOrientation(LinearLayout.VERTICAL);
		this.llRec.setPadding(SPACING_HORIZONTAL, SPACING_VERTICAL, 0, 0);

		// Define view structure
		this.llTop.addView(btSave);
		this.svRec.addView(llRec);
		this.mainFrame.addView(llTop);
		this.mainFrame.addView(svRec);

		// Set content view
		setContentView(this.mainFrame, llpMatch);
	}

	/*
	 * Read field values (To read m2o and m2m fields)
	 * 
	 * @see
	 * com.example.testapp.ReadActivityInterface#dataFetched(java.lang.String[],
	 * java.util.List)
	 */


	public void showTimePickerDialog(View v) {
		DateTimePickerDialogFragment newFragment = new DateTimePickerDialogFragment();
		newFragment.setCallerView(v);
		newFragment.show(getSupportFragmentManager(), "timePicker");

	}

	/*
	 * Read field types to decide the view layout (Called when
	 * FieldsGetAsyncTask ends)
	 * 
	 * @see
	 * com.example.testapp.FieldsGetActivityInterface#fieldsFeyched(java.util
	 * .HashMap)
	 */
	@Override
	public void fieldsFetched(HashMap<String, Object> data) {
		// Draws layout
		// formViews array holds the views to retrieve data on Save
		formViews = new ArrayList<View>();
		for (String fieldname : this.fields) {
			Log.d(fieldname, fgAsTa.getFieldType(fieldname));
			TextView tvLabel = new TextView(this);
			tvLabel.setText(fieldname);
			llRec.addView(tvLabel);
			switch (OoType
					.valueOf(fgAsTa.getFieldType(fieldname).toUpperCase())) {
			case TEXT:
			case CHAR:
				EditText etName = new EditText(this);
				etName.setTag(fieldname);
				formViews.add(etName);
				llRec.addView(etName);
				break;
			case MANY2ONE:
			case MANY2MANY:
				LinkedList<IdString> manylist = new LinkedList<IdString>();
				for (HashMap<String, Object> record : fgAsTa.getList(fieldname)) {
					manylist.add(new IdString((Integer) record.get("id"),
							(String) record.get("name")));
				}
				Spinner spinner = new Spinner(this);
				spinner.setTag(fieldname);
				ArrayAdapter<IdString> spinnerArrayAdapter = new ArrayAdapter<IdString>(
						this, android.R.layout.simple_spinner_dropdown_item,
						manylist);
				spinner.setAdapter(spinnerArrayAdapter);
				formViews.add(spinner);
				llRec.addView(spinner);
				break;
			case DATE:
			case DATETIME:
				TextView tvDate = new TextView(this, null,
						android.R.attr.spinnerStyle);
				tvDate.setTag(fieldname);

				tvDate.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						FormActivity.this.showTimePickerDialog(v);
					}
				});
				formViews.add(tvDate);
				llRec.addView(tvDate);
				break;
			//TODO Complete field types views
			default:
				break;
			}
		}
	}

	/*
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		Boolean goodInput = true;

		// Get input data into HashMap<String,Object>
		// TODO check input data (required fields, bad input...)
		if (v.getId() == this.btSave.getId()) {
			for (View view : formViews) {
				if (view instanceof EditText) {
					values.put((String) view.getTag(), ((EditText) view)
							.getText().toString());
				}
				if (view instanceof Spinner) {
					values.put((String) view.getTag(),
							((IdString) ((Spinner) view).getSelectedItem())
									.getId());
				}
				
				if (view instanceof TextView) {
					values.put((String) view.getTag(),
							((TextView) view).getText().toString());
				}
				//TODO Complete view instance data retrieval
				
			}

			// Call AsyncTask to actually insert record
			if (goodInput) {
				this.crAsTa = new CreateAsyncTask(this);
				this.crAsTa.execute(values);
				startTree();
				finish();
			}
		}
	}


	/**
	 * Return to TreeView on Back pressed
	 */
	@Override
	public void onBackPressed() {
		goBackDiscard();
	}
	
	private void startTree(){
		Intent i = new Intent(getBaseContext(),
				TreeActivity.class);
		startActivity(i);
	}

	private void goBackDiscard() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog.setPositiveButton(R.string.sYes,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						FormActivity.this.startTree();
						finish();
						
					}
				});

		alertDialog.setNegativeButton(getString(R.string.sNo), null);

		alertDialog.setMessage(getString(R.string.sDiscard));
		alertDialog.setTitle("TestApp");
		alertDialog.show();
	}
/*
	private void doExit() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog.setPositiveButton(R.string.sYes,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});

		alertDialog.setNegativeButton(getString(R.string.sNo), null);

		alertDialog.setMessage(getString(R.string.sCloseQ));
		alertDialog.setTitle("TestApp");
		alertDialog.show();
	}
*/

	@Override
	public void dataFetched(String[] fields, List<HashMap<String, Object>> data) {
				
	}

}
