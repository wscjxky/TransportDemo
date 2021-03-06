package com.example.a98.transportdemo.record_road;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.R;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import static com.example.a98.transportdemo.util.Calculate.gen_angle;
import static com.example.a98.transportdemo.util.Calculate.gen_double_string;

/**
 * AMapV2地图中简单介绍一些Marker的用法.
 */
public class AngleActivity extends BaseActivity implements AMapLocationListener, AMap.OnMarkerDragListener {
    @ViewInject(R.id.btn_angle)
    private Button btn_angle;
    @ViewInject(R.id.btn_add_point)
    private Button btn_add_point;
    @ViewInject(R.id.tv_position)
    private TextView tv_position;
    @ViewInject(R.id.map)
    private MapView mapView;
    private AMap aMap;
    public AMapLocationClientOption option = new AMapLocationClientOption();

    private AMapLocationClient locationClient = null;
    private MyLocationStyle myLocationStyle;
    private List<Marker> markers = new ArrayList<Marker>();
    private MarkerOptions markerOption;
    private double angle = 0.0;
    private LatLng current_positions;

    @SuppressLint("SetTextI18n")
    @Event(R.id.btn_cal_angle)
    private void cal_radius(View v) {
        try {
            List<LatLng> point_list = new ArrayList<>();
            for (Marker m : markers) {
                double lat = m.getPosition().latitude;
                double lng = m.getPosition().longitude;
                LatLng latLng = new LatLng(lat, lng);
                point_list.add(latLng);
            }
            angle = gen_angle(point_list);
            String p = gen_double_string(angle);
            btn_angle.setText(p);
            log(angle);
        } catch (Exception e) {
            show_toast("计算失败,请正确操作定位!");
        }


//        addMarkersToMap();

    }

    @Event(R.id.btn_clear_point)
    private void clear_pointw(View v) {
        if (aMap != null) {
            aMap.clear();
            if (!markers.isEmpty()) {
                markers.clear();

            }
        }
    }

    @Event(R.id.btn_add_point)
    private void add_point(View v) {
        if (current_positions != null) {
            addMarkersToMap(current_positions);
            show_toast("添加点成功");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_angle);
        x.view().inject(this);
        mapView.onCreate(savedInstanceState); // 此方法必须重写
        mContext=AngleActivity.this;
        initmap();
        initBlueP(aMap);
        initPop(mContext,btn_add_point,"添加新点,长按红点后可拖动,最少需要四个点位");

    }

    private void initmap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        locationClient = new AMapLocationClient(this);
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        locationClient.setLocationOption(option);
        //设置定位监听

        locationClient.setLocationListener(this);
        locationClient.startLocation();

    }





    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap(LatLng latlng) {
        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(latlng)
                .draggable(true)
                .visible(true);
        Marker marker = aMap.addMarker(markerOption);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latlng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        markers.add(marker);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        markers.remove(marker);
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        String curDes = marker.getTitle() + "当前位置:(lat,lng)\n("
                + marker.getPosition().latitude + ","
                + marker.getPosition().longitude + ")";
        addMarkersToMap(marker.getPosition());
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
            log("签到成功，签到经纬度：(" + aMapLocation.getLatitude() + "," + aMapLocation.getLongitude() + ")");
            String curDes = "当前位置:(纬度,经度)\n("
                    + aMapLocation.getLatitude() + ","
                    + aMapLocation.getLongitude() + ")";
            tv_position.setText(curDes);
            current_positions = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

        } else {
            //可以记录错误信息，或者根据错误错提示用户进行操作，Demo中只是打印日志
            Log.e("AMap", "签到定位失败，错误码：" + aMapLocation.getErrorCode() + ", " + aMapLocation.getLocationDetail());
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != locationClient) {
            locationClient.onDestroy();
        }

    }
}
