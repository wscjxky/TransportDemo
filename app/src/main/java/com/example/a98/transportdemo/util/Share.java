package com.example.a98.transportdemo.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.a98.transportdemo.R;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.vondear.rxfeature.module.wechat.share.WechatShareModel;
import com.vondear.rxfeature.module.wechat.share.WechatShareTools;
import com.vondear.rxtool.RxImageTool;

import static android.provider.UserDictionary.Words.APP_ID;

public class Share extends BroadcastReceiver {
    public static final String ACTION_SHARE_RESPONSE = "action_wx_share_response";
    private Context context;
    private IWXAPI api;

    @Override
    public void onReceive(Context context, Intent intent) {
        final IWXAPI api = WXAPIFactory.createWXAPI(context, null);

    }

    public interface OnResponseListener {
        void onSuccess();

        void onFail(String message);
    }


    public void share(Context mContext) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, "交通数据表");
        //extraText为文本的内容
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "分享"));

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


}
