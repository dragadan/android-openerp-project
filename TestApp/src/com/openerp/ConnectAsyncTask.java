package com.openerp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.testapp.LoginActivityInterface;
import com.example.testapp.R;

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
		this.oc = OpenErpHolder.getInstance().getmOConn();
	}

	@Override
	protected OpenErpConnect doInBackground(String... params) {
		loadConnection();
		oc = OpenErpConnect.connect(params[0], Integer.parseInt(params[1]), params[2],
				params[3], params[4]);
		return oc;
	}

	// This is called each time you call publishProgress()
	 @Override
	protected void onProgressUpdate(Integer... progress) {
         
     }
	 

	// This is called when doInBackground() is finished
	@Override
	protected void onPostExecute(OpenErpConnect result) {

		super.onPostExecute(null);
		if (result != null) {
			Log.d(this.getClass().getName(), "Connected");
			OpenErpHolder.getInstance().setmOConn(this.oc);
            ((LoginActivityInterface) activity).connectionResolved(true);
		} else {
			Log.d(this.getClass().getName(), "Failed connection");
            ((LoginActivityInterface) activity).connectionResolved(false);

		}
		if (this.dialog.isShowing()) {
			this.dialog.dismiss();
		}
	}
}