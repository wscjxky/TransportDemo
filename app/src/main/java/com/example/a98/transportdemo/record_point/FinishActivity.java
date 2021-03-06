package com.example.a98.transportdemo.record_point;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.R;
import com.example.a98.transportdemo.show_data.ShowDataActivity;

import org.xutils.view.annotation.Event;
import org.xutils.x;


public class FinishActivity extends BaseActivity {
    @Event(R.id.btn_finish_finish)
    private void  finish_btn(View view){
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
//        intent.putExtra(Intent.EXTRA_TEXT, "交通" +
//                "数据表.xls");
//        //extraText为文本的内容
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivityForResult(Intent.createChooser(intent, "分享"),REQUEST_SHARE);
        Intent intent=new Intent(FinishActivity.this, ShowDataActivity.class);
        startActivity(intent);
        finish();

    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        x.view().inject(this); //绑定注解
    }
}
