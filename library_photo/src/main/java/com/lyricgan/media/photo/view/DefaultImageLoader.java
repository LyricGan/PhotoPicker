package com.lyricgan.media.photo.view;

import android.text.TextUtils;
import android.widget.ImageView;

import com.facebook.drawee.controller.ControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.lyricgan.media.photo.R;

public class DefaultImageLoader implements IImageLoader {
    private final ControllerListener<ImageInfo> mListener;
    private final int mDefaultResId;
    private int mWidth;
    private int mHeight;

    public DefaultImageLoader(int defaultResId) {
        this(null, defaultResId);
    }

    public DefaultImageLoader(ControllerListener<ImageInfo> listener, int defaultResId) {
        this.mListener = listener;
        this.mDefaultResId = defaultResId;
    }

    public void setSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    public void load(ImageView imageView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (mWidth <=0 || mHeight <= 0) {
            int size = imageView.getContext().getResources().getDimensionPixelSize(R.dimen.photo_size);
            mWidth = size;
            mHeight = size;
        }
        if (imageView instanceof ImageDraweeView) {
            ImageDraweeView draweeView = (ImageDraweeView) imageView;
            draweeView.setImageUrl(url, mWidth, mHeight, mDefaultResId, mListener);
        }
    }
}
