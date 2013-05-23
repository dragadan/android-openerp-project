package com.example.testapp;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

import com.openerp.FieldsGetAsyncTask;
import com.openerp.OpenErpHolder;
import com.openerp.ReadAsyncTask;

/*
 * TODO 
 * Define field custom names
 * Define field domain filter (see documentation)
 */

public class TreeActivity extends FragmentActivity implements FieldsGetActivityInterface, ReadActivityInterface,
        android.view.View.OnClickListener {
    private final static int SPACING_VERTICAL = 35;
    private final static int SPACING_HORIZONTAL = 10;
    private ScrollView svRec;
    private RelativeLayout rlRec;
    private LinearLayout mainFrame;
    private LinearLayout llTop;
    private TableLayout tlRec;
    private Button bCreate;
    public List<HashMap<String, Object>> rData;
    private String[] inFields;
    private String modelName;
    private FieldsGetAsyncTask fgAsTa;
    private HashMap<String, Object> fieldsAttrs;
    private TwoDScrollView tdScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initializeLayout();
        this.startRead();

    }

    private void startRead() {
        // TODO check if params are set and raise exception if not
        // TODO parametrice infields or use view_fields_get from OpenERPConnect to choos fields
        String [] theFields = {"boolfield","intfield","floatfield","charfield","textfield","datefield","datetimefield","binfield","selfield","ftm2o","fto2m","ftm2m","funcfield"};
        this.inFields = theFields;
        this.modelName = "ftest";

        OpenErpHolder.modelName = this.modelName;

        //Get field attributes (calls fieldsFetched when completed)
        this.fgAsTa = new FieldsGetAsyncTask(this, this.inFields);
        this.fgAsTa.execute(this.inFields);
    }
    private void initializeLayout() {

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

        //this.svRec = new ScrollView(this);

        this.rlRec = new RelativeLayout(this);
        this.rlRec.setLayoutParams(llpWrap);
        this.tdScroll = new TwoDScrollView(this);

        this.tdScroll.addView(rlRec);


        this.tlRec = new TableLayout(this);
        this.tlRec.setLayoutParams(llpMatch);
        this.tlRec.setPadding(SPACING_HORIZONTAL, SPACING_VERTICAL, 0, 0);

        // Define view structure
        this.llTop.addView(bCreate);
        //this.svRec.addView(rlRec);
        this.mainFrame.addView(llTop);
        this.mainFrame.addView(tdScroll);

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

    // When task for getting field attributes
    @Override
    public void fieldsFetched(HashMap<String, Object> data) {
        //Save field attributes to local var
        this.fieldsAttrs = data;
        //Call read task to get record values (calls dataFetched when completed)
        ReadAsyncTask rAsTa = new ReadAsyncTask(this);
        rAsTa.execute(inFields);
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
            String headerText = (String) ((HashMap <String,Object>)this.fieldsAttrs.get(fields[col])).get("string");
            tvColField[col].setText(headerText);
            tvColField[col].setTypeface(Typeface.DEFAULT_BOLD);
            tvColField[col].setPadding(0, 0, SPACING_HORIZONTAL,
                    SPACING_VERTICAL);
            trHeader.addView(tvColField[col]);
        }

        rlRec.addView(tlRec);
        tlRec.addView(trHeader);

        // Draw record rows
        View[] vFields = new View[fields.length];
        TableRowCustom[] tr = new TableRowCustom[this.rData.size()];
        int row = 0;
        // For each record
        for (HashMap<String, Object> obj : this.rData) {
            // For each field
            tr[row] = new TableRowCustom(this);
            tr[row].setRowData(obj);
            for (int col = 0; col < fields.length; col++) {
                View fieldRepr = getFieldReprView(fields[col],obj.get(fields[col]));
                tr[row].addView(fieldRepr);
            }
            tlRec.addView(tr[row], new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            row++;
        }
    }


    // Return a view that contains field value
    View getFieldReprView(String fieldname,Object obj){
        View fieldRepView = new View(this);
        HashMap<String,Object> fAttr = (HashMap<String, Object>) this.fieldsAttrs.get(fieldname);
        String ftype = ((String) fAttr.get("type")).toUpperCase(Locale.US);
        if(ftype.equals("BOOLEAN")){
            CheckBox cb = new CheckBox(this);
            cb.setChecked((Boolean) obj);
            cb.setEnabled(false);
            cb.setGravity(Gravity.TOP);
            fieldRepView = cb;
        }
        else{
            if(!(obj instanceof Boolean)){
                switch (OpenErpHolder.OoType.valueOf(ftype)) {
                    case INTEGER:
                    case FLOAT:
                    case CHAR:
                    case TEXT:
                    case DATE:
                    case DATETIME:
                        fieldRepView = new TextView(this);
                        ((TextView)fieldRepView).setText((String) (obj).toString());
                        break;
                    case BINARY:
                        fieldRepView = new TextView(this);
                        char[] bytes = ((String)obj).toCharArray();
                        ((TextView)fieldRepView).setText(String.valueOf(bytes.length) + " bytes");

                        break;
                    case SELECTION:
                        fieldRepView = new TextView(this);
                        String selectionKey = obj.toString(); //This holds key, now we look for the string representation
                        String selectionText = null;
                        Object[] selection = ((Object[])fAttr.get("selection"));
                        for(int i = 0; i<selection.length;i++) {
                            Object[] thePair = (Object[])selection[i]; //Contains [0] = key, [1] = string
                            if(((String) thePair[0]).equals(selectionKey)) {
                                selectionText = thePair[1].toString();
                                break;
                            }
                        }
                        if(selectionText!=null){
                            ((TextView)fieldRepView).setText(selectionText);
                        }
                        else {
                            ((TextView)fieldRepView).setText(selectionKey);
                        }

                        break;
                    case ONE2ONE:
                        break;
                    case MANY2ONE:
                        fieldRepView = new TextView(this);
                        ((TextView)fieldRepView).setText((String)((Object[])obj)[1]);
                        break;
                    case ONE2MANY:
                    case MANY2MANY:
                        fieldRepView = new TextView(this);
                        ((TextView)fieldRepView).setText(String.valueOf(((Object[])obj).length) + " " + this.getString(R.string.sRecords));
                        break;
                    case RELATED:
                        break;
                }
            }
            else{
                fieldRepView = new TextView(this);
                ((TextView)fieldRepView).setText("");
            }
        }


        fieldRepView.setPadding(0, 0, SPACING_HORIZONTAL,SPACING_VERTICAL);
        return fieldRepView;
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
