package com.example.a98.transportdemo.show_data;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.R;
import com.google.gson.Gson;

import org.xutils.view.annotation.Event;
import org.xutils.x;

import java.util.List;
import java.util.Map;

public class DataDetailActivity extends BaseActivity {
    private Context mContext;
    //
//    @ViewInject(R.id.map)
    private List<ShowData> datalist;

    @Event(R.id.btn_finish)
    private void finish_activity(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_datadetail);
        x.view().inject(this);
        mContext = DataDetailActivity.this;
    }

    private void init() {
        String jsonString = getIntent().getStringExtra("show_detail");
        ShowData show_data = new Gson().fromJson(jsonString, ShowData.class);
        Map data = show_data.toMap();
        LinearLayout toplayout = findViewById(R.id.activity_datadetail);

        for (Object o : data.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String key = entry.getKey().toString();
            String val = entry.getValue().toString();
            LinearLayout linearLayout = new LinearLayout(mContext);

            LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            LP_WW.leftMargin = 20;
            LP_WW.weight = 1.0f;
            TextView textView = new TextView(mContext);
            textView.setText(key);
            textView.setLayoutParams(LP_WW);
            linearLayout.addView(textView);

            LP_WW = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            LP_WW.rightMargin = 20;
            TextView tv = new TextView(mContext); // 普通聊天对话
            tv.setText(val);
            linearLayout.addView(tv);

            toplayout.addView(linearLayout);
        }
    }
}