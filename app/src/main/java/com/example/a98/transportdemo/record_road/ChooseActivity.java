package com.example.a98.transportdemo.record_road;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.R;
import com.example.a98.transportdemo.util.CheckVersionActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.x;

@ContentView(R.layout.activity_choose)

public class ChooseActivity extends BaseActivity {

    @Event(R.id.btn_satellite)
    private void btn_satellite(View v) {
        Intent intent=new Intent(ChooseActivity.this,SatelliteActivity.class);
        startActivity(intent);
    }
    @Event(R.id.btn_radius)
    private void btn_radius(View v) {
        Intent intent=new Intent(ChooseActivity.this,RadiusActivity.class);
        startActivity(intent);
    }
    @Event(R.id.btn_altitude)
    private void btn_altitude(View v) {
        Intent intent=new Intent(ChooseActivity.this,AltitudeActivity.class);
        startActivity(intent);
    }
    @Event(R.id.btn_angle)
    private void btn_angle(View v) {
        Intent intent=new Intent(ChooseActivity.this,AngleActivity.class);
        startActivity(intent);
    }
    @Event(R.id.btn_video)
    private void btn_video(View v) {
        Intent intent=new Intent(ChooseActivity.this,VideoActivity.class);
        startActivity(intent);
    }
    @Event(R.id.btn_checkver)
    private void btn_checkver(View v) {
        Intent intent=new Intent(ChooseActivity.this, CheckVersionActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        x.view().inject(this);

    }
    @Event(R.id.btn_finish)
    private void finish_activity(View v) {

    }

}
