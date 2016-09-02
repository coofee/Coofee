package com.coofee;

import android.content.Context;

import com.coofee.component.Component;
import com.coofee.component.http.Auth;
import com.coofee.component.http.HttpUtils;
import com.coofee.component.http.LogInterceptor;
import com.coofee.component.http.TokenInterceptor;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Dns;
import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * Created by zhaocongying on 16/8/1.
 */
public class AppInitializeImpl implements AppInitialize {

    public static final long CONNECTION_TIME_OUT = 30;
    public static final long READ_TIME_OUT = 80;
    public static final long WRITE_TIME_OUT = 120;

    @Override
    public void init(Context appContext) {
        // 初始化日志;
//        Timber.plant(new Timber.DebugTree());

        // 初始化http;
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(HttpUtils.CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(HttpUtils.READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(HttpUtils.WRITE_TIME_OUT, TimeUnit.SECONDS)
                .addNetworkInterceptor(new LogInterceptor())
                .addNetworkInterceptor(new TokenInterceptor())
                .authenticator(new Auth())
                .dns(new Dns() {
                    @Override
                    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
                        // 可以用来实现自己的dns;
                        return Dns.SYSTEM.lookup(hostname);
                    }
                })
                .cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(appContext)))
                .build();

        Component.with(httpClient);
    }
}
