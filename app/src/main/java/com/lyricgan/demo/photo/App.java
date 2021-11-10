package com.lyricgan.demo.photo;

import android.app.Application;

import com.lyricgan.media.photo.PhotoPickerFactory;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PhotoPickerFactory.getImageHelper().initialize(this);
    }
}
