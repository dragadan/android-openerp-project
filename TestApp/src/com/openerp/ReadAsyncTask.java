package com.openerp;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.testapp.R;
import com.example.testapp.ReadActivityInterface;
import com.example.testapp.TreeActivity;

public class ReadAsyncTask extends AsyncTask<String, String, OpenErpConnect> {
	public ProgressDialog dialog;
	private Activity activity;
	private OpenErpConnect oc;
	private Long[] ids;
	List<HashMap<String, Object>> data;
	private String[] fields;

	public ReadAsyncTask(Activity act) {
		this.activity = act;
		this.ids = null;
		this.data = null;
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

	private void loadConnection() {
		this.oc = OpenErpHolder.oc;
	}

	/*
	 * Reads field values from model params -> fields
	 */
	@Override
	protected OpenErpConnect doInBackground(String... params) {
		loadConnection();
		this.fields = new String[params.length];
		System.arraycopy(params, 0, this.fields, 0, params.length);
		this.ids = oc.search(OpenErpHolder.modelName, new Object[0]);
		if (this.ids != null) {
			this.data = oc.read(OpenErpHolder.modelName, this.ids, this.fields);
		}
		return oc;
	}

	@Override
	protected void onPostExecute(OpenErpConnect result) {
		// TODO Handle Read errors
		if (this.data != null) {
			((ReadActivityInterface) activity).dataFetched(fields, data);
		}
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

}
