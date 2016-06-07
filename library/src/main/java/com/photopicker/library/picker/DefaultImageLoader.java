package com.photopicker.library.picker;

import android.net.Uri;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.photopicker.library.R;

import java.io.File;

public class DefaultImageLoader implements IImageLoader {
    private final ControllerListener<ImageInfo> mListener;
    private final int mDefaultResId;

    public DefaultImageLoader(int defaultResId) {
        this(null, defaultResId);
    }

    public DefaultImageLoader(ControllerListener<ImageInfo> listener, int defaultResId) {
        this.mListener = listener;
        this.mDefaultResId = defaultResId;
    }

    @Override
    public void load(String url, ImageView iv) {
        int size = iv.getContext().getResources().getDimensionPixelSize(R.dimen.photo_size);
        SimpleDraweeView view = (SimpleDraweeView) iv;
        Uri uri = url.startsWith("http://") || url.startsWith("https://") ? Uri.parse(url) : Uri.fromFile(new File(url));
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setLocalThumbnailPreviewsEnabled(true)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setProgressiveRenderingEnabled(false)
                .setResizeOptions(new ResizeOptions(size, size))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(mListener)
                .setOldController(view.getController())
                .setImageRequest(request)
                .build();
        if (mDefaultResId != 0) {
            view.getHierarchy().setPlaceholderImage(mDefaultResId);
        }
        view.setController(controller);
    }
}
