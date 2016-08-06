package com.coofee.component.http;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by zhaocongying on 16/8/6.
 */
public class Auth implements okhttp3.Authenticator {
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        TokenUtils.refreshToken();
        return response.request().newBuilder()
                .addHeader("Authorization", TokenUtils.getToken())
                .build();
    }
}
