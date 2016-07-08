package com.photopicker.app;

import android.app.Application;

import com.photopicker.library.PhotoPickerFactory;

public class BaseApp extends Application {
    private static BaseApp mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        PhotoPickerFactory.getImageHelper().initialize(this);
    }

    public static BaseApp getContext() {
        return mContext;
    }
}
