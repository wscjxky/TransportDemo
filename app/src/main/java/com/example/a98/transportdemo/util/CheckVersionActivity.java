package com.example.a98.transportdemo.util;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.a98.transportdemo.BaseActivity;
import com.vondear.rxtool.RxDeviceTool;

import org.xutils.common.Callback;

import java.io.File;
import java.util.Objects;

/**
 * Created by Administrator on 2017/9/13.
 */

public class CheckVersionActivity extends BaseActivity {
    boolean update = false;
    private String check_url=ROOT_URL+"download/";
    private String download_url=check_url+"download";
    private String version;
    private String temp;

    @Override
    public void initData() {
//
         temp = getResources().getString(R.string.newest_apk_down);
        version=RxDeviceUtils.getAppVersionName(CheckVersionActivity.this);
    }

    @Override
    public void loadData() {
        httpGet(check_url, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if( !Objects.equals(version, result)){
                    ShowDialog("您当前版本为"+ version+", 发现最新版本"+result+"是否更新?");
                }
                else{
                    showSuccToast("您是最新的版本啦，尽情享用吧");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                    LOG(ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_checkversion);
        StatusBarUtil.setTransparent(this);
    }




        /**
         * 检查是否有新版本，如果有就升级
         */


        private void ShowDialog(String strAppVersionName) {
            final SweetAlertDialog dialog= getConfirmDialog();
            dialog.setContentText(strAppVersionName);
            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    getFile(download_url);
                    dialog.cancel();
                }
            });
            dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    showErrorToast("已经取消当前下载");
                    dialog.cancel();
                }
            });
            dialog.show();
        }

        /** 例子
         * 下载APk文件并自动弹出安装
         */
    public void getFile(String url) {
        Log.e("SDf",url);
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("飞速下载中");
        pDialog.setCancelable(true);
        pDialog.show();
        final ProgressHelper helper=pDialog.getProgressHelper();
        OkGo.<File>get(url)//
                .tag(this)//
                .execute(new FileCallback() {
                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                        showErrorToast(R.string.checkInternet);
                    }
                    @Override
                    public void onSuccess(Response<File> response) {
                        // file 即为文件数据，文件保存在指定目录
                        showSuccToast("下载成功啦");
                        File file=response.body();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()), "application/vnd.android.package-archive");
                        startActivity(i);
                        pDialog.cancel();
                    }  //文件下载时，可以指定下载的文件目录和文件名

                    @Override
                    public void downloadProgress(Progress progress) {
                        super.downloadProgress(progress);
                        helper.setInstantProgress(progress.fraction);
                    }
                });
    }



}
