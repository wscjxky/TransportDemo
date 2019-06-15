package com.example.a98.transportdemo.record_point;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.LocateActivity;
import com.example.a98.transportdemo.R;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.yongchun.library.view.ImageSelectorActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class PhotoActivity extends BaseActivity {
    private Uri imageUri;
    //存放听写分析结果文本
    private HashMap<String, String> hashMapTexts = new LinkedHashMap<String, String>();
    SpeechRecognizer hearer;  //听写对象
    RecognizerDialog dialog;  //讯飞提示框
    private boolean flag;
    private boolean exist;
    private TextView bridge;
    private EditText bridge2;
    @Event(value = {R.id.button_submit})
    private void submit(View view) {
        Intent intent = new Intent(PhotoActivity.this, ImageSelectorActivity.class);
        startActivityForResult(intent, POINT_TAKE_PHOTO);

//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
//        intent.putExtra(Intent.EXTRA_TEXT, "交通" +
//                "数据表.xls");
//        //extraText为文本的内容
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(Intent.createChooser(intent, "分享"));

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        x.view().inject(this); //绑定注解

//        photo_view = (ImageView) findViewById(R.id.take_photo);
//        bridge = findViewById(R.id.bridge);
//        bridge2 = findViewById(R.id.bridge2);
//
//        bridge = (TextView) findViewById(R.id.bridge);
//        bridge2 = (EditText) findViewById(R.id.bridge2);

    }

    //    @Event(value = {R.id.button_submit})
//    private void submit(View view){
//        Toast.makeText(this,"bt1测试",Toast.LENGTH_LONG).show();
//    }
    //处理返回结果的函数，下面是隐示Intent的返回结果的处理方式，具体见以前我所发的intent讲解
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE){
            ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            // do something
        }
    }



}


