package com.example.testapp;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.openerp.OpenErpHolder;
import com.openerp.ReadAsyncTask;

/*
 * TODO 
 * Define field custom names
 * Define field domain filter (see documentation)
 */

public class TreeActivity extends FragmentActivity implements ReadActivityInterface,
		android.view.View.OnClickListener {
	private final static int SPACING_VERTICAL = 35;
	private final static int SPACING_HORIZONTAL = 10;
	private ScrollView svRec;
	private LinearLayout llRec;
	private LinearLayout mainFrame;
	private LinearLayout llTop;
	private TableLayout tlRec;
	private Button bCreate;
	public List<HashMap<String, Object>> rData;
	private String[] inFields;
	private String modelName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.inicialiceLayout();
		this.startRead();

	}

	private void startRead() {
		// TODO check if params are set and raise exception if not
		// TODO parametrice infields or use view_fields_get from OpenERPConnect to choos fields
		String [] theFields = {"name","login","email","organization_id","login_date"};
		this.inFields = theFields;
		this.modelName = "res.users";
		

		OpenErpHolder.modelName = this.modelName;
		// Two lines for testing
		ReadAsyncTask rAsTa = new ReadAsyncTask(this);
		rAsTa.execute(inFields);
	}

	private void inicialiceLayout() {

		// Layout params definition
		LinearLayout.LayoutParams llpMatch = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		LinearLayout.LayoutParams llpWrap = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		// Create layouts and apply params
		this.mainFrame = new LinearLayout(this);
		this.mainFrame.setOrientation(LinearLayout.VERTICAL);

		this.bCreate = new Button(this);
		this.bCreate.setText(R.string.sCreate);
		this.bCreate.setOnClickListener(this);

		this.llTop = new LinearLayout(this);
		this.llTop.setLayoutParams(llpWrap);
		this.llTop.setPadding(SPACING_HORIZONTAL, SPACING_VERTICAL, 0, 0);

		this.svRec = new ScrollView(this);

		this.llRec = new LinearLayout(this);
		this.llRec.setLayoutParams(llpWrap);
		this.llRec.setOrientation(LinearLayout.VERTICAL);

		this.tlRec = new TableLayout(this);
		this.tlRec.setLayoutParams(llpMatch);
		this.tlRec.setPadding(SPACING_HORIZONTAL, SPACING_VERTICAL, 0, 0);

		// Define view structure
		this.llTop.addView(bCreate);
		this.svRec.addView(llRec);
		this.mainFrame.addView(llTop);
		this.mainFrame.addView(svRec);

		// Set content view
		setContentView(this.mainFrame, llpMatch);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.inmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.exit_app:
			doExit();
			break;

		default:
			break;
		}

		return true;
	}

	// Retrieve and draw data on TableRow
	@Override
	public void dataFetched(String[] fields, List<HashMap<String, Object>> data) {
		// Save retrieved data to local attribute and convert to correct type
		this.rData = data;
		// Set table columns
		TableRow trHeader = new TableRow(this);
		TextView[] tvColField = new TextView[fields.length];
		for (int col = 0; col < fields.length; col++) {
			tvColField[col] = new TextView(this);
			tvColField[col].setText(fields[col]);
			tvColField[col].setPadding(0, 0, SPACING_HORIZONTAL,
					SPACING_VERTICAL);
			trHeader.addView(tvColField[col]);
		}
		llRec.addView(tlRec);
		tlRec.addView(trHeader);

		// Draw record rows
		TextView[] tvField = new TextView[fields.length];
		TableRowCustom[] tr = new TableRowCustom[this.rData.size()];
		int row = 0;
		// For each record
		for (HashMap<String, Object> obj : this.rData) {
			// For each field
			tr[row] = new TableRowCustom(this);
			tr[row].setRowData(obj);
			for (int col = 0; col < fields.length; col++) {
				tvField[col] = new TextView(this);
				String colText = getStringFromField(obj.get(fields[col]));
				tvField[col].setText(colText);
				tvField[col].setPadding(0, 0, SPACING_HORIZONTAL,
						SPACING_VERTICAL);
				tr[row].addView(tvField[col]);
			}
			tlRec.addView(tr[row], new TableLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			row++;
		}
	}
	


	// Return string for object value (object or object[])
	private String getStringFromField(Object object) {
		String text = "";
		if (object instanceof String) {
			text = new String(String.valueOf(object));
		} else { // TODO Check other kinds of fields
			if (object instanceof Object[]) {
				Object[] a = (Object[]) object;
				text = new String(String.valueOf(a[1]));
			}
		}
		return text;
	}
	
	// Call loadForm(null) for create, with data to edit
	public void loadForm(HashMap<String, Object> recordDataToEdit){
		Intent i = new Intent(this, FormActivity.class);
		i.putExtra("fields", this.inFields);
		if(recordDataToEdit != null){
			i.putExtra("edtRecord", recordDataToEdit);
		}
		startActivity(i);
		this.finish();
	}
	
	// Button clicked
	@Override
	public void onClick(View v) {
		if (v.getId() == this.bCreate.getId()) {
			loadForm(null);
		}
	}

	//When a row is clicked a new instance of a DialogFragment is created, set the row clicked data and call show()
	
	public void rowShortClicked(TableRow tr) {
		AlertDialogRowClic adrc = AlertDialogRowClic.newInstance(this);
		if(tr instanceof TableRowCustom){
			adrc.setRowData(((TableRowCustom)tr).getRowData());
		}
		adrc.show(getSupportFragmentManager(), "Action");
	}

	
	/**
	 * Exit the app if user select yes.
	 */
	@Override
	public void onBackPressed() {
		doLogout();
	}

	private void doLogout() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog.setPositiveButton(getString(R.string.sYes),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(getBaseContext(),
								LoginActivity.class);
						startActivity(i);
						finish();
					}
				});

		alertDialog.setNegativeButton(getString(R.string.sNo), null);

		alertDialog.setMessage(getString(R.string.sLogout));
		alertDialog.setTitle("TestApp");
		alertDialog.show();
	}

	private void doExit() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog.setPositiveButton(getString(R.string.sYes),
				new OnClickListener() {

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



	




}
