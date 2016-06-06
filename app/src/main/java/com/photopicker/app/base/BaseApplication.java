package com.photopicker.app.base;

import android.app.Application;

/**
 * @author lyric
 * @description APP入口
 * @time 2016/6/6 19:34
 */
public class BaseApplication extends Application {
    private static BaseApplication mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static BaseApplication getContext() {
        return mContext;
    }
}
