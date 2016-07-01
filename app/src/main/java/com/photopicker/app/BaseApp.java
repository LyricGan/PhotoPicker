package com.photopicker.app;

import android.app.Application;
import android.os.Environment;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import java.io.File;

public class BaseApp extends Application {
    private static BaseApp mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        initialize();
    }

    private void initialize() {
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryPath(new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "Locale Pictures"))
                .setBaseDirectoryName("fresco")
                .setMaxCacheSize(Runtime.getRuntime().maxMemory() / 8)
                .build();
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(this)
                .setMainDiskCacheConfig(diskCacheConfig)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, imagePipelineConfig);
    }

    public static BaseApp getContext() {
        return mContext;
    }
}
