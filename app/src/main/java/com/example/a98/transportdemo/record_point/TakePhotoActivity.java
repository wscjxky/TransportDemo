package com.example.a98.transportdemo.record_point;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.R;
import com.tencent.mm.opensdk.utils.Log;
import com.vondear.camera.RxCameraView;
import com.vondear.camera.tool.RxCameraTool;
import com.vondear.rxtool.RxBarTool;
import com.vondear.rxtool.RxFileTool;
import com.vondear.rxtool.RxLocationTool;
import com.vondear.rxtool.RxTimeTool;
import com.vondear.rxtool.RxTool;
import com.vondear.rxtool.interfaces.OnRxCamera;
import com.vondear.rxtool.view.RxToast;
import com.vondear.rxui.activity.ActivityBaseLocation;
import com.vondear.rxui.view.dialog.RxDialogScaleView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static com.example.a98.transportdemo.BaseActivity.POINT_TAKE_PHOTO;
import static com.example.a98.transportdemo.BaseActivity.REQUEST_LARGE_AREA_ACTIVITY;

public class TakePhotoActivity extends ActivityBaseLocation {

    @ViewInject(R.id.camera)
    RxCameraView mCameraView;
    @ViewInject(R.id.btn_take_camera)
    Button mBtnTakeCamera;
    @ViewInject(R.id.tv_gps)
    TextView mTvGps;
    @ViewInject(R.id.tv_gps_state)
    TextView mTvState;
    @ViewInject(R.id.iv_pic)
    ImageView mIvPic;
    private String bearing="东";
    private File photo;
    protected String outpout_file="" ;

    @Override
    public void setGpsInfo(Location location) {
        mTvGps.setText(String.format("经度: %s  纬度: %s\n精度: %s  方位: %s",
                RxLocationTool.gpsToDegree(location.getLongitude()), RxLocationTool.gpsToDegree(location.getLatitude()), location.getAccuracy(), location.getBearing()));
        bearing=tras_bearing(location.getBearing());

    }
    protected String tras_bearing(float bearing){
        if (bearing==0){
            return "北角";
        }
        if (bearing>0 && bearing<90){
            return "东北角";
        }
        if (bearing==90){
            return "东角";
        }
//        if (bearing>0 && bearing<50){
//            return "北角";
//        }
//        if (bearing>0 && bearing<50){
//            return "北角";
//        }
//        if (bearing>0 && bearing<50){
//            return "北角";
//        }
        return "";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBarTool.noTitle(this);
        RxBarTool.setTransparentStatusBar(this);
        setContentView(R.layout.activity_take_photo);
        x.view().inject(this);
        Intent intent = getIntent();
        // 获取Intent中的Bundle数据
        outpout_file=intent.getStringExtra(MediaStore.EXTRA_OUTPUT);
        initCamera();
        statusBarHide();
    }


    private void initCamera() {
        mCameraView.addCallback(new RxCameraView.Callback() {
            @Override
            public void onCameraOpened(RxCameraView cameraView) {
                super.onCameraOpened(cameraView);
            }

            @Override
            public void onCameraClosed(RxCameraView cameraView) {
                super.onCameraClosed(cameraView);
            }

            @Override
            public void onPictureTaken(RxCameraView cameraView, final byte[] data) {
                super.onPictureTaken(cameraView, data);
                try {
                    finish_take_photo(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void finish_take_photo(final byte[] data) throws IOException {
//        String fileDir = RxFileTool.getRootPath().getAbsolutePath() + File.separator + "RoadExcel" + File.separator + "picture";
//        String fileName = RxTimeTool.getCurrentDateTime("yyyyMMddHHmmss") + "_" + new Random().nextInt(1000) + ".jpg";
//        System.out.println(fileName);

//        FileOutputStream os = new FileOutputStream(getExternalCacheDir()+fileName);
        FileOutputStream os=new FileOutputStream(outpout_file);
        os.write(data);
        Log.e("",outpout_file);
        os.close();
//        photo = new File(getExternalCacheDir()+fileName);
//        FileInputStream fs = new FileInputStream(photo);
//        Bitmap bitmap= BitmapFactory.decodeStream(fs);
//        mIvPic.setImageBitmap(bitmap);
        Intent intent = getIntent();
        intent.putExtra("bearing", bearing);
        intent.putExtra("filename",outpout_file);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }


    @Event(R.id.btn_take_camera)
    private void take_camera(View view) {
        if (RxTool.isFastClick(1000)) {
            RxToast.normal("请不要重复点击拍照按钮");
            return;
        } else {
            RxCameraTool.takePic(TakePhotoActivity.this, mCameraView);
        }
    }

    @Event(R.id.iv_pic)
    private void show_pic(View view) {
        if (photo == null) {
            RxToast.normal("请先拍照");
        } else {
            RxDialogScaleView rxDialogScaleView = new RxDialogScaleView(mContext);
            rxDialogScaleView.setImage(photo.getAbsolutePath(), false);
            rxDialogScaleView.show();
        }
    }

    private   void statusBarHide(){
        View decorView = this.getWindow().getDecorView();
        decorView.setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        decorView.setSystemUiVisibility(SYSTEM_UI_FLAG_HIDE_NAVIGATION);


    }
}

