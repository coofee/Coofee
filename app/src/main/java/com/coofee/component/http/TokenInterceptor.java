package com.coofee.component.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhaocongying on 16/8/6.
 */
public class TokenInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String token = TokenUtils.getToken();
        if (token == null || originalRequest.header("Authorization") != null) {
            // 没有获取到token;或者已经添加token到header;
            return chain.proceed(originalRequest);
        }
        Request authorised = originalRequest.newBuilder()
                .header("Authorization", token)
                .build();
        return chain.proceed(authorised);
    }
}
