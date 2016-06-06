package com.photopicker.app.ui;

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
import com.heaven7.core.util.ViewHelper;
import com.photopicker.app.R;

import java.io.File;

public class DraweeImageLoader implements ViewHelper.IImageLoader {
    private final ControllerListener<ImageInfo> mListener;
    private final int mDefaultResId;

    public DraweeImageLoader(int defaultResId) {
        this(null, defaultResId);
    }

    public DraweeImageLoader(ControllerListener<ImageInfo> mListener, int defaultResId) {
        this.mListener = mListener;
        this.mDefaultResId = defaultResId;
    }

    @Override
    public void load(String url, ImageView iv) {
        int size = iv.getContext().getResources().getDimensionPixelSize(R.dimen.photo_size);
        SimpleDraweeView view = (SimpleDraweeView) iv;
        Uri uri = url.startsWith("http://") || url.startsWith("https://") ? Uri.parse(url) : Uri.fromFile(new File(url));
        try {
            ImageRequest request = ImageRequestBuilder
                    // .newBuilderWithSource(Uri.fromFile(new File(url)))
                    .newBuilderWithSource(uri)
                    .setLocalThumbnailPreviewsEnabled(true)
                    .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                    .setProgressiveRenderingEnabled(false)
                    .setResizeOptions(new ResizeOptions(size, size))
                    .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setControllerListener(mListener)
                    .setOldController(view.getController())
                            // .setUri(uri)
                    .setImageRequest(request)
                    .build();
            if (mDefaultResId != 0) {
                view.getHierarchy().setPlaceholderImage(mDefaultResId);
            }
            view.setController(controller);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
    }

    public void loadLoacalFile(SimpleDraweeView view, String filename, int width, int height) {
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.fromFile(new File(filename)))
                .setLocalThumbnailPreviewsEnabled(true)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setProgressiveRenderingEnabled(false)
                .setResizeOptions(new ResizeOptions(width, height))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(view.getController())
                .build();
        view.setController(controller);
    }
}
