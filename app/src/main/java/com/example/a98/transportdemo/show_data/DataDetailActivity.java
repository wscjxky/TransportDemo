package com.example.a98.transportdemo.show_data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vondear.rxfeature.module.wechat.share.WechatShareModel;
import com.vondear.rxfeature.module.wechat.share.WechatShareTools;
import com.vondear.rxtool.RxImageTool;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DataDetailActivity extends BaseActivity {
    private Context mContext;
    //
    @ViewInject(R.id.iv_data_photo)
    private ImageView iv_photo;

    @Event(R.id.btn_share)
    private void share(View v) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, "交通数据表");
        //extraText为文本的内容
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        DataDetailActivity.this.startActivity(Intent.createChooser(intent, "分享"));

        WechatShareTools.init(mContext, "wx10e48798def4a607");//初始化

        String url = "http://123.56.19.49/transport/";//网页链接

        String description = "分享成功！";//描述

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_ico);//获取Bitmap
        byte[] bitmapByte = RxImageTool.bitmap2Bytes(bitmap, Bitmap.CompressFormat.PNG);//将 Bitmap 转换成 byte[]

        WechatShareModel mWechatShareModel = new WechatShareModel(url, mContext.getString(
                R.string.app_name), description, bitmapByte);

        //Friend 分享微信好友,Zone 分享微信朋友圈,Favorites 分享微信收藏
        WechatShareTools.shareURL(mWechatShareModel, WechatShareTools.SharePlace.Friend);//分享操作

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datadetail);
        x.view().inject(this);
        mContext = DataDetailActivity.this;
        init();

    }

    private void init() {
        String jsonString = getIntent().getStringExtra("show_detail");
        Type type = new TypeToken<HashMap>() {
        }.getType();
        HashMap<String, String> data = new Gson().fromJson(jsonString, type);
        LinearLayout toplayout = (LinearLayout) findViewById(R.id.layout_datadetail);

        for (Object o : data.entrySet()) {
            log(o);
            Map.Entry entry = (Map.Entry) o;
            String key = entry.getKey().toString();
            String val = entry.getValue().toString();
            if (key.equals("图片")) {
                File file = new File(val);
                Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
                iv_photo.setImageBitmap(bitmap);
                continue;
            }
            LinearLayout linearLayout = new LinearLayout(DataDetailActivity.this);
            linearLayout.setBackgroundColor(0xFFFFFFFF);
            linearLayout.setPadding(8, 8, 8, 45);

            linearLayout.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            TextView textView = new TextView(DataDetailActivity.this);
            textView.setTextSize(20f);
            textView.setTypeface(null, Typeface.BOLD);

            LP_WW.weight = 1.0f;
            textView.setPadding(20, 0, 0, 0);
            textView.setText(key);
            textView.setLayoutParams(LP_WW);
            linearLayout.addView(textView);

            TextView tv = new TextView(DataDetailActivity.this); // 普通聊天对话
            tv.setPadding(0, 0, 20, 0);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextSize(20f);

            tv.setText(val);
            linearLayout.addView(tv);
            toplayout.addView(linearLayout);
        }
    }
}