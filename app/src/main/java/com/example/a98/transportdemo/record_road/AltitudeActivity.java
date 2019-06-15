package com.example.a98.transportdemo.record_road;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.R;
import com.vondear.rxtool.RxLocationTool;
import com.vondear.rxtool.RxTextTool;
import com.vondear.rxtool.view.RxToast;
import com.vondear.rxui.activity.ActivityBaseLocation;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.a98.transportdemo.util.Calculate.gen_double_string;

public class AltitudeActivity extends ActivityBaseLocation implements AMapLocationListener {
    @ViewInject(R.id.tv_gps_info)
    private TextView tv_gps_info;
    @ViewInject(R.id.tv_res_altitude)
    private TextView tv_res_altitude;

    @ViewInject(R.id.tv_start_altitude)
    private TextView tv_start_altitude;
    @ViewInject(R.id.map)
    private MapView mapView;
    @ViewInject(R.id.tv_end_altitude)
    private TextView tv_end_altitude;
    public double start_altitiude = 0;
    private AMap aMap;
    private AMapLocationClient locationClient = null;
    public double end_altitiude = 0;

    @Event(R.id.btn_cal_altitude)
    private void cal(View v) {
        Double alt=(end_altitiude - start_altitiude);
        String p=gen_double_string(alt);
        tv_res_altitude.setText(p);
        RxToast.showToast("计算结果" + String.valueOf(end_altitiude - start_altitiude));
    }

    @Event(R.id.btn_clear_point)
    private void clear_point(View v) {
        if (aMap != null) {
            aMap.clear();
        }
        start_altitiude = 0;
        end_altitiude = 0;
        tv_end_altitude.setText("");
        tv_start_altitude.setText("");

    }
    private void initBlueP(){
        MyLocationStyle myLocationStyle=new MyLocationStyle();
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
//        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latlng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
    }


    @Event(R.id.btn_record_point)
    private void start_a(View v) {
        if (null != locationClient) {
            //签到只需调用startLocation即可
            locationClient.startLocation();
        }
    }

    @Override
    public void setGpsInfo(Location amapLocation) {
        String text = String.format("经度: %s  纬度: %s 海拔: %s", RxLocationTool.gpsToDegree(amapLocation.getLongitude()),
                RxLocationTool.gpsToDegree(amapLocation.getLatitude()), amapLocation.getAltitude());
        System.out.println(text);
        tv_gps_info.setText(text);
        Double lat = amapLocation.getLatitude();//获取纬度
        Double lng = amapLocation.getLongitude();//获取经度
        float accuracy = amapLocation.getAccuracy();//获取精度信息
        Float bearing = amapLocation.getBearing();//获取方向
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(amapLocation.getTime());
        df.format(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_altitude);
        x.view().inject(this);
        mapView.onCreate(savedInstanceState); // 此方法必须重写
        initmap();
        initBlueP();
    }

    private void initmap() {
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


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
            System.out.println("签到成功，签到经纬度：(" + aMapLocation.getLatitude() + "," + aMapLocation.getLongitude() + ")");
            DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String altitude=decimalFormat.format(aMapLocation.getAltitude());//format 返回的是字符串
            if (start_altitiude == 0) {
                start_altitiude = aMapLocation.getAltitude();
                tv_start_altitude.setText(String.valueOf(altitude));

            } else {
                end_altitiude = aMapLocation.getAltitude();
                tv_end_altitude.setText(String.valueOf(altitude));

            }
        } else {
            //可以记录错误信息，或者根据错误错提示用户进行操作，Demo中只是打印日志
            Log.e("AMap", "签到定位失败，错误码：" + aMapLocation.getErrorCode() + ", " + aMapLocation.getLocationDetail());
            //提示错误信息
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
