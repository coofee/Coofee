package com.coofee.splash;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.coofee.App;
import com.coofee.R;
import com.coofee.multidex.MultidexFix;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    public static final String LAUNCH_FRAGMENT = "com.coofee.splash.LaunchFragment";

    private Subscription mMultidexInstallSubscription = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!MultidexFix.isMultidexInstalled()) {
            multidexInstall();
        } else {
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
                        Toast.makeText(SplashActivity.this, "multidex falied", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        // multidex installed.
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
