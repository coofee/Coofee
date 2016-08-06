package com.coofee.multidex;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.multidex.MultiDex;

import com.coofee.App;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

/**
 * Created by zhaocongying on 16/8/1.
 */
public class MultidexFix {
    private static final String SP_APP_MULTIDEX = "app.multidex";

    private static final String KEY_MULTIDEX_INSTALLED = "app.multidex.installed";

    public static final int TYPE_ERROR = -1;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_INSTALLED = 1;
    public static final int TYPE_INSTALLING = 2;

    private static int sInstallStatus = TYPE_UNKNOWN;

    public static Observable<Integer> install() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    if (sInstallStatus == TYPE_INSTALLED) {
                        subscriber.onNext(sInstallStatus);
                        subscriber.onCompleted();
                        return;
                    }

                    if (isMultidexInstalled()) {
                        sInstallStatus = TYPE_INSTALLED;
                        subscriber.onNext(sInstallStatus);
                        subscriber.onCompleted();
                        return;
                    }

                    if (sInstallStatus == TYPE_INSTALLING) {
                        while (sInstallStatus != TYPE_INSTALLED) {
                            SystemClock.sleep(100L);
                        }

                        subscriber.onNext(sInstallStatus);
                        subscriber.onCompleted();
                        return;
                    }

                    if (sInstallStatus == TYPE_UNKNOWN) {
                        try {
                            sInstallStatus = TYPE_INSTALLING;
                            Timber.d("Multidex");
                            installed();
                            setMultidexInstalled();
                            sInstallStatus = TYPE_INSTALLED;
                            subscriber.onNext(sInstallStatus);
                        } catch (Throwable e) {
                            sInstallStatus = TYPE_UNKNOWN;
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                }
            }
        });
    }

    public static void installed() {
        MultiDex.install(App.getInstance());
    }

    public static boolean isMultidexInstalled() {
        SharedPreferences sp = App.getInstance().getSharedPreferences(SP_APP_MULTIDEX, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_MULTIDEX_INSTALLED, false);
    }

    public static boolean setMultidexInstalled() {
        SharedPreferences sp = App.getInstance().getSharedPreferences(SP_APP_MULTIDEX, Context.MODE_PRIVATE);
        return sp.edit().putBoolean(KEY_MULTIDEX_INSTALLED, true).commit();
    }
}
