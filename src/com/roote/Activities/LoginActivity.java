package com.roote.Activities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;

import com.facebook.AppEventsLogger;
import com.facebook.model.GraphUser;
import com.roote.Fragments.LoginFragment;
import com.roote.Utils.RemoveFragmentListener;

public class LoginActivity extends FragmentActivity implements
		RemoveFragmentListener {
	private Fragment loginFragment;
	private GraphUser userInfo;
	private Boolean loggedin = false;

	public GraphUser getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(GraphUser userInfo) {
		this.userInfo = userInfo;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// ActionBar actionBar = getActionBar();
		// actionBar.hide()
		
		super.onCreate(savedInstanceState);
		try {
			PackageInfo info = getPackageManager()
					.getPackageInfo("com.roote",
							PackageManager.GET_SIGNATURES); // Your package name
															// here
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.i("KeyHash:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {
		}

		if (!isTaskRoot()) {
			finish();
			return;
		} else {
			if (savedInstanceState == null) {
				// Add the fragment on initial activity setup
				loginFragment = new LoginFragment();
				getSupportFragmentManager().beginTransaction()
						.add(android.R.id.content, loginFragment).commit();
			} else {
				// Or set the fragment from restored state info
				loginFragment = (LoginFragment) getSupportFragmentManager()
						.findFragmentById(android.R.id.content);
			}
		}
	}

	@Override
	public void onFragmentSuicide() {
		if (loggedin == false) {
			loggedin = true;
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Logs 'install' and 'app activate' App Events.
		AppEventsLogger.activateApp(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Logs 'app deactivate' App Event.
		AppEventsLogger.deactivateApp(this);
	}
}
