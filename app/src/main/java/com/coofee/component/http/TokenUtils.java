package com.coofee.component.http;

import android.content.Context;

import com.coofee.App;

import java.util.UUID;

/**
 * Created by zhaocongying on 16/8/6.
 */
public class TokenUtils {
    private static final String SP_TOKEN = "token";
    private static final String KEY_TOKEN = "token";

    private static String sToken = null;

    public static String getToken() {
        if (sToken == null) {
            sToken = App.getInstance().getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE).getString(KEY_TOKEN, null);
        }
        return sToken;
    }

    public static boolean setToken(String token) {
        boolean result = App.getInstance().getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE).edit().putString(KEY_TOKEN, token).commit();
        if (result) {
            sToken = token;
        }

        return result;
    }

    public static boolean refreshToken() {
        // request network and get refresh token;
        setToken(UUID.randomUUID().toString());
        return true;
    }
}
