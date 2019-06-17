package com.example.a98.transportdemo.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.a98.transportdemo.R;
import com.lxj.xpopup.core.ImageViewerPopupView;

public class CustomImageViewerPopup extends ImageViewerPopupView {
    public CustomImageViewerPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.custom_image_viewer_popup;
    }
}