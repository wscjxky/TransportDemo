package com.example.a98.transportdemo.record_point;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a98.transportdemo.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.opensdk.utils.Log;
import com.vondear.camera.RxCameraView;
import com.vondear.camera.tool.RxCameraTool;
import com.vondear.rxtool.RxBarTool;
import com.vondear.rxtool.RxExifTool;
import com.vondear.rxtool.RxLocationTool;
import com.vondear.rxtool.RxSPTool;
import com.vondear.rxtool.RxTool;
import com.vondear.rxtool.view.RxToast;
import com.vondear.rxui.activity.ActivityBaseLocation;
import com.vondear.rxui.view.dialog.RxDialogScaleView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

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
    private String bearing = "北角";
    private File photo;
    protected String outpout_file = "";
    protected Double lat = 116.21345;
    protected Double lng = 39.1654;

    @Override
    public void setGpsInfo(Location location) {
        bearing = tras_bearing(location.getBearing());
        lng = location.getLongitude();
        lat = location.getLatitude();
        mTvGps.setText(String.format("经度: %s  纬度: %s\n精度: %s  方位: %s",
                RxLocationTool.gpsToDegree(lng), RxLocationTool.gpsToDegree(lat), location.getAccuracy(), bearing));
    }

    protected String tras_bearing(float bearing) {
        if (bearing == 0.0) {
            return "北角";
        } else if (bearing > 0.0 && bearing < 90.0) {
            return "东北角";
        } else if (bearing == 90.0) {
            return "东角";
        } else if (bearing > 90.0 && bearing < 180.0) {
            return "东角";
        } else if (bearing == 180) {
            return "东角";
        } else if (bearing > 270.0 && bearing < 270.0) {
            return "东角";
        } else if (bearing == 270.0) {
            return "东角";
        } else if (bearing > 270.0 && bearing < 360.0) {
            return "东角";
        } else {
            return "北角";
        }
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
        outpout_file = intent.getStringExtra(MediaStore.EXTRA_OUTPUT);
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

    private Gson gson = new Gson();

    private void finish_take_photo(final byte[] data) throws IOException {
//        String fileDir = RxFileTool.getRootPath().getAbsolutePath() + File.separator + "RoadExcel" + File.separator + "picture";
//        String fileName = RxTimeTool.getCurrentDateTime("yyyyMMddHHmmss") + "_" + new Random().nextInt(1000) + ".jpg";
//        System.out.println(fileName);

//        FileOutputStream os = new FileOutputStream(getExternalCacheDir()+fileName);
        FileOutputStream os = new FileOutputStream(outpout_file);
        os.write(data);
        Log.e("", outpout_file);
        RxExifTool.writeLatLonIntoJpeg(outpout_file, lat, lng);

        os.close();
//        photo = new File(getExternalCacheDir()+fileName);
//        FileInputStream fs = new FileInputStream(photo);
//        Bitmap bitmap= BitmapFactory.decodeStream(fs);
//        mIvPic.setImageBitmap(bitmap);
        Intent intent = getIntent();
        intent.putExtra("bearing", bearing);
        intent.putExtra("filename", outpout_file);
        HashMap<String, String> showdata = getShowItem();
        showdata.put("经度", RxLocationTool.gpsToDegree(lng));
        showdata.put("图片", outpout_file);
        showdata.put("纬度", RxLocationTool.gpsToDegree(lat));
        showdata.put("方位角", bearing);
        setShowItem(showdata);
        System.out.println(showdata);
        addShowData(showdata);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
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

    public void delShowData(HashMap<String, String> showdata) {
        List<HashMap<String, String>> ori_show_data = getShowData();
        ori_show_data.remove(showdata);
        String jsonString = gson.toJson(ori_show_data);
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

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }


    @Event(R.id.btn_take_camera)
    private void take_camera(View view) {
        if (RxTool.isFastClick(100)) {
            RxToast.normal("请不要重复点击拍照按钮");
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

    private void statusBarHide() {
        View decorView = this.getWindow().getDecorView();
        decorView.setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        decorView.setSystemUiVisibility(SYSTEM_UI_FLAG_HIDE_NAVIGATION);


    }
}

