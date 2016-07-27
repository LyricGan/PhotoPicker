package com.photopicker.app;

import android.app.Application;

import com.photopicker.library.PhotoPickerFactory;

public class App extends Application {
    private static App mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        PhotoPickerFactory.getImageHelper().initialize(this);
    }

    public static App getContext() {
        return mContext;
    }
}
