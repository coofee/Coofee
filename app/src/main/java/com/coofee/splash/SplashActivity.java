package com.coofee.splash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.coofee.App;
import com.coofee.R;
import com.coofee.multidex.MultidexFix;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MultidexFix.install().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        App.getInstance().init(App.getInstance());
                    }
                }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                // multidex error;
            }

            @Override
            public void onNext(Integer integer) {
                // multidex installed.
            }
        });
    }
}
