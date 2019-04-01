package com.example.a98.transportdemo.data;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.content.Context;
import android.widget.ImageView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

public class VideoAdapter extends BaseAdapter {

    private Context mContext;
    private List<Bitmap> bitmaps;
    private boolean flag = true;

    public VideoAdapter(Context c, List<Bitmap> bts) {
        mContext = c;
        bitmaps = bts;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return bitmaps.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub

        ImageView imageview;
        if (convertView == null) {
            imageview = new ImageView(mContext);
            imageview.setLayoutParams(new GridView.LayoutParams(250, 250));
            imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageview.setPadding(0, 1, 0, 1);

        } else {
            imageview = (ImageView) convertView;
            imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    parent.removeViewInLayout(v);
                    dialog.setContentView(v);
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
            return convertView;
        }

        imageview.setImageBitmap(bitmaps.get(position));

        return imageview;
    }


}
