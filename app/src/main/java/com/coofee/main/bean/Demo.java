package com.coofee.main.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.coofee.BR;

/**
 * Created by zhaocongying on 16/9/2.
 */
public class Demo extends BaseObservable {

    public String title;
    public String iconUrl;
    public Class<?> mActivityClass;

    public Demo() {
    }

    public Demo(String title, String iconUrl, Class<?> mActivityClass) {
        this.title = title;
        this.iconUrl = iconUrl;
        this.mActivityClass = mActivityClass;
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    @Bindable
    public String getIconUrl() {
        return iconUrl;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        notifyPropertyChanged(BR.iconUrl);
    }
}
