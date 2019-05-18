package com.example.a98.transportdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;


public class PointActivity extends LocateActivity  {
    public static final int TAKE_PHOTO = 1;
    public static final int TAKE_PHOTO_XI = 2;
    @ViewInject(R.id.take_photo)
    private ImageView photo_view;
    @ViewInject(R.id.Text1)
    private EditText text1;
    @ViewInject(R.id.Text2)
    private EditText text2;
    @ViewInject(R.id.Text3)
    private EditText text3;
    @ViewInject(R.id.Text4)
    private EditText text4;
    @ViewInject(R.id.Text5)
    private EditText text5;
    @ViewInject(R.id.speak)
    private ImageView speak;
    @ViewInject(R.id.speak1)
    private ImageView speak1;
    @ViewInject(R.id.speak2)
    private ImageView speak2;
    @ViewInject(R.id.speak3)
    private ImageView speak3;
    @ViewInject(R.id.speak4)
    private ImageView speak4;
    @ViewInject(R.id.iv_smallArea)
    private ImageView iv_smallArea;
    @ViewInject(R.id.btn_largeArea)
    private Button btn_largeArea;
    @ViewInject(R.id.tv_pollArea)
    private EditText tv_pollArea;
    @ViewInject(R.id.tv_bearing)
    private TextView tv_bearing;
    private Uri imageUri;
    //存放听写分析结果文本
    private HashMap<String, String> hashMapTexts = new LinkedHashMap<String, String>();
    SpeechRecognizer hearer;  //听写对象
    RecognizerDialog dialog;  //讯飞提示框
    private boolean flag;
    private boolean exist;
    private TextView bridge;
    private EditText bridge2;

    @Event(value = {R.id.speak})
    private void setSpeak(View view) {
        setRecoder(text1);
    }

    @Event(value = {R.id.speak1})
    private void setSpeak1(View view) {
        setRecoder(text2);
    }

    @Event(value = {R.id.speak2})
    private void setSpeak2(View view) {
        setRecoder(text3);
    }

    @Event(value = {R.id.speak3})
    private void setSpeak3(View view) {
        setRecoder(text4);
    }

    @Event(value = {R.id.speak4})
    private void setSpeak4(View view) {
        setRecoder(text5);
    }

    @Event(value = {R.id.iv_smallArea})
    private void setSpeak5(View view) {
        setRecoder(tv_pollArea);
    }



    @Event(value = {R.id.take_photo})
    private void takephoto2(View view) {
        takephoto(TAKE_PHOTO);
    }

    @Event(value = {R.id.button_submit})
    private void submit(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, "交通" +
                "数据表.xls");
        //extraText为文本的内容
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "分享"));

    }
    @Event(value = {R.id.btn_largeArea})
    private void calucate_area(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        startActivity(Intent.createChooser(intent, "分享"));

    }
    private void takephoto(int type) {
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try//判断图片是否存在，存在则删除在创建，不存在则直接创建
        {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(PointActivity.this,
                    "com.example.cameraalbumtest.fileprovider", outputImage);

        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        tv_bearing.setText(getBearing());
        startActivityForResult(intent, type);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        x.view().inject(this); //绑定注解
        photo_view = (ImageView) findViewById(R.id.take_photo);
        bridge = findViewById(R.id.bridge);
        bridge2 = findViewById(R.id.bridge2);

        bridge = (TextView) findViewById(R.id.bridge);
        bridge2 = (EditText) findViewById(R.id.bridge2);

    }

    //    @Event(value = {R.id.button_submit})
//    private void submit(View view){
//        Toast.makeText(this,"bt1测试",Toast.LENGTH_LONG).show();
//    }
    //处理返回结果的函数，下面是隐示Intent的返回结果的处理方式，具体见以前我所发的intent讲解
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        //调整图片角度
                        Matrix matrix = new Matrix();
                        matrix.setRotate(90);
                        Bitmap b2 = bitmap;
                        b2 = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                        photo_view.setImageBitmap(b2);
                        //将图片解析成Bitmap对象，并把它显现出来
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }


    private void setRecoder(final EditText edit) {
        flag = true;
        log("sdf");
        // 语音配置对象初始化
        SpeechUtility.createUtility(PointActivity.this, SpeechConstant.APPID + "=5b4f2581");

        // 1.创建SpeechRecognizer对象，第2个参数：本地听写时传InitListener
        hearer = SpeechRecognizer.createRecognizer(PointActivity.this, null);
        // 交互动画
        dialog = new RecognizerDialog(PointActivity.this, null);
        // 2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        hearer.setParameter(SpeechConstant.DOMAIN, "iat"); // domain:域名
        hearer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        hearer.setParameter(SpeechConstant.ACCENT, "mandarin"); // mandarin:普通话

        //3.开始听写
        dialog.setListener(new RecognizerDialogListener() {  //设置对话框
            @Override
            public void onResult(RecognizerResult results, boolean isLast) {
                Log.d("Result", results.getResultString());
                //(1) 解析 json 数据<< 一个一个分析文本 >>
                StringBuilder strBuffer = new StringBuilder();
                try {
                    JSONTokener tokener = new JSONTokener(results.getResultString());
                    Log.i("TAG", "Test" + results.getResultString());
                    Log.i("TAG", "Test" + results.toString());
                    JSONObject joResult = new JSONObject(tokener);

                    JSONArray words = joResult.getJSONArray("ws");
                    for (int i = 0; i < words.length(); i++) {
                        // 转写结果词，默认使用第一个结果
                        JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                        JSONObject obj = items.getJSONObject(0);
                        strBuffer.append(obj.getString("w"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //String text = strBuffer.toString();
                // (2)读取json结果中的sn字段
                String sn = null;

                try {
                    JSONObject resultJson = new JSONObject(results.getResultString());
                    sn = resultJson.optString("sn");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //(3) 解析语音文本<< 将文本叠加成语音分析结果  >>
                hashMapTexts.put(sn, strBuffer.toString());
                StringBuilder resultBuffer = new StringBuilder();  //最后结果
                for (String key : hashMapTexts.keySet()) {
                    resultBuffer.append(hashMapTexts.get(key));
                }
                if (flag) {
                    bridge.setText(resultBuffer.toString());
                    edit.append(bridge.getText().toString());
                    edit.requestFocus();//获取焦点
                    edit.setSelection(edit.getText().length());//将光标定位到文字最后，以便修改
                    flag = false;
                }
            }

            @Override
            public void onError(SpeechError error) {
                error.getPlainDescription(true);
            }
        });

        dialog.show();  //显示对话框

    }



}


