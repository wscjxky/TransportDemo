package com.example.a98.transportdemo.record_point;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.TraceOverlay;
import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.R;
import com.example.a98.transportdemo.data.DbAdapter;
import com.example.a98.transportdemo.data.PathRecord;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.example.a98.transportdemo.util.Calculate.gen_area;

@ContentView(R.layout.activity_third)
public class AreaActivity extends BaseActivity implements com.amap.api.maps.LocationSource, AMapLocationListener
         {
    private com.amap.api.maps.AMap mAMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private PolylineOptions mPolyoptions;
    private Polyline mpolyline;
    private PathRecord record;
    private long mStartTime;
    private long mEndTime;
    @ViewInject(R.id.locationbtn)
    private ToggleButton btn;
    private DbAdapter DbHepler;
    @ViewInject(R.id.tv_level)
    private TextView tv_level;
    @ViewInject(R.id.map)
    private com.amap.api.maps.MapView mMapView;
    private LocationManager manager;


    private GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onGpsStatusChanged(int event) {
            if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
                ActivityCompat.checkSelfPermission(AreaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                GpsStatus gpsStatus = manager.getGpsStatus(null);
                int maxSatellites = gpsStatus.getMaxSatellites();
                Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                int count = 0;
                StringBuilder sb = new StringBuilder();
                while (iters.hasNext() && count <= maxSatellites) {
                    count++;
                    GpsSatellite s = iters.next();
                    float snr = s.getSnr();
                    sb.append("第").append(count).append("颗").append("：").append(snr).append("\n");
                }
                tv_level.setText("当前卫星颗数：" + count);
            }
        }
    };

    private TraceOverlay mTraceoverlay;

    private double area = 0.0;

    @Event(R.id.btn_finish)
    private void finish_activity(View v) {
        try {
//            float area = AMapUtils.calculateArea(leftTopLatlng, rightBottomLatlng);
//            area = Math.abs(area);
            Intent intent = getIntent();
            DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String p = decimalFormat.format(area);//format 返回的是字符串
            intent.putExtra("area", p);
            setResult(REQUEST_LARGE_AREA_ACTIVITY, intent);
            finish();
        } catch (Exception e) {
            log(e);
            Intent intent = getIntent();
            intent.putExtra("area", "0");
            setResult(REQUEST_LARGE_AREA_ACTIVITY, intent);
            finish();
        }

    }

    private List<LatLng> createRectangle(LatLng center, double halfWidth,
                                         double halfHeight) {
        List< LatLng> latLngs = new ArrayList< LatLng>();
        latLngs.add(new  LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
        latLngs.add(new  LatLng(center.latitude - halfHeight, center.longitude + halfWidth));
        latLngs.add(new  LatLng(center.latitude + halfHeight, center.longitude + halfWidth));
        latLngs.add(new  LatLng(center.latitude + halfHeight, center.longitude - halfWidth));
        return latLngs;
    }

    private void set_rect() {
        try {

            ArrayList<Double> lat_list = new ArrayList<>();
            ArrayList<Double> lng_list = new ArrayList<>();
            List<com.amap.api.maps2d.model.LatLng> point_list = new ArrayList<>();
            List<AMapLocation> pathline = record.getPathline();
            for (AMapLocation point : pathline) {//其内部实质上还是调用了迭代器遍历方式，这种循环方式还有其他限制，不建议使用。
                double lat = point.getLatitude();
                double lng = point.getLongitude();
                com.amap.api.maps2d.model.LatLng latLng = new com.amap.api.maps2d.model.LatLng(lat, lng);
                lat_list.add(lat);
                lng_list.add(lng);
                point_list.add(latLng);
            }
            area = gen_area(point_list);

            //        最大经度
            double right = Collections.max(lat_list);
            double left = Collections.min(lat_list);
////            //        最大纬度
            double top = Collections.max(lng_list);
            double bottom = Collections.min(lng_list);
//            double right = 116.341686;
//            double left = 116.341749;
//            double top = 39.950101;
//            double bottom = 39.950076;
            LatLng leftTopLatlng = new LatLng(top, left);
            LatLng rightBottomLatlng = new LatLng(bottom, right);
            LatLng leftBottomLatlng = new LatLng(bottom, left);
            LatLng rightTopLatlng = new LatLng(top, right);

            ArrayList<LatLng> rect = new ArrayList<>();
            rect.add(leftTopLatlng);rect.add(leftBottomLatlng);rect.add(rightBottomLatlng);rect.add(rightTopLatlng);
            log(rect);
            mAMap.addPolygon(new PolygonOptions().addAll(rect)
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(10, 255, 0, 0))
                    .strokeWidth(8));
        } catch (Exception e) {
            log(e);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        x.view().inject(this);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        mContext=AreaActivity.this;
        init();
        initpolyline();
        initPop(mContext,btn,"当卫星颗数较多,定位稳定时候,点击开始定位");
    }

    private void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
        }
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn.isChecked()) {
                    mAMap.clear(true);
                    if (record != null) {
                        record = null;
                    }
                    record = new PathRecord();
                    mStartTime = System.currentTimeMillis();
                    record.setDate(getcueDate(mStartTime));
                } else {
                    mEndTime = System.currentTimeMillis();
                    saveRecord(record.getPathline(), record.getDate());
                    set_rect();

                }
            }
        });

        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        assert manager != null;
        manager.addGpsStatusListener(gpsStatusListener);

    }

    protected void saveRecord(List<AMapLocation> list, String time) {
        if (list != null && list.size() > 0) {
            DbHepler = new DbAdapter(this);
            DbHepler.open();
            String duration = getDuration();
            float distance = getDistance(list);
            String average = getAverage(distance);
            String pathlineSring = getPathLineString(list);
            AMapLocation firstLocaiton = list.get(0);
            AMapLocation lastLocaiton = list.get(list.size() - 1);
            String stratpoint = amapLocationToString(firstLocaiton);
            String endpoint = amapLocationToString(lastLocaiton);
            DbHepler.createrecord(String.valueOf(distance), duration, average,
                    pathlineSring, stratpoint, endpoint, time);
            DbHepler.close();
        } else {
            Toast.makeText(AreaActivity.this, "没有记录到路径", Toast.LENGTH_SHORT).show();
        }
    }

    private String getDuration() {
        return String.valueOf((mEndTime - mStartTime) / 1000f);
    }

    private String getAverage(float distance) {
        return String.valueOf(distance / (float) (mEndTime - mStartTime));
    }

    private float getDistance(List<AMapLocation> list) {
        float distance = 0;
        if (list == null || list.size() == 0) {
            return distance;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            AMapLocation firstpoint = list.get(i);
            AMapLocation secondpoint = list.get(i + 1);
             LatLng firstLatLng = new  LatLng(firstpoint.getLatitude(),
                    firstpoint.getLongitude());
             LatLng secondLatLng = new  LatLng(secondpoint.getLatitude(),
                    secondpoint.getLongitude());
            double betweenDis = com.amap.api.maps.AMapUtils.calculateLineDistance(firstLatLng,
                    secondLatLng);
            distance = (float) (distance + betweenDis);
        }
        return distance;
    }

    private String getPathLineString(List<AMapLocation> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuilder pathline = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            AMapLocation location = list.get(i);
            String locString = amapLocationToString(location);
            pathline.append(locString).append(";");
        }
        String pathLineString = pathline.toString();
        pathLineString = pathLineString.substring(0,
                pathLineString.length() - 1);
        return pathLineString;
    }

    private String amapLocationToString(AMapLocation location) {
        return location.getLatitude() + "," +
                location.getLongitude() + "," +
                location.getProvider() + "," +
                location.getTime() + "," +
                location.getSpeed() + "," +
                location.getBearing();
    }

    private void initpolyline() {
        mPolyoptions = new  PolylineOptions();
        mPolyoptions.width(8f);
        mPolyoptions.color(Color.GRAY);



    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAMap.setMyLocationType(com.amap.api.maps.AMap.LOCATION_TYPE_LOCATE);
        mAMap.moveCamera(com.amap.api.maps.CameraUpdateFactory.zoomTo(18));

    }

    /**
     * 定位结果回调
     *
     * @param amapLocation 位置信息类
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                 LatLng mylocation = new  LatLng(amapLocation.getLatitude(),
                        amapLocation.getLongitude());
                mAMap.moveCamera(com.amap.api.maps.CameraUpdateFactory.changeLatLng(mylocation));
                if (btn.isChecked()) {
                    record.addpoint(amapLocation);
                    mPolyoptions.add(mylocation);
                    redrawline();

                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": "
                        + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 开始定位。
     */
    private void startlocation() {
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mLocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);

            mLocationOption.setInterval(2000);

            // 设置定位参数
            mLocationClient.setLocationOption(mLocationOption);

            mLocationClient.startLocation();

        }
    }

    /**
     * 实时轨迹画线
     */
    private void redrawline() {
        if (mPolyoptions.getPoints().size() > 1) {
            if (mpolyline != null) {
                mpolyline.setPoints(mPolyoptions.getPoints());
            } else {
                mpolyline = mAMap.addPolyline(mPolyoptions);
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getcueDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd  HH:mm:ss ");
        Date curDate = new Date(time);
        return formatter.format(curDate);
    }




    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        startlocation();
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();

        }
        mLocationClient = null;
    }

}
