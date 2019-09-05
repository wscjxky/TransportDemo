package com.example.a98.transportdemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.vondear.rxtool.RxSPTool;
import com.vondear.rxtool.view.RxToast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {
    //添加 menu
    public static final int REQUEST_LARGE_AREA_ACTIVITY = 10;
    public static final int REQUEST_SHARE = 30;
    public static final String ROOT_URL = "http://123.56.19.49/app/";
    public static final int POINT_TAKE_PHOTO = 21;
    public static final int REQUEST_SCREENSHOT = 41;
    protected Context mContext;
    private Gson gson = new Gson();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    public void log(Object str) {
        Log.e("debug_log", str.toString());

    }

    public HashMap<String, String> getShowItem() {
        String js = RxSPTool.getContent(mContext, "show_item");

        if (js == null || js.isEmpty())
            return new HashMap<String, String>();
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

    public void updateShowData(List<HashMap<String, String>> showdata) {
//        List<HashMap<String, String>> ori_show_data = getShowData();
//        System.out.println(ori_show_data);
//        ori_show_data.remove(showdata);
        String jsonString = gson.toJson(showdata);
        RxSPTool.putContent(mContext, "show_data", jsonString);
    }

    public List<HashMap<String, String>> getShowData() {
        String js = RxSPTool.getContent(mContext, "show_data");
        if (js == null || js.isEmpty()) {
            return new ArrayList<HashMap<String, String>>();
        }
        Type type = new TypeToken<List<HashMap<String, String>>>() {
        }.getType();

        return gson.fromJson(js, type);
    }

    protected void initPop(final Context context, final View view, final String text) {
        new XPopup.Builder(context)
                .atView(view)
                .asAttachList(new String[]{text}, new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                            }
                        })
                .show();
    }
//    public void start(Class<?> obj){
//        Intent intent=new Intent(mContext,obj);
//        startActivity(intent);
//        finish();
//    }

    //menu的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.about_us:
                Toast.makeText(this, "联系我们", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    protected void show_toast(String string) {
        RxToast.showToast(string);
    }

    public void initBlueP(AMap aMap) {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
//        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latlng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));

    }
}
