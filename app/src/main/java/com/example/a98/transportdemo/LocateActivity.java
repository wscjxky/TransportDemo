package com.example.a98.transportdemo;

import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.MapView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocateActivity  extends BaseActivity {
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    public AMapLocationListener mLocationListener =null;

    public AMapLocation amapLocation;
    public Float bearing = (float) 0;

    public String getBearing() {
        return Float.toString(bearing);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLocate();
    }
    public void initLocate(){
        mLocationListener = new AMapLocationListener(){
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        Double lat=amapLocation.getLatitude();//获取纬度
                        Double lng=amapLocation.getLongitude();//获取经度
                        float accuracy=amapLocation.getAccuracy();//获取精度信息
                        String address=amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                        String city=amapLocation.getCity();//城市信息
                        String district=amapLocation.getDistrict();//城区信息
                        String street=amapLocation.getStreet();//街道信息
                        String streetNum=amapLocation.getStreetNum();//街道门牌号信息
                        int gpsAccuracyStatus=amapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
                        bearing=amapLocation.getBearing();//获取方向
                        double altitude=amapLocation.getAltitude();//获取G拔
                        float speed=amapLocation.getSpeed();//获取GPS的当前状态
                        log(lat);
                        log(lng);
                        log(speed);
                        log(altitude);
                        log(bearing);
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(amapLocation.getTime());
                        df.format(date);
                    }else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError","location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());

                    }
                }
            }
        };
        mLocationClient = new AMapLocationClient(this);
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        mLocationClient.setLocationOption(mLocationOption);
    }
    /**
     * 方法必须重写
     */


}
