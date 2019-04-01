package com.example.a98.transportdemo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import com.example.a98.transportdemo.util.Share;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;


public class FirstActivity extends BaseActivity implements View.OnFocusChangeListener {
    public static final int TAKE_PHOTO = 1;//声明一个请求码，用于识别返回的结果
    private ImageView photo_view;
    private ImageView speak;
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
    @ViewInject(R.id.speak1)
    private ImageView speak1;
    @ViewInject(R.id.speak2)
    private ImageView speak2;
    @ViewInject(R.id.speak3)
    private ImageView speak3;
    @ViewInject(R.id.speak4)
    private ImageView speak4;
    @ViewInject(R.id.speak5)
    private ImageView speak5;

    private Uri imageUri;
    //存放听写分析结果文本
    private HashMap<String, String> hashMapTexts = new LinkedHashMap<String, String>();
    SpeechRecognizer hearer;  //听写对象
    RecognizerDialog dialog;  //讯飞提示框
    private boolean flag;
    private boolean exist;
    private TextView bridge;
    private EditText bridge2;

    @Event(value = {R.id.take_photo_xibei})
    private void takephoto(View view) {

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

    protected void setFocus(View v, Boolean hasFocus) {
        if (hasFocus) {
            // 得到焦点，便于语音输入确定位置
            bridge2 = (EditText) findViewById(R.id.bridge2);
            bridge2.setText("");
            bridge2 = (EditText) v;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        x.view().inject(this); //绑定注解
        photo_view = (ImageView) findViewById(R.id.take_photo);
        bridge = findViewById(R.id.bridge);
        bridge2 = findViewById(R.id.bridge2);
        speak1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                flag = true;
                // 语音配置对象初始化
                SpeechUtility.createUtility(FirstActivity.this, SpeechConstant.APPID + "=5b4f2581");

                // 1.创建SpeechRecognizer对象，第2个参数：本地听写时传InitListener
                hearer = SpeechRecognizer.createRecognizer(FirstActivity.this, null);
                // 交互动画
                dialog = new RecognizerDialog(FirstActivity.this, null);
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
                        StringBuffer strBuffer = new StringBuffer();
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
                        StringBuffer resultBuffer = new StringBuffer();  //最后结果
                        for (String key : hashMapTexts.keySet()) {
                            resultBuffer.append(hashMapTexts.get(key));
                        }
                        if (flag == true) {
                            bridge.setText(resultBuffer.toString());
                            bridge2.append(bridge.getText().toString());
                            bridge2.requestFocus();//获取焦点
                            bridge2.setSelection(bridge2.getText().length());//将光标定位到文字最后，以便修改
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

        });

        photo_view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
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
                if (Build.VERSION.SDK_INT >= 24)
                    //判断安卓的版本是否高于7.0，高于则调用高于的方法，低于则调用低于的方法
                    //把文件转换成Uri对象
                    /*
                    之所以这样，是因为android7.0以后直接使用本地真实路径是不安全的，会抛出异常。
                    FileProvider是一种特殊的内容提供器，可以对数据进行保护
                     */ {
                    imageUri = FileProvider.getUriForFile(FirstActivity.this,
                            "com.example.cameraalbumtest.fileprovider", outputImage);
                    /*
                    第一个参数：context对象
                    第二个参数：任意唯一的字符串
                    第三个参数：文件对象
                     */

                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                //使用隐示的Intent，系统会找到与它对应的活动，即调用摄像头，并把它存储
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
                //调用会返回结果的开启方式，返回成功的话，则把它显示出来
            }
        });
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


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.Text1:
                setFocus(text1, hasFocus);
                break;
            case R.id.Text2:
                setFocus(text2, hasFocus);
                break;
            case R.id.Text3:
                setFocus(text3, hasFocus);

                break;
            case R.id.Text4:
                setFocus(text4, hasFocus);
                break;
            case R.id.Text5:
                setFocus(text5, hasFocus);
                break;
            default:
                break;
        }
    }
}

