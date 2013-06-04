package com.openerp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.testapp.R;
import com.example.testapp.ReadExtrasActivityInterface;

import java.util.HashMap;
import java.util.List;

/**
 * Populates many2one and many2many mMany2DataLists
 * String key is the fieldname
 * value is a list of records
 *
 * Populates binary fields
 */
public class ReadExtraAsyncTask extends AsyncTask<String, String, OpenErpConnect> {
    private final Activity activity;
    private ProgressDialog dialog;
    private final OpenErpConnect oc;
    private HashMap<String, Object> fieldAttrs;
	private HashMap<String, List<HashMap<String, Object>>> mMany2DataLists;
    private List<HashMap<String,Object>> mListBinary;


    public ReadExtraAsyncTask(Activity act, HashMap<String, Object> record) {
        activity = act;
        fieldAttrs = OpenErpHolder.getInstance().getmFieldsDescrip();
		mMany2DataLists = null;
        mListBinary = null;
        oc = OpenErpHolder.getInstance().getmOConn();
	}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(activity);
        dialog.setMessage(activity.getString(R.string.sConnecting) + "...");
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void populateMany2OneDataLists(){
        this.mMany2DataLists = new HashMap<String, List<HashMap<String, Object>>>();
        String[] retFields = { "id", "name" };
            for (String fieldname : OpenErpHolder.getInstance().getmFieldNames()) {
                String type = getFieldType(fieldname);
                if (type.equals("many2one") || type.equals("many2many")) {
                    String rel = getFieldRelation(fieldname);
                    Long[] ids = this.oc.search(rel, new Object[0]);
                    if (ids != null) {
                        List<HashMap<String, Object>> listData = oc.read(rel,
                                ids, retFields);
                        mMany2DataLists.put(fieldname, listData);
                    }
                }
            }

    }

    //TODO write code to populate binary
    private void populateBinaryFields() {
    }


    @Override
	protected OpenErpConnect doInBackground(String... params) {
        this.populateMany2OneDataLists();
		this.populateBinaryFields();
		return this.oc;
	}


    private String getFieldRelation(String fieldname) {
		String relation = null;
		if (this.fieldAttrs != null) {
			HashMap<String, Object> fieldAttr = (HashMap<String, Object>) fieldAttrs
					.get(fieldname);
			relation = (String) fieldAttr.get("relation");
		}
		return relation;
	}


	private String getFieldType(String fieldname) {
		String type = null;
		if (this.fieldAttrs != null) {
			HashMap<String, Object> fieldAttr = (HashMap<String, Object>) fieldAttrs
					.get(fieldname);
			type = (String) fieldAttr.get("type");
		}
		return type;
	}



    @Override
    protected void onPostExecute(OpenErpConnect result) {
        ((ReadExtrasActivityInterface) activity).dataFetched(mMany2DataLists,mListBinary);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }



}
