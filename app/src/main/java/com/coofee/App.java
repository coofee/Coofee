package com.coofee;

import android.app.Application;
import android.content.Context;

import com.coofee.multidex.MultidexFix;

import hugo.weaving.DebugLog;
import timber.log.Timber;

/**
 * Created by zhaocongying on 16/8/1.
 */
public class App extends Application {

    private static App sInstance;
    private AppInitialize mAppInitialize;

    @DebugLog
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        // 初始化日志;
        Timber.plant(new Timber.DebugTree());

        if (MultidexFix.isMultidexInstalled()) {
            Timber.d("App: multidex installed, so init.");
            MultidexFix.installed();
            init();
        } else {
            Timber.d("App: multidex not installed, skip.");
            // do nothing;
            // 系统会自动跳转到首页.
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static App getInstance() {
        return sInstance;
    }

    @DebugLog
    public void init() {
        Timber.d("App: try init...");
        try {
            if (mAppInitialize == null) {
                // 防止AppInitializeImpl被打入主dex.
                String fullClassName = new StringBuilder("com.")
                        .append("coofee.")
                        .append("AppInitializeImpl").toString();
                mAppInitialize = (AppInitialize) Class.forName(fullClassName).newInstance();
                mAppInitialize.init(this);
                Timber.d("App: inited.");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Timber.d(e, "App: init error");
        }
    }
}
