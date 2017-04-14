package com.randy.randyclientdemo;

import android.app.Application;

import com.randy.randyclient.helper.RxRetrofitApp;

/**
 * 应用实例
 * Created by RandyZhang on 2017/4/14.
 */

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // init RxRetrofitApp if use db cache
        RxRetrofitApp.init(this, BuildConfig.DEBUG);
    }
}
