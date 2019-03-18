package com.example.a98.transportdemo;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ThirdActivity extends AppCompatActivity {
    private TextView mLocation;
    private LocationManager locationManager;
    private String locationProvider;       //位置提供器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        mLocation = (TextView)findViewById(R.id.tv_location);
        getLocation(this);
    }

    private void getLocation(Context context){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            //1.获取位置管理器
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            //2.获取位置提供器，GPS或是NetWork
            List<String> providers = locationManager.getProviders(true);
            if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                //如果是网络定位
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
                //如果是GPS定位
                locationProvider = LocationManager.GPS_PROVIDER;
            } else {
                Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
                return;
            }

            //3.获取上次的位置，一般第一次运行，此值为null
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location != null) {
                showLocation(location);
            } else {
                // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
                locationManager.requestLocationUpdates(locationProvider, 0, 0, mListener);
            }
        }
        else{
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLocation(Location location){
        String address = "纬度："+location.getLatitude()+"\n经度："+location.getLongitude();
        mLocation.setText(address);
    }

    LocationListener mListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        // 如果位置发生变化，重新显示
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }
    };
}
