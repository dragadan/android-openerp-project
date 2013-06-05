package com.example.testapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.openerp.CreateAsyncTask;
import com.openerp.OpenErpHolder;
import com.openerp.ReadExtraAsyncTask;
import com.openerp.WriteAsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

public class FormActivity extends FragmentActivity implements
        ReadActivityInterface, OnClickListener, DialogInterface.OnClickListener {
    private final static int SPACING_VERTICAL = 25;
    private final static int SPACING_HORIZONTAL = 10;
    private final static int INTVAL = 2; //For integer Fields
    private final static int STRVAL = 3; //For Char and Text Fields
    private final static int DOUBLEVAL = 4; //For Float fields
    private final static int BOOLVAL = 5; // For Boolean Fields
    private final static int DOWNLOAD_BUTTON_ID = 120;
    private final static int UPLOAD_BUTTON_ID = 121;
    private final static int CLEAR_BUTTON_ID = 122;
    private final static int BINARY_FIELD_ID = 123;

    private ArrayList<View> mFormViews;
    private ScrollView mSvRecords;
    private LinearLayout mLlRecordframe;
    private LinearLayout mLlMainframe;
    private LinearLayout mLlTopframe;
    private Button mBSave;
    private HashMap<String, Object> mValues;
    private boolean mEditMode;
    private HashMap<String, Object> mValuesToEdit;
    private CreateAsyncTask mCreateTask;
    private String[] mFieldNames;
    private ReadExtraAsyncTask mReadExtraAsyncTask;
    private WriteAsyncTask mWriteTask;
    private HashMap<String, Object> mFieldsAttributes; //Fields OpenERP Attributes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initializeVars();
        this.initializeLayout();

    }


    private void initializeVars() {
        this.mValuesToEdit = null;
        this.mFieldNames = OpenErpHolder.getInstance().getmFieldNames();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("editRecordId")) {
                this.mValuesToEdit = (HashMap<String, Object>) OpenErpHolder.getInstance().getmData().get(((Integer) extras.get("editRecordId")));
                this.mEditMode = true;
            } else {
                this.mEditMode = false;
            }
        }
        this.mValues = new HashMap<String, Object>();
        this.mReadExtraAsyncTask = new ReadExtraAsyncTask(this, this.mValuesToEdit);
        this.mReadExtraAsyncTask.execute(this.mFieldNames);

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
        this.mLlMainframe = new LinearLayout(this);
        this.mLlMainframe.setOrientation(LinearLayout.VERTICAL);

        this.mBSave = new Button(this);
        this.mBSave.setText(R.string.sSave);
        this.mBSave.setOnClickListener(this);

        this.mLlTopframe = new LinearLayout(this);
        this.mLlTopframe.setLayoutParams(llpWrap);
        this.mLlTopframe.setPadding(SPACING_HORIZONTAL, SPACING_VERTICAL, 0, 0);

        this.mSvRecords = new ScrollView(this);

        this.mLlRecordframe = new LinearLayout(this);
        this.mLlRecordframe.setLayoutParams(llpWrap);
        this.mLlRecordframe.setOrientation(LinearLayout.VERTICAL);
        this.mLlRecordframe.setPadding(SPACING_HORIZONTAL, SPACING_VERTICAL, 0, 0);

        // Define view structure
        this.mLlTopframe.addView(mBSave);
        this.mSvRecords.addView(mLlRecordframe);
        this.mLlMainframe.addView(mLlTopframe);
        this.mLlMainframe.addView(mSvRecords);

        // Set content view
        setContentView(this.mLlMainframe, llpMatch);
    }


    /**
     * Get OpenERP Field Type in string
     * @param fieldname
     * @return field type
     */
    private String getFieldType(String fieldname){
        HashMap<String,Object> fAttr = (HashMap<String, Object>) this.mFieldsAttributes.get(fieldname);
        return ((String) fAttr.get("type")).toUpperCase(Locale.US);
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
    public void dataFetched(){
        // Draws layout
        // The views arraylist holds the views to retrieve data on Save
        this.mFieldsAttributes = OpenErpHolder.getInstance().getmFieldsDescrip();
        mFormViews = new ArrayList<View>();
        int fieldcount = 0;
        for (String fieldname : this.mFieldNames) {
            fieldcount++;
            TextView tvLabel = new TextView(this);
            String headerText = (String) ((HashMap <String,Object>)this.mFieldsAttributes.get(fieldname)).get("string");
            tvLabel.setText(headerText);
            tvLabel.setPadding(0, SPACING_VERTICAL, 0, 0);
            mLlRecordframe.addView(tvLabel);
            String ftype = getFieldType(fieldname);
            switch (OpenErpHolder.OoType.valueOf(ftype)) {
                case BOOLEAN:
                    CheckBox cb = new CheckBox(this);
                    cb.setTag(fieldname);
                    mFormViews.add(cb);
                    mLlRecordframe.addView(cb);
                    cb.setId(BOOLVAL*1000 + fieldcount);
                    if(this.mEditMode){
                        Boolean checked = (Boolean) this.mValuesToEdit.get(fieldname);
                        cb.setChecked(checked);
                    }
                    cb.setClickable(true);
                    break;
                case INTEGER:
                    EditText etInt = new EditText(this);
                    etInt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etInt.setTag(fieldname);
                    etInt.setId(INTVAL*1000 + fieldcount);
                    mFormViews.add(etInt);
                    mLlRecordframe.addView(etInt);
                    if(this.mEditMode){
                        if (!(this.mValuesToEdit.get(fieldname) instanceof Boolean)) {
                            etInt.setText(this.mValuesToEdit.get(fieldname).toString());
                        }
                    }
                    break;
                case FLOAT:
                    EditText etFloat = new EditText(this);
                    etFloat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    etFloat.setTag(fieldname);
                    etFloat.setId(DOUBLEVAL*1000 + fieldcount);
                    mFormViews.add(etFloat);
                    mLlRecordframe.addView(etFloat);
                    if(this.mEditMode){
                        if (!(this.mValuesToEdit.get(fieldname) instanceof Boolean)) {
                            etFloat.setText(this.mValuesToEdit.get(fieldname).toString());
                        }
                    }
                    break;
                case CHAR:
                case TEXT:
                    EditText etStrfield = new EditText(this);
                    etStrfield.setTag(fieldname);
                    etStrfield.setId(STRVAL*1000 + fieldcount);
                    mFormViews.add(etStrfield);
                    mLlRecordframe.addView(etStrfield);
                    if (this.mEditMode) {
                        if (!(this.mValuesToEdit.get(fieldname) instanceof Boolean)) {
                            etStrfield.setText((String) this.mValuesToEdit.get(fieldname));
                        }
                    }
                    break;
                case DATE:
                    TextView tvDate = new TextView(this, null,android.R.attr.spinnerStyle);
                    tvDate.setTag(fieldname);
                    tvDate.setId(STRVAL*1000 +fieldcount);
                    tvDate.setOnClickListener(this);
                    mFormViews.add(tvDate);
                    mLlRecordframe.addView(tvDate);
                    if (this.mEditMode) {
                        if (!(this.mValuesToEdit.get(fieldname) instanceof Boolean)) {
                            tvDate.setText((String) this.mValuesToEdit.get(fieldname));
                        }
                    }
                    break;
                case DATETIME:
                    TextView tvDateTime = new TextView(this, null,android.R.attr.spinnerStyle);
                    tvDateTime.setTag(fieldname);
                    tvDateTime.setId(STRVAL*1000 +fieldcount);
                    tvDateTime.setOnClickListener(this);
                    mFormViews.add(tvDateTime);
                    mLlRecordframe.addView(tvDateTime);
                    if (this.mEditMode) {
                        if (!(this.mValuesToEdit.get(fieldname) instanceof Boolean)) {
                            tvDateTime.setText((String) this.mValuesToEdit.get(fieldname));
                        }
                    }
                    break;
                case BINARY:
                    Button btDownload = new Button(this);
                    btDownload.setText(R.string.sDownload);
                    btDownload.setId(DOWNLOAD_BUTTON_ID);
                    btDownload.setOnClickListener(this);
                    Button btClear = new Button(this);
                    btClear.setText(R.string.sClear);
                    btClear.setId(CLEAR_BUTTON_ID);
                    btClear.setOnClickListener(this);
                    Button btUpload = new Button(this);
                    btUpload.setText(R.string.sUpload);
                    btUpload.setId(UPLOAD_BUTTON_ID);
                    TextView tvBinSize = new TextView(this);
                    tvBinSize.setMinimumWidth(100);
                    btDownload.setEnabled(false);
                    btClear.setEnabled(false);
                    //TODO uncomment and adapt to get info from listBinary

                    if(this.mEditMode) {
                        Object binfield = this.mReadExtraAsyncTask.getListBinary().get(0).get(fieldname);
                        if (!(binfield instanceof Boolean)) {
                            //TODO get right index
                            byte[] bytes = (((String) binfield).getBytes());
                            tvBinSize.setText(readableFileSize(bytes.length));
                            btDownload.setTag((String)fieldname);
                            btDownload.setEnabled(true);
                            btClear.setEnabled(true);
                        }
                    }
                    LinearLayout llBinary = new LinearLayout(this);
                    llBinary.setOrientation(LinearLayout.HORIZONTAL);
                    llBinary.addView(tvBinSize);
                    llBinary.addView(btDownload);
                    llBinary.addView(btUpload);
                    llBinary.addView(btClear);

                    mLlRecordframe.addView(llBinary);
                    break;
                case SELECTION:
                    break;
                case ONE2ONE:
                    break;
                case MANY2ONE:
                    LinkedList<IdString> manylist = new LinkedList<IdString>();
                    Spinner spinner = new Spinner(this);
                    spinner.setTag(fieldname);
                    spinner.setId(INTVAL+fieldcount);
                    //Blank many2list option
                    IdString dummyIdStr = new IdString(-1, "");
                    manylist.add(dummyIdStr);
                    int pos = manylist.indexOf(dummyIdStr);
                    spinner.setSelection(pos); //Set as default
                    //--
                    for (HashMap<String, Object> record : this.mReadExtraAsyncTask.getMany2DataLists().get(fieldname)) {
                        manylist.add(new IdString((Integer) record.get("id"),
                                (String) record.get("name")));
                    }
                    ArrayAdapter<IdString> spinnerArrayAdapter = new ArrayAdapter<IdString>(
                            this, android.R.layout.simple_spinner_dropdown_item,
                            manylist);
                    spinner.setAdapter(spinnerArrayAdapter);
                    mFormViews.add(spinner);
                    mLlRecordframe.addView(spinner);
                    if (this.mEditMode) {
                        if (!(this.mValuesToEdit.get(fieldname) instanceof Boolean)) {
                            int edId = (Integer) ((Object[]) this.mValuesToEdit
                                    .get(fieldname))[0];
                            String edStr =  ((Object[]) this.mValuesToEdit
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


    public String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /*
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View v) {
        Boolean goodInput = true;

        // Get input data into HashMap<String,Object>
        // TODO check input data (required mFieldNames, bad input...)
        if (v.getId() == this.mBSave.getId()) {
            for (View view : mFormViews) {
                String field = (String) view.getTag();
                switch (view.getId()/1000){
                    case BOOLVAL:
                        Boolean checked = ((CheckBox)view).isChecked();
                        mValues.put(field,checked);
                        break;
                    case INTVAL:
                        if(view instanceof Spinner){
                            Integer m2id = ((IdString) ((Spinner) view).getSelectedItem()).getId();
                            if (m2id == -1) {
                                mValues.put(field, false);
                            } else {
                                mValues.put(field, m2id);
                            }
                        }
                        else{
                            String intText = ((EditText) view).getText().toString();
                            if (intText.length() == 0) {
                                mValues.put(field, false);
                            } else {
                                mValues.put(field,Integer.valueOf(intText));
                            }
                        }
                        break;
                    case STRVAL:
                        String strText = "";
                        if(view instanceof  EditText){
                            strText = ((EditText) view).getText().toString();
                        }
                        else{
                            strText = ((TextView) view).getText().toString();
                        }
                        if (strText.length() == 0) {
                            mValues.put(field, false);
                        } else {
                            mValues.put(field,strText);
                        }
                        break;
                    case DOUBLEVAL:
                        String doubleText = ((EditText) view).getText().toString();
                        if (doubleText.length() == 0) {
                            mValues.put(field, false);
                        } else {
                            mValues.put(field,Double.valueOf(doubleText));
                        }
                        break;
                }

                // TODO Complete view instance data retrieval
            }

            if (goodInput) {
                if (mEditMode) {
                    this.mWriteTask = new WriteAsyncTask(this);
                    this.mWriteTask.execute(this.mValuesToEdit, mValues);
                } else {
                    // Call AsyncTask to actually insert record
                    this.mCreateTask = new CreateAsyncTask(this);
                    this.mCreateTask.execute(mValues);
                }
                setResult(RESULT_OK);
                finish();
            }
        }

        if(v.getId() == DOWNLOAD_BUTTON_ID){
            byte[] buffer =((String) this.mValuesToEdit.get(v.getTag())).getBytes();
            byte[] dec_buffer = Base64.decode(buffer);

            //Write file to SD
            String path = "/testpath/";
            String fileName = "testname";

            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + path);
                dir.mkdirs();
                FileOutputStream f = new FileOutputStream(new File(dir, fileName));
                f.write(dec_buffer, 0, dec_buffer.length);
                f.close();
            } catch (Exception e) {
                Log.d("Download", e.getMessage());
            }


        }

        // If TextView for DATE or DATETIME is clicked
        if(v.getId()/1000 == STRVAL){
            DateTimePickerDialog newFragment;
            if(getFieldType((String) v.getTag()).equals("DATETIME")){
                newFragment = new DateTimePickerDialog(v, true);
                newFragment.show(getSupportFragmentManager(), "DateTimePick");
            }
            else{
                if(getFieldType((String) v.getTag()).equals("DATE")){
                    newFragment = new DateTimePickerDialog(v, false);
                    newFragment.show(getSupportFragmentManager(), "DatePick");
                }
            }
        }
    }

    /**
     * Return to TreeView on Back and dialog confirm
     */
    @Override
    public void onBackPressed() {
        goBackDiscard();
    }

    private void goBackDiscard() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setPositiveButton(R.string.sYes, (DialogInterface.OnClickListener) this);
        alertDialog.setNegativeButton(getString(R.string.sNo), null);
        alertDialog.setMessage(getString(R.string.sDiscard));
        alertDialog.setTitle("TestApp");
        alertDialog.show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
