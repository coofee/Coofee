package com.coofee.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coofee.R;
import com.coofee.main.MainActivity;

/**
 * Created by zhaocongying on 16/8/3.
 */
public class LaunchFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.splash_fragment_launch, container, false);

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Activity activity = getActivity();
                if (activity != null) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
            }
        });
        return contentView;
    }
}
