package com.example.a98.transportdemo;
import android.app.Application;
import android.graphics.Bitmap;
import com.vondear.rxtool.RxTool;
import org.xutils.x;

public class App extends Application {
    private Bitmap mScreenCaptureBitmap;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
        RxTool.init(this);

    }

    public Bitmap getmScreenCaptureBitmap() {
        return mScreenCaptureBitmap;
    }

    public void setmScreenCaptureBitmap(Bitmap mScreenCaptureBitmap) {
        this.mScreenCaptureBitmap = mScreenCaptureBitmap;
    }
}
