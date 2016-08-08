package com.coofee.splash;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.coofee.App;
import com.coofee.R;
import com.coofee.multidex.MultidexFix;

import hugo.weaving.DebugLog;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SplashActivity extends FragmentActivity {

    public static final String LAUNCH_FRAGMENT = "com.coofee.splash.LaunchFragment";

    private Subscription mMultidexInstallSubscription = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!MultidexFix.isMultidexInstalled()) {
            Timber.d("Splash: multidex not installed.");
            multidexInstall();
        } else {
            Timber.d("Splash: multidex installed, show launch fragment.");
            Fragment fragment = Fragment.instantiate(SplashActivity.this, LAUNCH_FRAGMENT);
            showPage(fragment);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMultidexInstallSubscription != null) {
            mMultidexInstallSubscription.unsubscribe();
        }
    }

    @DebugLog
    private void multidexInstall() {
        mMultidexInstallSubscription = MultidexFix.install().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // multidex error;
                        Timber.d(e, "Splash: multidex installed error.");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        // multidex installed.

                        Timber.d("Splash: multidex installed, call App.init() and then show launch fragment.");
                        App.getInstance().init();
                        Toast.makeText(SplashActivity.this, "multidex successed.", Toast.LENGTH_LONG).show();

                        Fragment fragment = Fragment.instantiate(SplashActivity.this, LAUNCH_FRAGMENT);
                        showPage(fragment);
                    }
                });
    }

    private void showPage(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.splash_container, fragment);
        ft.commit();
    }
}
