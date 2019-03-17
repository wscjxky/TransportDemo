package com.example.a98.transportdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;

import java.io.File;

import static android.media.MediaMetadataRetriever.OPTION_PREVIOUS_SYNC;

public class SecondActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_CAMERA=1;
    private Uri outputFileUri;
    private Button takevideo;
    private SeekBar sbVideo;
    private ImageView ivHead;
    private VideoView videoView;
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    boolean isTouch = false;
    int totalTime;
    int currentTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        sbVideo=findViewById(R.id.sb_select);
        ivHead=findViewById(R.id.iv_head);
        videoView = (VideoView) findViewById(R.id.videoView);
        takevideo=(Button)findViewById(R.id.take_video);
        takevideo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                File file = new File(getExternalCacheDir(),"test.mp4");
                outputFileUri = Uri.fromFile(file);

                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                // 录制视频最大时长15s
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        });
    }

    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        //读取直接返回的视频数据
        Uri uri = data.getData();
        videoView.setVideoURI(uri);

        mmr.setDataSource(this,uri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                totalTime = videoView.getDuration();//毫秒
                //显示第一帧图像
                Bitmap bitmap = mmr.getFrameAtTime((long) (currentTime * 1000), OPTION_PREVIOUS_SYNC);
                ivHead.setImageBitmap(bitmap);
            }

        });

        sbVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(isTouch){
                    currentTime = (int)(((float) progress / 100) * totalTime);
                    videoView.seekTo(currentTime);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTouch = false;
                //获取第一帧图像的bitmap对象 单位是微秒
                Bitmap bitmap = mmr.getFrameAtTime((long) (currentTime * 1000), OPTION_PREVIOUS_SYNC);
                ivHead.setImageBitmap(bitmap);
            }
        });

        videoView.start();
    }





}
