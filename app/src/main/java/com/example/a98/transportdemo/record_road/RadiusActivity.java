package com.example.a98.transportdemo.record_road;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.LocateActivity;
import com.example.a98.transportdemo.R;
import com.vondear.rxtool.RxLocationTool;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import static com.example.a98.transportdemo.util.Calculate.gen_angle;
import static com.example.a98.transportdemo.util.Calculate.gen_circle;
import static com.example.a98.transportdemo.util.Calculate.gen_double_string;
import static com.example.a98.transportdemo.util.Calculate.gen_radius;

/**
 * AMapV2地图中简单介绍一些Marker的用法.
 */
public class RadiusActivity extends BaseActivity implements AMapLocationListener,AMap.OnMarkerDragListener {
    @ViewInject(R.id.tv_radius)
    private TextView tv_radius;
    @ViewInject(R.id.tv_position)
    private TextView tv_position;
    @ViewInject(R.id.map)
    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient locationClient = null;
    private  MyLocationStyle myLocationStyle;
    private List<Marker> markers=new ArrayList<Marker>();
    private MarkerOptions markerOption;
    private float circle_radius=0;
    private LatLng  circle_point = new LatLng(0, 0);

    private LatLng latlng = new LatLng(39.761, 116.434);
    @Event(R.id.btn_cal_radius)
    private void cal_radius(View v) {
        List<LatLng> point_list = new ArrayList<>();
        for(Marker m : markers) {
            double lat = m.getPosition().latitude;
            double lng = m.getPosition().longitude;
            LatLng latLng = new LatLng(lat, lng);
            point_list.add(latLng);
        }
        circle_point = gen_circle(point_list);
//        circle_radius = gen_radius(point_list);
        addMarkersToMap(circle_point);
        log(circle_radius);

        circle_radius = AMapUtils.calculateLineDistance(circle_point, markers.get(0).getPosition());
        double c = circle_radius;
        String p=gen_double_string(c);
        tv_radius.setText(p);
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
        log("正在获取位置...");
        if(null != locationClient) {
            //签到只需调用startLocation即可
            locationClient.startLocation();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radius);
        x.view().inject(this);
        mapView.onCreate(savedInstanceState); // 此方法必须重写
        initmap();
        initBlueP(aMap);

    }
    private void initmap(){
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        locationClient = new AMapLocationClient(this);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        locationClient.setLocationOption(option);
        //设置定位监听
        locationClient.setLocationListener(this);
        locationClient.startLocation();

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
        if(null != locationClient){
            locationClient.onDestroy();
        }

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
        Marker marker=aMap.addMarker(markerOption);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latlng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
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
//        tv_position.setText(curDes);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
            log("签到成功，签到经纬度：(" + aMapLocation.getLatitude() + "," + aMapLocation.getLongitude()+ ")");
            String curDes = "当前位置:(纬度,经度)\n("
                    + aMapLocation.getLatitude()+ ","
                    + aMapLocation.getLongitude() + ")";
            tv_position.setText(curDes);

        } else {
            //可以记录错误信息，或者根据错误错提示用户进行操作，Demo中只是打印日志
            Log.e("AMap", "签到定位失败，错误码：" + aMapLocation.getErrorCode() + ", " + aMapLocation.getLocationDetail());
            //提示错误信息
            log("签到失败");
        }
    }
}
