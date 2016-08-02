package com.txyapp.downloaddemo.application;

import android.app.Application;

import com.facebook.stetho.Stetho;


/**
 * Created by liuxiaofeng on 2016/8/1.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
