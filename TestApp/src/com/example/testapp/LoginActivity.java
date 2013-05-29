package com.example.testapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.openerp.ConnectAsyncTask;
import com.openerp.OpenErpHolder;


public class LoginActivity extends Activity implements LoginActivityInterface {
	private static final int RESULT_SETTINGS = 1;
	public static final String PREFS_NAME = "MyPrefsFile";
	public String serverHost;
	public String serverPort;
	public String serverDBName;
	protected Button btnLogin;
	protected EditText edtUser;
	protected EditText edtPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
        this.setActivities();
		this.findViews();
		this.loadPreferences();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerDBName(String serverDBName) {
        this.serverDBName = serverDBName;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		// Handle item selection (only 1 option so we don't check item)
		viewSettings();

		return true;
	}

    public String getServerHost() {
        return serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }

    public String getServerDBName() {
        return serverDBName;
    }

    public Button getBtnLogin() {
        return btnLogin;
    }

    public EditText getEdtUser() {
        return edtUser;
    }

    public EditText getEdtPassword() {
        return edtPassword;
    }

    private void viewSettings() {
		Intent i = new Intent(this, PrefsActivity.class);
		startActivityForResult(i, RESULT_SETTINGS);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		savePreferences();
	}

	private void savePreferences() {
		// Save Preferences to local variables
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		this.serverHost = settings.getString("serverHost", "localhost");
		this.serverPort = settings.getString("serverPort", "8069");
		this.serverDBName = settings.getString("serverDBName", "");

		// Save Preferences to file
		settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("serverHost", this.serverHost);
		editor.putString("serverPort", this.serverPort);
		editor.putString("serverDBName", this.serverDBName);

		// Commit the edits!
		editor.commit();
	}

	private void loadPreferences() {
		// Load preferences from files
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		this.serverHost = settings.getString("serverHost", "localhost");
		this.serverPort = settings.getString("serverPort", "8069");
		this.serverDBName = settings.getString("serverDBName", "");
	}

	private void findViews() {
		// Binds xml views to local variables
		btnLogin = (Button) findViewById(R.id.btnConnect_id);
		edtUser = (EditText) findViewById(R.id.edtUser);
		edtPassword = (EditText) findViewById(R.id.edtPassword);
	}
	

	public void doLogin(View view) {
		ConnectAsyncTask conAsT = new ConnectAsyncTask(this);
		conAsT.execute(serverHost, serverPort.toString(), serverDBName, edtUser.getText()
				.toString(), edtPassword.getText().toString());
		Log.d("Connecting...", "...");
	}

	/**
	 * Exit the app if user select yes.
	 */
	@Override
	public void onBackPressed() {

		doExit();
	}

	private void doExit() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				LoginActivity.this);

		alertDialog.setPositiveButton(getString(R.string.sYes), new OnClickListener() {

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

    @Override
    public void connectionResolved(Boolean result) {
        if(result){
            Intent i = new Intent(this, OpenErpHolder.getInstance().getmClassTreeActivity());
            startActivity(i);
            finish();
        }
        else{
            String failMsg = this.getString(R.string.sCheckSettings);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(this.getString(R.string.sFailTitle))
                    .setMessage(failMsg)
                    .setCancelable(false)
                    .setNegativeButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void setActivities() {
        OpenErpHolder.getInstance().setmClassTreeActivity(TreeActivity.class);
        OpenErpHolder.getInstance().setmClassFormActivity(FormActivity.class);
    }
}
