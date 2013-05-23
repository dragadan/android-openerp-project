package com.example.testapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import com.openerp.CreateAsyncTask;
import com.openerp.FieldsGetAndM2PopulateAT;
import com.openerp.OpenErpHolder;
import com.openerp.WriteAsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

public class FormActivity extends FragmentActivity implements
        FieldsGetActivityInterface, OnClickListener {
    private final static int SPACING_VERTICAL = 35;
    private final static int SPACING_HORIZONTAL = 10;
    private final static int DOWNLOAD_BUTTON_ID = 1210;
    private final static int UPLOAD_BUTTON_ID = 1211;
    private final static int CLEAR_BUTTON_ID = 1212;
    private ArrayList<View> formViews;
    private ScrollView svRec;
    private LinearLayout llRec;
    private LinearLayout mainFrame;
    private LinearLayout llTop;
    private Button btSave;
    private HashMap<String, Object> values;
    private HashMap<String, Object> edtRecord;
    private CreateAsyncTask crAsTa;
    private String[] fields;
    private FieldsGetAndM2PopulateAT fgAsTaAM2;
    private boolean editMode;
    private WriteAsyncTask wrAsTa;
    private HashMap<String, Object> fieldsAttrs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.inicializeVars();
        this.inicializeLayout();

    }

    @SuppressWarnings("unchecked")
    private void inicializeVars() {
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
                this.editMode = true;
            } else {
                this.editMode = false;
            }
        }
        this.values = new HashMap<String, Object>();
        this.fgAsTaAM2 = new FieldsGetAndM2PopulateAT(this, this.fields);
        this.fgAsTaAM2.execute(this.fields);

    }

    private void inicializeLayout() {

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
        this.fieldsAttrs = data;
        formViews = new ArrayList<View>();
        for (String fieldname : this.fields) {
            TextView tvLabel = new TextView(this);
            String headerText = (String) ((HashMap <String,Object>)this.fieldsAttrs.get(fieldname)).get("string");
            tvLabel.setText(headerText);
            llRec.addView(tvLabel);
            HashMap<String,Object> fAttr = (HashMap<String, Object>) this.fieldsAttrs.get(fieldname);
            String ftype = ((String) fAttr.get("type")).toUpperCase(Locale.US);
            switch (OpenErpHolder.OoType.valueOf(ftype)) {
                case INTEGER:
                    EditText etInt = new EditText(this);
                    etInt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etInt.setTag(fieldname);
                    llRec.addView(etInt);
                    if(this.editMode){
                        if (!(this.edtRecord.get(fieldname) instanceof Boolean)) {
                            etInt.setText(this.edtRecord.get(fieldname).toString());
                        }
                    }
                case FLOAT:
                    break;
                case CHAR:
                case TEXT:
                    EditText etName = new EditText(this);
                    etName.setTag(fieldname);
                    formViews.add(etName);
                    llRec.addView(etName);
                    if (this.editMode) {
                        if (!(this.edtRecord.get(fieldname) instanceof Boolean)) {
                            etName.setText((String) this.edtRecord.get(fieldname));
                        }

                    }
                    break;
                case DATE:
                    TextView tvDate = new TextView(this, null,
                            android.R.attr.spinnerStyle);
                    tvDate.setTag(fieldname);

                    tvDate.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Create the fragment and show it as a dialog.
                            DateTimePickerDialog newFragment = new DateTimePickerDialog(v,false);
                            newFragment.show(FormActivity.this.getSupportFragmentManager(), "DatePick");
                        }
                    });
                    formViews.add(tvDate);
                    llRec.addView(tvDate);
                    if (this.editMode) {
                        if (!(this.edtRecord.get(fieldname) instanceof Boolean)) {
                            tvDate.setText((String) this.edtRecord.get(fieldname));
                        }
                    }
                    break;
                case DATETIME:
                    TextView tvDateTime = new TextView(this, null,
                            android.R.attr.spinnerStyle);
                    tvDateTime.setTag(fieldname);

                    tvDateTime.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Create the fragment and show it as a dialog.
                            DateTimePickerDialog newFragment = new DateTimePickerDialog(v, true);
                            newFragment.show(FormActivity.this.getSupportFragmentManager(), "DateTimePick");
                        }
                    });
                    formViews.add(tvDateTime);
                    llRec.addView(tvDateTime);
                    if (this.editMode) {
                        if (!(this.edtRecord.get(fieldname) instanceof Boolean)) {
                            tvDateTime.setText((String) this.edtRecord.get(fieldname));
                        }
                    }
                    break;
                case BINARY:
                    Button downloadBt = new Button(this);
                    downloadBt.setText(getString(R.string.sDownload));
                    downloadBt.setId(DOWNLOAD_BUTTON_ID);
                    llRec.addView(downloadBt);
                    break;
                case SELECTION:
                    break;
                case ONE2ONE:
                    break;
                case MANY2ONE:
                    LinkedList<IdString> manylist = new LinkedList<IdString>();
                    Spinner spinner = new Spinner(this);
                    spinner.setTag(fieldname);
                    //Blank many2list option
                    IdString dummyIdStr = new IdString(-1, "");
                    manylist.add(dummyIdStr);
                    int pos = manylist.indexOf(dummyIdStr);
                    spinner.setSelection(pos); //Set as default
                    //--
                    for (HashMap<String, Object> record : fgAsTaAM2.getList(fieldname)) {
                        manylist.add(new IdString((Integer) record.get("id"),
                                (String) record.get("name")));
                    }
                    ArrayAdapter<IdString> spinnerArrayAdapter = new ArrayAdapter<IdString>(
                            this, android.R.layout.simple_spinner_dropdown_item,
                            manylist);
                    spinner.setAdapter(spinnerArrayAdapter);
                    formViews.add(spinner);
                    llRec.addView(spinner);
                    if (this.editMode) {
                        if (!(this.edtRecord.get(fieldname) instanceof Boolean)) {
                            int edId = (Integer) ((Object[]) this.edtRecord
                                    .get(fieldname))[0];
                            String edStr =  ((Object[]) this.edtRecord
                                    .get(fieldname))[1].toString();
                            IdString edIdStr = new IdString(edId, edStr);
                            pos = manylist.indexOf(edIdStr);
                            spinner.setSelection(pos);
                        }
                    }
                    break;
                case ONE2MANY:
                    break;
                case MANY2MANY:
                    break;
                case RELATED:
                    break;
                // TODO Complete field types views

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
                    if (((EditText) view).getText().toString().length() == 0) {
                        values.put((String) view.getTag(), false);
                    } else {
                        values.put((String) view.getTag(), ((EditText) view)
                                .getText().toString());
                    }
                }
                if (view instanceof Spinner) {
                    Integer m2id = ((IdString) ((Spinner) view)
                            .getSelectedItem()).getId();
                    if (m2id == -1) {
                        values.put((String) view.getTag(), false);
                    } else {
                        values.put((String) view.getTag(), m2id);
                    }
                }

                if (view instanceof TextView) {
                    if (((TextView) view).getText().toString().length() == 0) {
                        values.put((String) view.getTag(), false);
                    } else {
                        values.put((String) view.getTag(), ((TextView) view)
                                .getText().toString());
                    }
                }
                // TODO Complete view instance data retrieval

            }

            if (goodInput) {
                if (editMode) {
                    this.wrAsTa = new WriteAsyncTask(this);
                    this.wrAsTa.execute(this.edtRecord, values);
                } else {
                    // Call AsyncTask to actually insert record
                    this.crAsTa = new CreateAsyncTask(this);
                    this.crAsTa.execute(values);
                }
                startTree();
                finish();
            }
        }

        if(v.getId() == DOWNLOAD_BUTTON_ID){

        }
    }

    /**
     * Return to TreeView on Back and dialog confirm
     */
    @Override
    public void onBackPressed() {
        goBackDiscard();
    }

    private void startTree() {
        Intent i = new Intent(getBaseContext(), TreeActivity.class);
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

}
