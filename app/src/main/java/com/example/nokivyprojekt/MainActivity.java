package com.example.nokivyprojekt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
	public static final int default_update_interval = 30;
	public static final int fast_update_interval = 5;
	private static final int PERMISSIONS_FINE_LOCATION = 99;
	private static final int PERMISSIONS_COARSE_LOCATION = 98;
	TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address, tv_countOfCrumbs;
	Switch sw_locationupdates, sw_gps;
	Button btn_newWayPoint, btn_showWayPointList;

	boolean updateOn = false;

	Location currentLocation;
	List<Location> savedLocations;

	private LocationRequest locationRequest;
	private LocationRequest.Builder builder;
	private LocationCallback locationCallBack;
	private FusedLocationProviderClient fusedLocationProviderClient;

//	private SensorManager sensorManager;
//	private List<Sensor> deviceSensors;
//	private WifiManager wifiManager;
	private int upd = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//		deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
//		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		tv_lat = findViewById(R.id.tv_lat);
		tv_lon = findViewById(R.id.tv_lon);
		tv_altitude = findViewById(R.id.tv_altitude);
		tv_accuracy = findViewById(R.id.tv_accuracy);
		tv_speed = findViewById(R.id.tv_speed);
		tv_sensor = findViewById(R.id.tv_sensor);
		tv_updates = findViewById(R.id.tv_updates);
		tv_address = findViewById(R.id.tv_address);
		sw_gps = findViewById(R.id.sw_gps);
		sw_locationupdates = findViewById(R.id.sw_locationsupdates);
		btn_newWayPoint = findViewById(R.id.btn_newWayPoint);
		btn_showWayPointList = findViewById(R.id.btn_showWayPointList);
		tv_countOfCrumbs = findViewById(R.id.tv_countOfCrumbs);

//		set properties of LocationRequest
		builder = new LocationRequest.Builder(default_update_interval * 1000)
				.setMinUpdateIntervalMillis(fast_update_interval * 1000)
				.setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
		locationRequest = builder.build();

//		event triggered on update in time intervals
		locationCallBack = new LocationCallback() {
			@Override
			public void onLocationResult(@NonNull LocationResult locationResult) {
				super.onLocationResult(locationResult);

//				save location
				updateUIValues(locationResult.getLastLocation());
			}
		};


		btn_newWayPoint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				get gps location

//				add location to the global list
				MyApplication myApplication = (MyApplication) getApplicationContext();
				savedLocations = myApplication.getMyLocations();
				savedLocations.add(currentLocation);
			}
		});

		sw_gps.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (sw_gps.isChecked()) {
					builder.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
					locationRequest = builder.build();
					tv_sensor.setText("Using GPS sensors");
				} else {
					builder.setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
					locationRequest = builder.build();
					tv_sensor.setText("Using Towers + WIFI");
				}
			}
		});


		sw_locationupdates.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (sw_locationupdates.isChecked()) {
//					ON - turn on location tracking
					startLocationUpdates();
				} else {
//					OFF - turn off tracking
					stopLocationUpdates();
				}
			}
		});


		updateGPS();
	}


	private void startLocationUpdates() {
		tv_updates.setText("Location tracking ON");
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
				requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_COARSE_LOCATION);
				requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
			}
		}
		fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
		updateGPS();
//		if(sw_gps.isChecked()){
//			tv_sensor.setText("Using GPS sensors");
//		}else {
//			tv_sensor.setText("Using Towers + WIFI");
//		}
	}

	private void stopLocationUpdates() {
		tv_updates.setText("Location tracking OFF");
		tv_lat.setText("Location tracking OFF");
		tv_lon.setText("Location tracking OFF");
		tv_speed.setText("Location tracking OFF");
		tv_address.setText("Location tracking OFF");
		tv_accuracy.setText("Location tracking OFF");
		tv_altitude.setText("Location tracking OFF");
//		tv_sensor.setText("Location tracking OFF");

		fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch (requestCode){
			case PERMISSIONS_FINE_LOCATION:
				if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
					updateGPS();
				}else {
					Toast.makeText(this,"This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
					finish();
				}
			case PERMISSIONS_COARSE_LOCATION:
				if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
					updateGPS();
				}else {
					Toast.makeText(this,"This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
					finish();
				}
		}
	}


	private void updateGPS(){
		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

		if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
			fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
				@Override
				public void onSuccess(Location location) {
					updateUIValues(location);
					currentLocation = location;
				}
			});
		}else {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
				requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_COARSE_LOCATION);
				requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
			}
		}
	}

	private void updateUIValues(Location location) {
		tv_lat.setText(String.valueOf(location.getLatitude()));
		tv_lon.setText(String.valueOf(location.getLongitude()));
		tv_accuracy.setText(String.valueOf(location.getAccuracy()));

		if(location.hasAltitude()){
			tv_altitude.setText(String.valueOf(location.getAltitude()));
		}else {
			tv_altitude.setText("Not available " + upd);
			upd++;
		}


		if(location.hasSpeed()){
			tv_speed.setText(String.valueOf(location.getSpeed()));
		}else {
			tv_speed.setText("Not available");
		}

		Geocoder geocoder = new Geocoder(MainActivity.this);
		try {
			List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
			tv_address.setText(addresses.get(0).getAddressLine(0));
		}catch (Exception e){
			tv_address.setText("Unable to get Street Address");
		}

		MyApplication myApplication = (MyApplication) getApplicationContext();
		savedLocations = myApplication.getMyLocations();

		tv_countOfCrumbs.setText(Integer.toString(savedLocations.size()));
	}

//	public void warudoClick(View view){
//		TextView hiZaWarudo = findViewById(R.id.hiZaWarudo);
//		TextView wifiSigStrength = findViewById(R.id.wifiSigStrength);
//		String name = deviceSensors.get(i).getName();
//		hiZaWarudo.setText(name);
//
////		wifiManager.startScan();
////		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
////		int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(),5);
//
//
////		wifiSigStrength.setText(level);
////		hiZaWarudo.setText("name");
//
//		if(i == deviceSensors.size() - 1){
//			i = 0;
//		}else {
//			i++;
//		}
//
//		Toast.makeText(this, "EJ, ŁAPY PRZY SOBIE!", Toast.LENGTH_SHORT).show();
//	}

}