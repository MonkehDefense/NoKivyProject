package com.example.nokivyprojekt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
	private SensorManager sensorManager;
	private List<Sensor> deviceSensors;
	private WifiManager wifiManager;
	private int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	}

	
	public void warudoClick(View view){
		TextView hiZaWarudo = findViewById(R.id.hiZaWarudo);
		TextView wifiSigStrength = findViewById(R.id.wifiSigStrength);
		String name = deviceSensors.get(i).getName();
		hiZaWarudo.setText(name);
		
//		wifiManager.startScan();
//		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//		int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(),5);


//		wifiSigStrength.setText(level);
//		hiZaWarudo.setText("name");
		
		if(i == deviceSensors.size() - 1){
			i = 0;
		}else {
			i++;
		}
		
		Toast.makeText(this, "EJ, ŁAPY PRZY SOBIE!", Toast.LENGTH_SHORT).show();
	}

}