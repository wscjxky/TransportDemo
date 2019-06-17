package com.example.a98.transportdemo.record_road;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;

@ContentView(R.layout.activity_statellite)

public class SatelliteActivity extends BaseActivity implements AMapLocationListener {
    @ViewInject(R.id.map)
    private MapView mapView;
    @ViewInject(R.id.tv_position)
    private TextView tv_position;

    private AMapLocationClient locationClient = null;
    private AMap aMap;
    private  MyLocationStyle myLocationStyle;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Event(R.id.iv_statellite_screenshot)
    private void screenshot(View v) {
//        mContext.getWindow().getDecorView().setDrawingCacheEnabled(true);
//        Bitmap bmp = mContext.getWindow().getDecorView().getDrawingCache();
        startActivityForResult(
                ((MediaProjectionManager) getSystemService("media_projection")).createScreenCaptureIntent(),1);
        show_toast("截图成功");
    }
    @Event(R.id.iv_statellite_changemap)
    private void changemap(View v) {
        if (aMap.getMapType()==AMap.MAP_TYPE_SATELLITE){
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 卫星地图模式
        }
        else{
            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statellite);
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
        locationClient.setLocationListener( this);
        locationClient.startLocation();


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
        }
    }
}
