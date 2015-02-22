package com.roote.Fragments;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.roote.R;
import com.roote.Adapters.ImageAdapter;
import com.roote.Utils.RemoveFragmentListener;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class LoginFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login, container, false);

		// Setup page adapter
		ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
		ImageAdapter adapter = new ImageAdapter(view.getContext());
		viewPager.setAdapter(adapter);
		PageIndicator mIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
		((CirclePageIndicator) mIndicator).setFillColor(Color.parseColor(getString(R.color.red)));
		mIndicator.setViewPager(viewPager);

		// Request Permissions
		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.authButton);
		authButton.setFragment(this);
		authButton.setReadPermissions(Arrays.asList("email", "public_profile",
				"user_friends"));
		return view;
	}

	private static final String TAG = "MainFragment";
	private UiLifecycleHelper uiHelper;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	static RemoveFragmentListener removeListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Request.newMeRequest(session, new Request.GraphUserCallback() {

				@Override
				public void onCompleted(final GraphUser user, Response response) {
					if (user != null) {
						switchScreen();
					}
				}
			}).executeAsync();
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
		}
	}

	public static void switchScreen() {
		// Switch screen
		removeListener.onFragmentSuicide();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {

			removeListener = (RemoveFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new RuntimeException(
					getActivity().getClass().getSimpleName()
							+ " must implement the suicide listener to use this fragment",
					e);
		}
	}

	@Override
	public void onResume() {
		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}
}
