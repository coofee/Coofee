package com.coofee.component.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Created by zhaocongying on 16/8/6.
 */
public class LogInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        long startTime = System.nanoTime();
        Request request = chain.request();
        Timber.d("send request url=%s on connection=%s headers=%s", request.url(), chain.connection(), request.headers());
        Response response = chain.proceed(request);
        long consumeTime = System.nanoTime() - startTime;
        Timber.d("finish request url=%s in %fms headers=%s", response.request().url(), consumeTime / 1e6d, response.headers());
        return response;
    }
}
