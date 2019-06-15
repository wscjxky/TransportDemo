package com.example.a98.transportdemo.record_road;

import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_statellite)

public class SatelliteActivity extends BaseActivity implements AMapLocationListener {
    @ViewInject(R.id.map)
    private MapView mapView;
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
        locationClient.setLocationListener( this);


    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

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
}
