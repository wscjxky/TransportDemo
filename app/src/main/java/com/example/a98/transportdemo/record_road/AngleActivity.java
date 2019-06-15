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
import static com.example.a98.transportdemo.util.Calculate.gen_area;
import static com.example.a98.transportdemo.util.Calculate.gen_double_string;

/**
 * AMapV2地图中简单介绍一些Marker的用法.
 */
public class AngleActivity extends BaseActivity implements AMapLocationListener,AMap.OnMarkerDragListener {
    @ViewInject(R.id.btn_angle)
    private Button btn_angle;
    @ViewInject(R.id.tv_position)
    private Button tv_position;
    @ViewInject(R.id.map)
    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient locationClient = null;
    private  MyLocationStyle myLocationStyle;
    private List<Marker> markers=new ArrayList<Marker>();
    private MarkerOptions markerOption;
    private  double angle=0.0;
    @SuppressLint("SetTextI18n")
    @Event(R.id.btn_cal_angle)
    private void cal_radius(View v) {
        List<LatLng> point_list = new ArrayList<>();
        for(Marker m : markers) {
                double lat = m.getPosition().latitude;
                double lng = m.getPosition().longitude;
                LatLng latLng = new LatLng(lat, lng);
                point_list.add(latLng);
            }
            angle = gen_angle(point_list);
        String p=gen_double_string(angle);
        tv_position.setText(p);
        log(angle);
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
        log("正在获取位置...");
        if(null != locationClient) {
            //签到只需调用startLocation即可
            locationClient.startLocation();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_angle);
        x.view().inject(this);
        mapView.onCreate(savedInstanceState); // 此方法必须重写
        initmap();
        initBlueP();
    }
    private void initmap(){
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        locationClient = new AMapLocationClient(this);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        locationClient.setLocationOption(option);
        //设置定位监听
        locationClient.setLocationListener(this);

    }
    private void initBlueP(){
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));

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
        tv_position.setText(curDes);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
            log("签到成功，签到经纬度：(" + aMapLocation.getLatitude() + "," + aMapLocation.getLongitude()+ ")");
            addMarkersToMap(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()));

        } else {
            //可以记录错误信息，或者根据错误错提示用户进行操作，Demo中只是打印日志
            Log.e("AMap", "签到定位失败，错误码：" + aMapLocation.getErrorCode() + ", " + aMapLocation.getLocationDetail());
            //提示错误信息
            log("签到失败");
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
        if(null != locationClient){
            locationClient.onDestroy();
        }

    }
}
