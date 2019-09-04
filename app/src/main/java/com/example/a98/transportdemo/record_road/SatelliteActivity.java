package com.example.a98.transportdemo.record_road;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.R;
import com.example.a98.transportdemo.util.CustomImageViewerPopup;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.XPopupImageLoader;
import com.tarek360.instacapture.Instacapture;
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener;
import com.vondear.rxtool.RxPhotoTool;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;

@ContentView(R.layout.activity_statellite)

public class SatelliteActivity extends BaseActivity implements AMapLocationListener {
    @ViewInject(R.id.map)
    private MapView mapView;
    @ViewInject(R.id.iv_statellite_screenshot)
    private ImageView iv_statellite_screenshot;
    @ViewInject(R.id.tv_position)
    private TextView tv_position;
    private AMapLocationClient locationClient = null;
    private AMap aMap;
    private MyLocationStyle myLocationStyle;
    private MediaProjectionManager mMediaProjectionManager;
    private AppCompatActivity mContext;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Event(R.id.iv_statellite_screenshot)
    private void screenshot(View v) {
//        mContext.getWindow().getDecorView().setDrawingCacheEnabled(true);
//        Bitmap bmp = mContext.getWindow().getDecorView().getDrawingCache();
//        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
//                getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//        startActivityForResult(
//                mediaProjectionManager.createScreenCaptureIntent(),
//                REQUEST_SCREENSHOT);
        Instacapture.INSTANCE.capture(SatelliteActivity.this, new SimpleScreenCapturingListener() {
            @Override
            public void onCaptureComplete(@NonNull Bitmap bitmap) {
                Uri uri = RxPhotoTool.createImagePathUri(mContext);
                String filename = RxPhotoTool.getRealFilePath(mContext, uri);
                writeJPEGFile(filename, bitmap);
                CustomImageViewerPopup viewerPopup = new CustomImageViewerPopup(mContext);
//自定义的ImageViewer弹窗需要自己手动设置相应的属性，必须设置的有srcView，url和imageLoader。
                viewerPopup.setSingleSrcView(iv_statellite_screenshot, uri);
                viewerPopup.setXPopupImageLoader(new ImageLoader());
                new XPopup.Builder(mContext)
                        .asCustom(viewerPopup)
                        .show();
                show_toast("截图成功,点击退出浏览,保存地址" +filename);

            }
        });


    }

    public void writeJPEGFile(String filename, Bitmap bitmap) { // 将在屏幕上绘制的图形保存到SD卡
        File outFile = new File(filename);
        log(outFile);

        try {
            FileOutputStream fos = new FileOutputStream(outFile); // 创建文件输出流（写文件）
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);// 将图片对象按PNG格式压缩（质量100%)，写入文件
            fos.flush(); // 刷新
            fos.close();// 关闭流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Event(R.id.iv_statellite_changemap)
    private void changemap(View v) {
        if (aMap.getMapType() == AMap.MAP_TYPE_SATELLITE) {
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 卫星地图模式
        } else {
            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statellite);
        x.view().inject(this);
        mapView.onCreate(savedInstanceState); // 此方法必须重写
        mContext = SatelliteActivity.this;
        initmap();
        initBlueP(aMap);

    }

    private void initmap() {
        if (aMap == null) {
            aMap = mapView.getMap();

        }
        locationClient = new AMapLocationClient(this);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        locationClient.setLocationOption(option);
        locationClient.setLocationListener(this);
        locationClient.startLocation();


    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
            log("签到成功，签到经纬度：(" + aMapLocation.getLatitude() + "," + aMapLocation.getLongitude() + ")");
            String curDes = "当前位置:(纬度,经度)\n("
                    + aMapLocation.getLatitude() + ","
                    + aMapLocation.getLongitude() + ")";
            tv_position.setText(curDes);
        } else {
            //可以记录错误信息，或者根据错误错提示用户进行操作，Demo中只是打印日志
            Log.e("AMap", "签到定位失败，错误码：" + aMapLocation.getErrorCode() + ", " + aMapLocation.getLocationDetail());
            //提示错误信息
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCREENSHOT && data != null) {


        }
    }

    public static class ImageLoader implements XPopupImageLoader {
        @Override
        public void loadImage(int position, @NonNull Object url, @NonNull ImageView imageView) {
            //必须指定Target.SIZE_ORIGINAL，否则无法拿到原图，就无法享用天衣无缝的动画
            Glide.with(imageView).load(url).apply(new RequestOptions().placeholder(R.mipmap.ic_launcher_round).override(Target.SIZE_ORIGINAL)).into(imageView);
        }

        @Override
        public File getImageFile(@NonNull Context context, @NonNull Object uri) {
            try {
                return Glide.with(context).downloadOnly().load(uri).submit().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
