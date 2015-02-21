package com.roote.Activities;

import info.androidhive.slidingmenu.adapter.NavDrawerListAdapter;
import info.androidhive.slidingmenu.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import yelp.YelpAPI;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.roote.R;
import com.roote.entity.Business;

public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private Boolean isZoomed = false;
	private LinearLayout DrawerLinear;
	private ArrayList<Business> businesses;
	static Location location;
	Timer timer = new Timer();
	int delay = 5000; // delay for 5 sec.
	int period = 100000; // repeat every 10 secs.

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	public MapFragment map;
	private LocationManager locationManager;
	private Context context;
	private Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();
		activity = this;
		
		DrawerLinear = (LinearLayout) findViewById(R.id.DrawerLinear);
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		for (int i = 0; i < navMenuTitles.length; i++) {
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons
					.getResourceId(i, -1)));
		}

		// Recycle the typed array
		navMenuIcons.recycle();

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// Setup GPS
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Get Map
		map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		map.getMapAsync(new OnMapReadyCallback() {

			@Override
			public void onMapReady(final GoogleMap gmap) {
				gmap.setMyLocationEnabled(true);

				// Define a listener that responds to location updates
				LocationListener locationListener = new LocationListener() {
					public void onLocationChanged(Location loc) {
						// Called when a new location is found by the network
						// location
						// provider.
						location = new Location(loc);

						if (isZoomed == false) {
							double latitude = loc.getLatitude();
							double longitude = loc.getLongitude();

							// Log.i("TAG", Double.toString(latitude));
							// Log.i("TAG", Double.toString(longitude));
							LatLng coordinates = new LatLng(latitude, longitude);
							gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(
									coordinates, 17));

							isZoomed = true;
						}
					}

					public void onStatusChanged(String provider, int status,
							Bundle extras) {
					}

					public void onProviderEnabled(String provider) {
					}

					public void onProviderDisabled(String provider) {
					}
				};

				// Register the listener with the Location Manager to receive
				// location
				// updates
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0,
						locationListener);

				// Request Yelp
				// Load Yelp companies
				timer.scheduleAtFixedRate(new TimerTask() {

					public void run() {

						if (location != null) {
							double latitute = location.getLatitude();
							double longitude = location.getLongitude();

							businesses = YelpAPI.getBusinesses("coffee",
									latitute, longitude);
							// for (Business business : businesses) {
							// Log.i("Yelp", business.toString());
							// }

							// Get a handler that can be used to post to the
							// main thread
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// Reload Map
									map.getMapAsync(new OnMapReadyCallback() {

										@Override
										public void onMapReady(
												GoogleMap googleMap) {
											// TODO Auto-generated method stub
											googleMap.clear();
											
											for (Business business : businesses) {

												double latitude = business
														.getLatitude();
												double longitude = business
														.getLongitude();
												String bName = business
														.getName();

												LatLng coord = new LatLng(
														latitude, longitude);

												googleMap
														.addMarker(new MarkerOptions()
																.position(coord)
																.title(bName));
												// .icon(mapPoint.getMarker())
												// .snippet(mapPoint.getInfoSnippetText()));
											}
										}
									});
								}
							});
						}
					}

				}, delay, period);
			}
		});
	}

	/**
	 * Slide menu item click listener
	 * */
	/*
	 * private class SlideMenuClickListener implements
	 * ListView.OnItemClickListener {
	 * 
	 * @Override public void onItemClick(AdapterView<?> parent, View view, int
	 * position, long id) { // display view for selected nav drawer item
	 * displayView(position); } }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(DrawerLinear);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
}
