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

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class FirstActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO=1;//声明一个请求码，用于识别返回的结果
    private ImageView picture;
    private Button takephoto;
    private Button speak;
    private EditText name;
    private EditText addr;
    private Uri imageUri;

    //存放听写分析结果文本
    private HashMap<String, String> hashMapTexts = new LinkedHashMap<String, String>();
    SpeechRecognizer hearer;  //听写对象
    RecognizerDialog dialog;  //讯飞提示框
    private boolean flag;
    private boolean exist;
    private TextView bridge;
    private EditText bridge2;

    private int w,h;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        takephoto=(Button)findViewById(R.id.take_photo);
        picture=findViewById(R.id.picture);
        ViewGroup.LayoutParams para;
        para = picture.getLayoutParams();
//        para.height = 500;
//        para.width = 500;
//        picture.setLayoutParams(para);
        w = para.width;
        h = para.height;

        name = (EditText) findViewById(R.id.TextName);
        addr = (EditText) findViewById(R.id.TextAddr);
        bridge = (TextView) findViewById(R.id.bridge);
        bridge2 = (EditText) findViewById(R.id.bridge2);

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 得到焦点，便于语音输入确定位置
                    bridge2 = (EditText) findViewById(R.id.bridge2);
                    bridge2.setText("");
                    bridge2 = name;
                }
            }
        });

        addr.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 得到焦点，便于语音输入确定位置
                    bridge2 = (EditText) findViewById(R.id.bridge2);
                    bridge2.setText("");
                    bridge2 = addr;
                }
            }
        });

        speak = findViewById(R.id.speak);
        speak.setOnClickListener(new View.OnClickListener() {

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

        takephoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                File outputImage=new File(getExternalCacheDir(),"output_image.jpg");
                /*
                创建一个File文件对象，用于存放摄像头拍下的图片，我们把这个图片命名为output_image.jpg
                并把它存放在应用关联缓存目录下，调用getExternalCacheDir()可以得到这个目录，为什么要
                用关联缓存目录呢？由于android6.0开始，读写sd卡列为了危险权限，使用的时候必须要有权限，
                应用关联目录则可以跳过这一步
                 */
                try//判断图片是否存在，存在则删除在创建，不存在则直接创建
                {
                    if(outputImage.exists())
                    {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24)
                    //判断安卓的版本是否高于7.0，高于则调用高于的方法，低于则调用低于的方法
                    //把文件转换成Uri对象
                    /*
                    之所以这样，是因为android7.0以后直接使用本地真实路径是不安全的，会抛出异常。
                    FileProvider是一种特殊的内容提供器，可以对数据进行保护
                     */
                {
                    imageUri= FileProvider.getUriForFile(FirstActivity.this,
                            "com.example.cameraalbumtest.fileprovider",outputImage);
                    /*
                    第一个参数：context对象
                    第二个参数：任意唯一的字符串
                    第三个参数：文件对象
                     */

                }
                else {
                    imageUri=Uri.fromFile(outputImage);
                }
                //使用隐示的Intent，系统会找到与它对应的活动，即调用摄像头，并把它存储
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
                //调用会返回结果的开启方式，返回成功的话，则把它显示出来
            }
        });
    }

    //处理返回结果的函数，下面是隐示Intent的返回结果的处理方式，具体见以前我所发的intent讲解
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode)
        {
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK)
                {
                    try
                    {
                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        //调整图片角度
                        Matrix matrix = new Matrix();
                        matrix.setRotate(90);
                        Bitmap b2 = bitmap;
                        b2 = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                        picture.setImageBitmap(b2);
                        //将图片解析成Bitmap对象，并把它显现出来
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }


}

