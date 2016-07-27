package com.photopicker.library.view;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.AttributeSet;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;

/**
 * @author lyricgan
 * @description use {@link ImageHelper#initialize(Context)} first
 * @time 2016/7/6 19:18
 */
public class ImageDraweeView extends SimpleDraweeView {

    public interface OnImageLoadListener {

        void onSuccess(int width, int height);

        void onFailed();
    }

    public ImageDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public ImageDraweeView(Context context) {
        super(context);
    }

    public ImageDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ImageDraweeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void addRoundingParams() {
        GenericDraweeHierarchy hierarchy = getHierarchy();
        RoundingParams roundingParams = hierarchy.getRoundingParams();
        if (roundingParams == null) {
            roundingParams = RoundingParams.asCircle();
        }
        hierarchy.setRoundingParams(roundingParams);
    }

    private void setPlaceholderImage(int resId) {
        getHierarchy().setPlaceholderImage(resId);
    }

    public void setImageScaleType(ScalingUtils.ScaleType scaleType) {
        getHierarchy().setActualImageScaleType(scaleType);
    }

    private Uri formatUri(String url) {
        return url.startsWith("http://") || url.startsWith("https://") ? Uri.parse(url) : Uri.fromFile(new File(url));
    }

    public void setRoundImageUrl(String url, int resId) {
        addRoundingParams();
        setImageUrl(url, resId);
    }

    public void setImageUrl(String url, int resId) {
        setImageUrl(url, resId, null);
    }

    public void setImageUrl(String url, int resId, ImageScaleType scaleType) {
        setImageUrl(url, resId, scaleType, null);
    }

    public void setImageUrl(String url, int resId, ImageScaleType scaleType, final OnImageLoadListener listener) {
        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable anim) {
                if (listener != null) {
                    listener.onSuccess(imageInfo.getWidth(), imageInfo.getHeight());
                }
            }

            @Override
            public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                // 如果允许呈现渐进式JPEG，同时图片也是渐进式图片，onIntermediateImageSet()会在每个扫描被解码后回调
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                if (listener != null) {
                    listener.onFailed();
                }
            }
        };
        setPlaceholderImage(resId);
        if (scaleType != null) {
            setImageScaleType(scaleType);
        }
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(ImageRequest.fromUri(formatUri(url)))
                .setImageRequest(ImageRequest.fromUri(formatUri(url)))
                .setTapToRetryEnabled(true)
                .setOldController(getController())
                .setControllerListener(controllerListener)
                .build();
        this.setController(controller);
    }

    public void setImageUri(int resId) {
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(resId))
                .build();
        setImageURI(uri);
    }

    public void setImageUrl(String url, int width, int height) {
        setImageUrl(url, width, height, 0, null);
    }

    public void setImageUrl(String url, int width, int height, int defaultResId, ControllerListener<ImageInfo> listener) {
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(formatUri(url))
                .setLocalThumbnailPreviewsEnabled(true)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setProgressiveRenderingEnabled(false)
                .setResizeOptions(new ResizeOptions(width, height))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(getController())
                .setControllerListener(listener)
                .build();
        if (defaultResId > 0) {
            setPlaceholderImage(defaultResId);
        }
        this.setController(controller);
    }
}
