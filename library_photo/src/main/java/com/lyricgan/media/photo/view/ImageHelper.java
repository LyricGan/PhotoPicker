package com.lyricgan.media.photo.view;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * 图片初始化工具类，单例
 * @author Lyric Gan
 */
public class ImageHelper {

    private ImageHelper() {
    }

    private static class Holder {
        private static final ImageHelper INSTANCE = new ImageHelper();
    }

    public static ImageHelper getInstance() {
        return Holder.INSTANCE;
    }

    public void initialize(Context context) {
        Fresco.initialize(context, ImageFactory.getImagePipelineConfig(context));
    }
}
