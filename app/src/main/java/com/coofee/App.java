package com.coofee;

import android.app.Application;
import android.content.Context;

import com.coofee.multidex.MultidexFix;

/**
 * Created by zhaocongying on 16/8/1.
 */
public class App extends Application implements AppInitialize {

    private static final String CLASS_APP_INITIALIZE = "com.coofee.AppInitializeImpl";

    private static App sInstance;
    private AppInitialize mAppInitialize;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        if (MultidexFix.isMultidexInstalled()) {
            MultidexFix.installed();
            init(App.getInstance());
        } else {
            // do nothing;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void init(Context appContext) {
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
