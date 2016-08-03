package com.coofee;

import android.app.Application;
import android.content.Context;

import com.coofee.multidex.MultidexFix;

/**
 * Created by zhaocongying on 16/8/1.
 */
public class App extends Application {

    private static final String CLASS_APP_INITIALIZE = "com.coofee.AppInitializeImpl";

    private static App sInstance;
    private AppInitialize mAppInitialize;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        if (MultidexFix.isMultidexInstalled()) {
            MultidexFix.installed();
            init();
        } else {
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

    public void init() {
        try {
            if (mAppInitialize == null) {
                mAppInitialize = (AppInitialize) Class.forName(CLASS_APP_INITIALIZE).newInstance();
                mAppInitialize.init(this);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
