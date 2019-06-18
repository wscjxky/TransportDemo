package com.example.a98.transportdemo.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.MainActivity;
import com.example.a98.transportdemo.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.vondear.rxtool.RxDeviceTool;
import com.vondear.rxtool.RxNetTool;
import com.vondear.rxtool.RxPermissionsTool;
import com.vondear.rxtool.view.RxToast;
import com.vondear.rxui.view.RxProgressBar;
import com.vondear.rxui.view.dialog.RxDialogLoading;
import com.vondear.rxui.view.dialog.RxDialogSureCancel;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by Administrator on 2017/9/13.
 */

public class CheckVersionActivity extends BaseActivity {
    @ViewInject(R.id.round_flikerbar)
    private RxProgressBar mRoundFlikerbar;
    @ViewInject(R.id.btn_enter_main)
    private Button btn_enter_main;
    boolean update = false;
    private String check_url = ROOT_URL + "check.php";
    private String download_url = ROOT_URL + "download.php";
    private String version;
    private String temp;
    private double mRxRoundProgress = 0;
    private Activity mContext;

    @Event(R.id.btn_enter_main)
    private void enter_main(View view) {
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkversion);
        x.view().inject(this);
        mContext = CheckVersionActivity.this;
        version = RxDeviceTool.getAppVersionName(mContext);
        loadData();
        init_permission();

    }
    private void init_permission() {
        RxPermissionsTool.
                with(this).
                addPermission(Manifest.permission.ACCESS_FINE_LOCATION).
                addPermission(Manifest.permission.ACCESS_COARSE_LOCATION).
                addPermission(Manifest.permission.READ_EXTERNAL_STORAGE).
                addPermission(Manifest.permission.CAMERA).
                addPermission(Manifest.permission.CALL_PHONE).
                addPermission(Manifest.permission.INTERNET).
                addPermission(Manifest.permission.RECORD_AUDIO).
                addPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES).
                addPermission(Manifest.permission.RESTART_PACKAGES).
                addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).
                initPermission();

    }


    public void httpGet(String url, Callback.CommonCallback<?> callback) {
        Log.e("url", url);
        RequestParams params = new RequestParams(url);
        x.http().get(params, callback);
    }

    protected void loadData() {
        final RxDialogLoading rxDialogLoading = new RxDialogLoading(mContext);
        rxDialogLoading.show();
        rxDialogLoading.setLoadingText("正在检查更新,请稍等");
        if (RxNetTool.isAvailable(mContext)) {
            httpGet(check_url, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    log(result);
                    log(version);
                    if (!Objects.equals(version, result)) {
                        ShowDialog("您当前版本为-" + version + ", 发现新版本-" + result + "是否更新?");
                    } else {
                        show_toast("您是最新的版本.");
                        btn_enter_main.setText("最新版本,点击进入程序");


                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    log(ex.toString());
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    rxDialogLoading.cancel();

                }
            });
        } else {
            rxDialogLoading.cancel("网络错误,无法更新,请检查网络");
            btn_enter_main.setText("网络错误,请下次重启更新");
        }

    }


    /**
     * 检查是否有新版本，如果有就升级
     */


    private void ShowDialog(String text) {


        final RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(mContext);
        rxDialogSureCancel.setTitle("发现更新");
        rxDialogSureCancel.setContent(text);
        rxDialogSureCancel.setCancelable(false);
        rxDialogSureCancel.getSureView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(download_url);
                rxDialogSureCancel.cancel();
            }
        });
        rxDialogSureCancel.getCancelView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_toast("已经取消当前下载");
                btn_enter_main.setText("取消更新,点击进入软件");

                rxDialogSureCancel.cancel();
            }
        });
        rxDialogSureCancel.show();
    }


    public void getFile(String url) {
        Log.e("SDf", url);
        mRoundFlikerbar.setVisibility(View.VISIBLE);

//        mRoundFlikerbar.toggle();
        mRoundFlikerbar.setProgress(0);
//        rx_round_pd2.
        OkGo.<File>get(url)//
                .tag(this)//
                .execute(new FileCallback() {
                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                        RxToast.error("下载失败,请重试");
                    }

                    @Override
                    public void onSuccess(Response<File> response) {
                        // file 即为文件数据，文件保存在指定目录
                        RxToast.success("下载成功");
                        mRoundFlikerbar.finishLoad();
                        btn_enter_main.setText("更新完成,点击进入程序");

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        File apkFile = response.body();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            log(mContext.getPackageName());
                            Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", apkFile);
                            intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        } else {
                            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        }
                        startActivity(intent);

//                        File file = response.body();
//                        Intent i = new Intent(Intent.ACTION_VIEW);
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        i.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()), "application/vnd.android.package-archive");
//                        startActivity(i);
                    }  //文件下载时，可以指定下载的文件目录和文件名

                    @Override
                    public void downloadProgress(Progress progress) {
                        super.downloadProgress(progress);
                        float fraction = progress.fraction * 100;
                        BigDecimal b = new BigDecimal(fraction);
                        float f = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                        mRoundFlikerbar.setProgress(f);
                    }
                });
    }


}
