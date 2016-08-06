package com.coofee.component;

import com.coofee.component.bus.RxBus;

import okhttp3.OkHttpClient;

/**
 * Created by zhaocongying on 16/8/3.
 */
public class Component {

    private static final class Holder {
        public static final Component sInstance = new Component();
    }

    private final RxBus<Object> mBus = new RxBus<Object>();
    private OkHttpClient mHttpClient;

    public static Component with(OkHttpClient httpClient) {
        Holder.sInstance.mHttpClient = httpClient;
        return Holder.sInstance;
    }

    public static RxBus<Object> getBus() {
        return Holder.sInstance.mBus;
    }

    public static OkHttpClient getHttp() {
        return Holder.sInstance.mHttpClient;
    }

}
