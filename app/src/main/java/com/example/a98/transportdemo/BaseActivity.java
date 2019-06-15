package com.example.a98.transportdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.vondear.rxtool.view.RxToast;

import org.xutils.x;

public abstract class BaseActivity extends AppCompatActivity {
    //添加 menu
    public static final int REQUEST_LARGE_AREA_ACTIVITY = 10;
    public static final int REQUEST_SHARE = 30;
    public static final int ROOT_URL = 30;

    public static final int POINT_TAKE_PHOTO = 21;
    public BaseActivity mContext;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        x.view().inject(this);
        mContext=this;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void log(Object str) {
        Log.e("debug_log", str.toString());

    }
    public void start(Class<?> obj){
        Intent intent=new Intent(mContext,obj);
        startActivity(intent);
        finish();
    }

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
    protected void show_toast(String string){
        RxToast.showToast(string);
    }

}
