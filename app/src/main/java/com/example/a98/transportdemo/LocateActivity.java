package com.example.a98.transportdemo;

import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vondear.rxtool.RxSPTool;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LocateActivity  extends BaseActivity {
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    public AMapLocationListener mLocationListener =null;

    public AMapLocation amapLocation;
    public Float bearing = (float) 0;
    private Gson gson=new Gson();

    public String getBearing() {
        return Float.toString(bearing);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLocate();
    }
    public HashMap<String, String> getShowItem() {
        String js = RxSPTool.getContent(mContext, "show_item");
        Type type = new TypeToken<HashMap>() {
        }.getType();
        return gson.fromJson(js, type);
    }

    public void setShowItem(HashMap<String, String> showdata) {
        String jsonString = gson.toJson(showdata);
        RxSPTool.putContent(mContext, "show_item", jsonString);
    }

    public void addShowData(HashMap<String, String> showdata) {
        List<HashMap<String, String>> ori_show_data = getShowData();
        ori_show_data.add(showdata);
        String jsonString = gson.toJson(ori_show_data);
        RxSPTool.putContent(mContext, "show_data", jsonString);
    }

    public List<HashMap<String, String>> getShowData() {
        String js = RxSPTool.getContent(mContext, "show_data");
        Type type = new TypeToken<List<HashMap<String, String>>>() {
        }.getType();
        return gson.fromJson(js, type);
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



}
