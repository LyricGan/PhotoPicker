package com.lyricgan.media.photo.view;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * @author Lyric Gan
 */
public class ImageHelper {
    private static ImageHelper mInstance;

    private ImageHelper() {
    }

    public static synchronized ImageHelper getInstance() {
        if (null == mInstance) {
            mInstance = new ImageHelper();
        }
        return mInstance;
    }

    public void initialize(Context context) {
        Fresco.initialize(context, ImageFactory.getImagePipelineConfig(context));
    }
}
