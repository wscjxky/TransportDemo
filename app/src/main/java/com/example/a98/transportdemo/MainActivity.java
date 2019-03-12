package com.example.a98.transportdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    private Button button1,button2,button3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1=(Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast tot = Toast.makeText(
                        MainActivity.this,
                        "1",
                        Toast.LENGTH_LONG);
                tot.show();
                Intent intent =new Intent(MainActivity.this,FirstActivity.class);
                startActivity(intent);
            }
        });

        button2=(Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast tot = Toast.makeText(
                        MainActivity.this,
                        "2",
                        Toast.LENGTH_LONG);
                tot.show();
            }
        });

        button3=(Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast tot = Toast.makeText(
                        MainActivity.this,
                        "3",
                        Toast.LENGTH_LONG);
                tot.show();
            }
        });
    }


}
