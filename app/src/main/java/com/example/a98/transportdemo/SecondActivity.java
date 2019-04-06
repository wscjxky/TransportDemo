package com.example.a98.transportdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.example.a98.transportdemo.data.VideoAdapter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.media.MediaMetadataRetriever.OPTION_CLOSEST;
import static android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC;
import static android.media.MediaMetadataRetriever.OPTION_PREVIOUS_SYNC;

@ContentView(R.layout.activity_second)

public class SecondActivity extends BaseActivity {
    public static final int REQUEST_CODE_CAMERA = 1;
    private Button takevideo;
    private SeekBar sbVideo;
    private ImageView ivHead;
    @ViewInject(R.id.video_view)
    private VideoView videoView;
    private List<Bitmap> bitmapList;
    private MediaMetadataRetriever mmr;
    boolean isTouch = false;
    long totalTime;
    private VideoAdapter videoAdapter;
    int currentTime;
    @ViewInject(R.id.video_gridview)
    private GridView video_gridview;

    @Event(R.id.take_video)
    private void take_video(View view) {
        File file = new File(getExternalCacheDir(), "test.mp4");
        Uri outputFileUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        bitmapList = new ArrayList<>();
        mmr = new MediaMetadataRetriever();
        videoAdapter = new VideoAdapter(this, bitmapList);
        video_gridview.setAdapter(videoAdapter);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                log("112");
                totalTime = videoView.getDuration();//毫秒
                get_avg_frames();

            }

        });
    }

    protected void get_avg_frames() {
        int segment = 8;
        String totalTime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Long t = Long.valueOf(totalTime);
        long per_sec = t / segment;
        log(t + "");
        log(per_sec + "");
        bitmapList.clear();
        for (int i = 0; i < segment; i++) {
            Bitmap bitmap = mmr.getFrameAtTime((i * 1000 * per_sec), OPTION_CLOSEST);
            log(i * 1000 * per_sec + "");
            bitmapList.add(bitmap);
        }
        videoAdapter.notifyDataSetChanged();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //读取直接返回的视频数据
        Uri uri = data.getData();
        videoView.setVideoURI(uri);
        mmr.setDataSource(this, uri);
        get_avg_frames();


    }
}
