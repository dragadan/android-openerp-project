package com.example.testapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.openerp.FieldsGetAsyncTask;
import com.openerp.OpenErpHolder;
import com.openerp.ReadAsyncTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/*
 * TODO 
 * Define field custom names
 * Define field domain filter (see documentation)
 */

public class TreeActivity extends FragmentActivity implements FieldsGetActivityInterface, ReadActivityInterface,
        android.view.View.OnClickListener {
    private final static int SPACING_VERTICAL = 35;
    private final static int SPACING_HORIZONTAL = 10;
    private RelativeLayout mRelativeLayoutRecord;
    private LinearLayout mLinearLayoutMain;
    private LinearLayout mLinearLayoutTop;
    private TableLayout mTableLayout;
    private Button mButtonCreate;
    private List<HashMap<String, Object>> mValues;
    private String[] mFieldNames;
    private String mModelName;
    private FieldsGetAsyncTask mFieldsGetAsyncTask;
    private HashMap<String, Object> mFieldsDescrip;
    private TwoDScrollView mTdScroll;
    private TableRow mTrHeader;
    private boolean hasBinaryField;
    private ReadAsyncTask mReadAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initializeLayout();
        this.startRead();

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
        this.mLinearLayoutMain = new LinearLayout(this);
        this.mLinearLayoutMain.setOrientation(LinearLayout.VERTICAL);

        this.mButtonCreate = new Button(this);
        this.mButtonCreate.setText(R.string.sCreate);
        this.mButtonCreate.setOnClickListener(this);

        this.mLinearLayoutTop = new LinearLayout(this);
        this.mLinearLayoutTop.setLayoutParams(llpWrap);
        this.mLinearLayoutTop.setPadding(SPACING_HORIZONTAL, SPACING_VERTICAL, 0, 0);

        this.mRelativeLayoutRecord = new RelativeLayout(this);
        this.mRelativeLayoutRecord.setLayoutParams(llpWrap);

        this.mTdScroll = new TwoDScrollView(this);
        this.mTdScroll.addView(mRelativeLayoutRecord);

        this.mTableLayout = new TableLayout(this);
        this.mTableLayout.setLayoutParams(llpMatch);
        this.mTableLayout.setPadding(SPACING_HORIZONTAL, SPACING_VERTICAL, 0, 0);

        mRelativeLayoutRecord.addView(mTableLayout);
        mTrHeader = new TableRow(this);
        mTableLayout.addView(mTrHeader);

        // Define view structure
        this.mLinearLayoutTop.addView(mButtonCreate);
        this.mLinearLayoutMain.addView(mLinearLayoutTop);
        this.mLinearLayoutMain.addView(mTdScroll);

        // Set content view
        setContentView(this.mLinearLayoutMain, llpMatch);
    }


    private void startRead() {
        // TODO check if params are set and raise exception if not
        String [] theFields = {"boolfield","intfield","floatfield","charfield","textfield","datefield","datetimefield","binfield","selfield","ftm2o","fto2m","ftm2m","funcfield"};
        this.mFieldNames = theFields;
        OpenErpHolder.getInstance().setmFieldNames(Arrays.copyOf(this.mFieldNames,this.mFieldNames.length));
        this.mModelName = "ftest";
        OpenErpHolder.getInstance().setmModelName(this.mModelName);
        //Get field attributes (calls fieldsFetched when completed)
        this.mFieldsGetAsyncTask = new FieldsGetAsyncTask(this, this.mFieldNames);
        this.mFieldsGetAsyncTask.execute(this.mFieldNames);
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

    // When task for getting field attributes ends.
    @Override
    public void fieldsFetched(HashMap<String, Object> data) {
        //Save field attributes to local var
        this.mFieldsDescrip = data;
        this.hasBinaryField = false;
        OpenErpHolder.getInstance().setmFieldsDescrip(mFieldsDescrip);

        //Check for binary fields and take them out to avoid fetching all the uploaded files.
        for(String fname: this.mFieldNames){
            HashMap<String,Object> fieldAttrs = (HashMap<String,Object>)this.mFieldsDescrip.get(fname);
            if ( ((HashMap<String,Object>)fieldAttrs).get("type").equals("binary") ){
                this.hasBinaryField = true;
                int i,j;
                for (i=j=0; j<this.mFieldNames.length;++j){
                    if (!fname.equals(this.mFieldNames[j])) this.mFieldNames[i++] = this.mFieldNames[j];
                }
                this.mFieldNames = Arrays.copyOf(this.mFieldNames,i);
            }
        }
        //Call read task to get record values.
        this.mReadAsyncTask = new ReadAsyncTask(this);
        mReadAsyncTask.execute(mFieldNames);
    }

    // Retrieve and draw data on TableRow, called when ReadAsyncTask ends.
    @Override
    public void dataFetched() {
        // Save retrieved data to local attribute and convert to correct type
        this.mValues = this.mReadAsyncTask.getData();
        OpenErpHolder.getInstance().setmData(mValues);
        String[] fields =  this.mReadAsyncTask.getFields();

        // Set table columns
        TextView[] tvColField = new TextView[fields.length];
        for (int col = 0; col < fields.length; col++) {
            tvColField[col] = new TextView(this);
            String headerText = (String) ((HashMap <String,Object>)this.mFieldsDescrip.get(fields[col])).get("string");
            tvColField[col].setText(headerText);
            tvColField[col].setTypeface(Typeface.DEFAULT_BOLD);
            tvColField[col].setPadding(0, 0, SPACING_HORIZONTAL,
                    SPACING_VERTICAL);
            mTrHeader.addView(tvColField[col]);
        }

        // Draw record rows
        View[] vFields = new View[fields.length];
        TableRowCustom[] tr = new TableRowCustom[this.mValues.size()];
        int row = 0;
        // For each record
        for (HashMap<String, Object> obj : this.mValues) {
            // For each field
            tr[row] = new TableRowCustom(this);
            tr[row].setRowData(obj);
            for (int col = 0; col < fields.length; col++) {
                View fieldRepr = getFieldReprView(fields[col],obj.get(fields[col]));
                tr[row].addView(fieldRepr);
            }
            mTableLayout.addView(tr[row], new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            row++;
        }
    }


    // Return a view that contains field value.
    public View getFieldReprView(String fieldname,Object obj){
        View fieldRepView = new View(this);
        HashMap<String,Object> fAttr = (HashMap<String, Object>) this.mFieldsDescrip.get(fieldname);
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
                        //No action taken on tree view, binary fields shouldn't be loaded here.
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
        Intent i = new Intent(this, OpenErpHolder.getInstance().getmClassFormActivity());
        if(recordDataToEdit != null){
            i.putExtra("editRecordId",mValues.indexOf(recordDataToEdit));
        }
        startActivityForResult(i, 1);

    }

    // Button clicked
    @Override
    public void onClick(View v) {
        if (v.getId() == this.mButtonCreate.getId()) {
            loadForm(null);
        }
    }

    // Called when Tree shows again after the form is closed
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {

        if (requestCode == 1) {
            // Reload activity if form has been saved
            if(resultCode == RESULT_OK){
                finish();
                startActivity(getIntent());
            }
            if (resultCode == RESULT_CANCELED) {
                // Not needed to reload anything
            }
        }
    }//onActivityResult

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



    public void setmModelName(String mModelName) {
        this.mModelName = mModelName;
    }

    public void setmFieldNames(String[] mFieldNames) {
        this.mFieldNames = mFieldNames;
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

    /**
     * Returns the relative layout that will hold the TableLayout
     * @return RelativeLayout
     */
    public RelativeLayout getmRelativeLayoutRecord() {
        return mRelativeLayoutRecord;
    }

    /**
     * Returns LinearLayout that contains all the views.
     * @return LinearLayout
     */
    public LinearLayout getmLinearLayoutMain() {
        return mLinearLayoutMain;
    }

    /**
     * Returns LinearLayout that holds the action buttons on the top of the screen.
     * @return LinearLayout
     */
    public LinearLayout getmLinearLayoutTop() {
        return mLinearLayoutTop;
    }

    /**
     * Returns the table that contains the TableRows withe the records
     * @return TableLayout
     */
    public TableLayout getmTableLayout() {
        return mTableLayout;
    }

    /**
     * Returns the create button, this button should start FormActivity and end the current Activity
     * @return Button
     */
    public Button getmButtonCreate() {
        return mButtonCreate;
    }

    /**
     * Returns a List that contains all the data retrieved
     * It contains HashMap<String,Object> values, the string key is the field name
     * and the Object is the data itself.
     * @return List<HashMap<String, Object>>
     */
    public List<HashMap<String, Object>> getmValues() {
        return mValues;
    }

    /**
     * Returns a String array with the field names that will be retrieved.
     * @return String[]
     */
    public String[] getmFieldNames() {
        return mFieldNames;
    }

    /**
     * Returns the target model name
     * @return String
     */
    public String getmModelName() {
        return mModelName;
    }

    /**
     * Returns HashMap with field attributes, the string key is the field name and the Object contains
     * the field attributes.
     * EX:
     * HMobject["One2ManyField"]:
     * (String)"context"
     * (String)"relation": "model"
     * (Object[])"domain":
     * (String)"type":"one2many"
     * (Boolean)"selectable":"true"
     * (String)"relation-field":"field"
     * (String)"string":"O2M Field"
     * @return HashMap<String, Object>
     */
    public HashMap<String, Object> getmFieldsAttrs() {
        return mFieldsDescrip;
    }

}
