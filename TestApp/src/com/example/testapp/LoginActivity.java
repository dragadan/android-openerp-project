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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.openerp.ConnectAsyncTask;
import com.openerp.OpenErpHolder;


public class LoginActivity extends Activity implements LoginActivityInterface {
	private static final int RESULT_SETTINGS = 1;
	public static final String PREFS_NAME = "MyPrefsFile";
    private String mLogin;
    private String mPassword;
	private String mServerHost;
	private String mServerPort;
	private String mServerDBName;
    private boolean mFirstRun;
    private RelativeLayout mRlContainer;
    private Button mLoginButton;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setActivities();
		this.loadPreferences();
        this.setLoginBtn();
	}

    private void setLoginBtn() {
        this.mRlContainer = new RelativeLayout(this);
        this.mLoginButton = new Button(this);
        this.mLoginButton.setText(getString(R.string.sSLogin));
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });
        this.mRlContainer.addView(this.mLoginButton);
        setContentView(this.mRlContainer);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

    public void setServerPort(String serverPort) {
        this.mServerPort = serverPort;
    }

    public void setServerHost(String serverHost) {
        this.mServerHost = serverHost;
    }

    public void setServerDBName(String serverDBName) {
        this.mServerDBName = serverDBName;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		// Handle item selection (only 1 option so we don't check item)
		viewSettings();
		return true;
	}

    public String getServerHost() {
        return mServerHost;
    }

    public String getServerPort() {
        return mServerPort;
    }

    public String getServerDBName() {
        return mServerDBName;
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
        this.mFirstRun = false;
        this.mLogin = settings.getString("login","");
        this.mPassword = settings.getString("password","");
		this.mServerHost = settings.getString("serverHost", "localhost");
		this.mServerPort = settings.getString("serverPort", "8069");
		this.mServerDBName = settings.getString("serverDBName", "");

		// Save Preferences to file
		settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("firstRun",this.mFirstRun);
        editor.putString("login",this.mLogin);
        editor.putString("password",this.mPassword);
		editor.putString("serverHost", this.mServerHost);
		editor.putString("serverPort", this.mServerPort);
		editor.putString("serverDBName", this.mServerDBName);

		// Commit the edits!
		editor.commit();

	}

	private void loadPreferences() {
		// Load preferences from files
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        this.mFirstRun = settings.getBoolean("firstRun",true);
        //this.mFirstRun = true; //REMOVE
        if (this.mFirstRun)
            viewSettings();
        else{
            this.mLogin = settings.getString("login","");
            this.mPassword = settings.getString("password","");
		    this.mServerHost = settings.getString("serverHost", "localhost");
		    this.mServerPort = settings.getString("serverPort", "8069");
		    this.mServerDBName = settings.getString("serverDBName", "");
        }
	}


	

	public void doLogin() {
		ConnectAsyncTask conAsT = new ConnectAsyncTask(this);
		conAsT.execute(mServerHost, mServerPort.toString(), mServerDBName, mLogin, mPassword);
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
                                    viewSettings();
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
