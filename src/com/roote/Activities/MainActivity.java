package com.roote.Activities;

import info.androidhive.slidingmenu.adapter.NavDrawerListAdapter;
import info.androidhive.slidingmenu.model.NavDrawerItem;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import yelp.YelpAPI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.roote.R;
import com.roote.csv.ReadCSV;
import com.roote.entity.Business;
import com.roote.entity.Importer;

@SuppressLint("InflateParams")
public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private Boolean isZoomed = false;
	private LinearLayout DrawerLinear;
	private ArrayList<Business> businesses;
	static Location location;
	private ArrayList<String> params;
	private Map<Integer, Business> mapPin;
	public static Marker marker;

	private int[] pinImages = new int[] { R.drawable.pin_food,
			R.drawable.pin_coffee, R.drawable.pin_grocery,
			R.drawable.pin_entertainment, R.drawable.pin_health,
			R.drawable.pin_mall, R.drawable.pin_clothing,
			R.drawable.pin_nightlife };
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
	private Activity activity;
	private Context context;
	private ProgressDialog dialog;
	private android.app.ActionBar actionbar;
	private ProgressBar spinner;

	class YelpTask implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			updateBusinessesList();
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					spinner.setVisibility(View.INVISIBLE);
				}
			});
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();
		actionbar = getActionBar();
		activity = this;
		setupActionBar();

		// Setup Guv Data
		dialog = ProgressDialog.show(MainActivity.this, "",
				"Loading Open Data. Please wait...", true);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Importer.importers = new ReadCSV().getImporters(context
						.getAssets());
				dialog.dismiss();
			}
		}).start();

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
					.getResourceId(i, -1), false));
		}

		// Recycle the typed array
		navMenuIcons.recycle();

		// Initialize params
		params = new ArrayList<String>();

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				CheckBox ch = (CheckBox) v.findViewById(R.id.checkbox);
				ch.setChecked(!ch.isChecked());
			}
		});

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
				ListView slidermenu = (ListView) view
						.findViewById(R.id.list_slidermenu);

				params.clear();
				for (int j = 0; j < slidermenu.getCount(); j++) {
					View v = (View) slidermenu.getChildAt(j);
					CheckBox ch = (CheckBox) v.findViewById(R.id.checkbox);
					if (ch.isChecked()) {
						params.add(navMenuTitles[j]);
					}
				}

				spinner.setVisibility(View.VISIBLE);
				new Thread(new YelpTask()).start();
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
									coordinates, 15));

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

						// Update Map
						updateBusinessesList();
					}
				}, delay, period);
			}
		});
	}

	private void setupActionBar() {
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setIcon(R.drawable.category);
		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.action_bar_title, null);
		spinner = (ProgressBar) v.findViewById(R.id.progressBar1);
		spinner.setVisibility(View.INVISIBLE);

		// ImageView titleTV = (ImageView) v.findViewById(R.id.title);
		// Typeface font = Typeface.createFromAsset(getAssets(),
		// "fonts/your_custom_font.ttf");
		// titleTV.setTypeface(font);

		actionbar.setCustomView(v);

		actionbar.setDisplayHomeAsUpEnabled(true);
	}

	private void updateBusinessesList() {
		// TODO Auto-generated method stub
		if (location != null) {
			double latitute = location.getLatitude();
			double longitude = location.getLongitude();

			businesses = new ArrayList<Business>();
			mapPin = new HashMap<Integer, Business>();

			if (params != null) {
				for (int i = 0; i < params.size(); i++) {
					ArrayList<Business> newBusinesses = YelpAPI.getBusinesses(
							params.get(i), latitute, longitude,
							context.getAssets());
					if (newBusinesses != null) {
						businesses.addAll(newBusinesses);
					}
				}
			}
			drawMap();
		}
	}

	private void drawMap() {
		// Get a handler that can be used to post to the
		// main thread
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Reload Map
				map.getMapAsync(new OnMapReadyCallback() {

					@Override
					public void onMapReady(GoogleMap googleMap) {
						// TODO Auto-generated method stub
						googleMap.clear();
						googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

						for (Business business : businesses) {

							double latitude = business.getLatitude();
							double longitude = business.getLongitude();
							String bName = business.getName();

							LatLng coord = new LatLng(latitude, longitude);
							Marker marker = null;

							for (int i = 0; i < navMenuTitles.length; i++) {
								if (business.getType() == navMenuTitles[i]) {
									BitmapDescriptor iconBitmap = BitmapDescriptorFactory
											.fromResource(pinImages[i]);

									marker = googleMap
											.addMarker(new MarkerOptions()
													.position(coord)
													.title(bName)
													.icon(iconBitmap));
									break;
								}
							}
							if (marker != null) {
								mapPin.put(marker.hashCode(), business);
							}
						}
					}
				});
			}
		});
	}

	class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		Marker marker;

		public DownloadImageTask(ImageView bmImage, Marker marker) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
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

	private class CustomInfoWindowAdapter implements InfoWindowAdapter {

		private View v;

		public CustomInfoWindowAdapter() {
			v = getLayoutInflater().inflate(R.layout.info_window_layout, null);
		}

		@Override
		public View getInfoWindow(Marker marker) {
			return null;
		}

		@Override
		public View getInfoContents(final Marker marker) {
			MainActivity.this.marker = marker;

			Business business = (Business) mapPin.get(marker.hashCode());

			TextView tvName = (TextView) v.findViewById(R.id.name);
			TextView tvType = (TextView) v.findViewById(R.id.type);
			TextView tvDeal = (TextView) v.findViewById(R.id.deal);
			ImageView ivImage = (ImageView) v.findViewById(R.id.image);
			TextView tvAddress = (TextView) v.findViewById(R.id.address);
			ImageView ivRating = (ImageView) v.findViewById(R.id.rating);

			if (business != null) {
				if (business.getName() != null) {
					tvName.setText(business.getName());
				}
				if (business.getType() != null) {
					tvType.setText(business.getType());
				}
				if (business.getAddress() != null) {
					tvAddress.setText(business.getAddress().toString());
				}
				if (business.getRatingImage() != null) {
					new DownloadImageTask(ivRating, marker).execute(business
							.getRatingImage());
				}
				if (business.getPhoto() != null) {
					new DownloadImageTask(ivImage, marker).execute(business
							.getPhoto());
				}
			}
			
			return v;
		}
	}
}
