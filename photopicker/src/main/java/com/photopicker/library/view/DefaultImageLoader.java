package com.photopicker.library.view;

import android.widget.ImageView;

import com.facebook.drawee.controller.ControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.photopicker.library.R;

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
    public void load(String url, ImageView imageView) {
        if (mWidth <=0 || mHeight <= 0) {
            int size = imageView.getContext().getResources().getDimensionPixelSize(R.dimen.photo_size);
            mWidth = size;
            mHeight = size;
        }
        if (imageView instanceof ImageDraweeView) {
            ImageDraweeView view = (ImageDraweeView) imageView;
            view.setImageFile(url, mWidth, mHeight, mDefaultResId, mListener);
        }
    }
}
