package com.coofee.main.bean;

/**
 * Created by zhaocongying on 16/9/2.
 */
public class Demo {

    public String title;
    public Class<?> mActivityClass;

    public Demo() {
    }

    public Demo(String title, Class<?> mActivityClass) {
        this.title = title;
        this.mActivityClass = mActivityClass;
    }

}
