package com.openerp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import com.example.testapp.R;
import com.example.testapp.ReadActivityInterface;

import java.util.HashMap;
import java.util.List;

public class ReadAsyncTask extends AsyncTask<String, String, OpenErpConnect> {
	public ProgressDialog dialog;
	private Activity activity;
    private OpenErpConnect oc;
	private Long[] ids;

    private List<HashMap<String, Object>> mData;
	private String[] mFields;

	public ReadAsyncTask(Activity act) {
		this.activity = act;
		this.ids = null;
		this.mData = null;
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

	/*
	 * Reads field values from model params -> mFields
	 */
	@Override
	protected OpenErpConnect doInBackground(String... params) {
        this.oc = OpenErpHolder.getInstance().getmOConn();
		this.mFields = new String[params.length];
		System.arraycopy(params, 0, this.mFields, 0, params.length);
		this.ids = oc.search(OpenErpHolder.getInstance().getmModelName(), new Object[0]);
		if (this.ids != null) {
			this.mData = oc.read(OpenErpHolder.getInstance().getmModelName(), this.ids, this.mFields);
		}
		return oc;
	}

	@Override
	protected void onPostExecute(OpenErpConnect result) {
		// TODO Handle Read errors
		if (this.mData != null) {
			((ReadActivityInterface) activity).dataFetched();
		}
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

    public List<HashMap<String, Object>> getData() {
        return mData;
    }


    public String[] getFields() {
        return mFields;
    }

}
