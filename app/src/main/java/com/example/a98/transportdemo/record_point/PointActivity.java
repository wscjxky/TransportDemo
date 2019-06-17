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

import com.example.a98.transportdemo.LocateActivity;
import com.example.a98.transportdemo.R;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.interfaces.XPopupCallback;
import com.vondear.rxtool.RxSPTool;

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


public class PointActivity extends LocateActivity {
    //    @ViewInject(R.id.take_photo)
//    private ImageView photo_view;
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
    @ViewInject(R.id.bridge2)
    private EditText bridge;

    //    @ViewInject(R.id.tv_bearing)
//    private TextView tv_bearing;
    private Uri imageUri;
    private ArrayList<EditText> table = new ArrayList<>();
    //存放听写分析结果文本
    private HashMap<String, String> hashMapTexts = new LinkedHashMap<String, String>();
    SpeechRecognizer hearer;  //听写对象
    RecognizerDialog dialog;  //讯飞提示框
    private boolean flag;
    @ViewInject(R.id.button_submit)
    private Button button_submit;

    @Event(value = {R.id.button_submit})
    private void submit(View view) {
        saveTable();
        Intent intent = new Intent(this, ImageSelectorActivity.class);
        startActivity(intent);
//        finish();
    }

    @Event(value = {R.id.btn_largeArea})
    private void calucate_area(View view) {
        Intent intent = new Intent(PointActivity.this, AreaActivity.class);
        startActivityForResult(intent, REQUEST_LARGE_AREA_ACTIVITY);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        x.view().inject(this); //绑定注解
        table.add(text1);
        table.add(text2);
        table.add(text3);
        table.add(text4);
        table.add(text5);
        table.add(tv_pollArea);
        setTable();
        initPop();

    }
    private void initPop(){
        XPopup.Builder pv = new XPopup.Builder(this)
                .atView(speak).setPopupCallback(new XPopupCallback() {
                    @Override
                    public void onShow() {
                    }

                    @Override
                    public void onDismiss() {
                        new XPopup.Builder(PointActivity.this)
                                .atView(button_submit)
                                .asAttachList(new String[]{"点击这里保存进入一下步"}, new int[]{},
                                        new OnSelectListener() {
                                            @Override
                                            public void onSelect(int position, String text) {
                                            }
                                        })
                                .show();
                    }
                });
        pv.asAttachList(new String[]{"点击这里打开语音输入"}, new int[]{},
                new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                    }
                })
                .show();
    }

    //    @Event(value = {R.id.button_submit})
//    private void submit(View view){
//        Toast.makeText(this,"bt1测试",Toast.LENGTH_LONG).show();
//    }
    //处理返回结果的函数，下面是隐示Intent的返回结果的处理方式，具体见以前我所发的intent讲解

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

    private void saveTable() {
        int count = 0;
        for (EditText et : table) {
            RxSPTool.putString(this, "text" + count, et.getText().toString());
            count += 1;
        }
    }

    private void setTable() {
        int count = 0;
        for (EditText et : table) {
            String text = RxSPTool.getString(this, "text" + count);
            count += 1;
            if (!text.isEmpty())
                et.setText(text);
        }
    }

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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {

        switch (requestCode) {
            case POINT_TAKE_PHOTO:
                String bearing_str = "";
                ArrayList<String> images = (ArrayList<String>) intent.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
                ArrayList<String> bearings = (ArrayList<String>) intent.getSerializableExtra("bearings");
                Bitmap bitmap = BitmapFactory.decodeFile(images.get(0));
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                //调整图片角度
                Matrix matrix = new Matrix();
                matrix.setRotate(90);
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//                    photo_view.setImageBitmap(b2);
                for (String bearing : bearings) {
                    bearing_str = bearing + ",";
                }
//                tv_bearing.setText(bearing_str);
                break;
            case REQUEST_LARGE_AREA_ACTIVITY:
                try {
                    String area = "1";
                    assert intent != null;
                    area = intent.getStringExtra("area");
                    tv_pollArea.setText(area);
                } catch (Exception e) {
                    break;
                }
                break;
            default:
                break;
        }
    }

}


