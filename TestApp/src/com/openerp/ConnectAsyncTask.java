package com.openerp;

import com.example.testapp.R;
import com.example.testapp.TreeActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class ConnectAsyncTask extends
		AsyncTask<String, Integer, OpenErpConnect> {
	public ProgressDialog dialog;
	private Activity activity;
	private OpenErpConnect oc;

	public ConnectAsyncTask(Activity act) {
		this.activity = act;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = new ProgressDialog(activity);
		dialog.setMessage(activity.getString(R.string.sConnecting) + "...");
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(true);
		dialog.show();
	}

	private void loadConnection() {
		this.oc = OpenErpHolder.oc;
	}

	protected OpenErpConnect doInBackground(String... params) {
		loadConnection();
		oc = OpenErpConnect.connect(params[0], Integer.parseInt(params[1]), params[2],
				params[3], params[4]);
		return oc;
	}

	// This is called each time you call publishProgress()
	 protected void onProgressUpdate(Integer... progress) {
         
     }
	 

	// This is called when doInBackground() is finished
	protected void onPostExecute(OpenErpConnect result) {
		String failMsg = activity.getString(R.string.sCheckSettings);
		super.onPostExecute(null);
		if (result != null) {
			Log.d(this.getClass().getName(), "Connected");
			Intent i = new Intent(activity, TreeActivity.class);
			OpenErpHolder.oc = this.oc;
			activity.startActivity(i);
			activity.finish();
		} else {
			Log.d(this.getClass().getName(), "Failed connection");
			//failMsg = activity.getString(R.string.sCheckLogin);
			

			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle(activity.getString(R.string.sFailTitle))
					.setMessage(failMsg)
					.setCancelable(false)
					.setNegativeButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
		if (this.dialog.isShowing()) {
			this.dialog.dismiss();
		}
	}
}