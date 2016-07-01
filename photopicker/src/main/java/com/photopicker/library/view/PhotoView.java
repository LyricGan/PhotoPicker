package com.photopicker.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;

public class PhotoView extends SimpleDraweeView implements IAttacher {
    private Attacher mAttacher;

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    protected void initialize() {
        if (mAttacher == null || mAttacher.getDraweeView() == null) {
            mAttacher = new Attacher(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        int saveCount = canvas.save();
        canvas.concat(mAttacher.getDrawMatrix());
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void onAttachedToWindow() {
        initialize();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        mAttacher.onDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    @Override
    public float getMinimumScale() {
        return mAttacher.getMinimumScale();
    }

    @Override
    public float getMediumScale() {
        return mAttacher.getMediumScale();
    }

    @Override
    public float getMaximumScale() {
        return mAttacher.getMaximumScale();
    }

    @Override
    public void setMinimumScale(float minimumScale) {
        mAttacher.setMinimumScale(minimumScale);
    }

    @Override
    public void setMediumScale(float mediumScale) {
        mAttacher.setMediumScale(mediumScale);
    }

    @Override
    public void setMaximumScale(float maximumScale) {
        mAttacher.setMaximumScale(maximumScale);
    }

    @Override
    public float getScale() {
        return mAttacher.getScale();
    }

    @Override
    public void setScale(float scale) {
        mAttacher.setScale(scale);
    }

    @Override
    public void setScale(float scale, boolean animate) {
        mAttacher.setScale(scale, animate);
    }

    @Override
    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        mAttacher.setScale(scale, focalX, focalY, animate);
    }

    @Override
    public void setZoomTransitionDuration(long duration) {
        mAttacher.setZoomTransitionDuration(duration);
    }

    @Override
    public void setAllowParentInterceptOnEdge(boolean allow) {
        mAttacher.setAllowParentInterceptOnEdge(allow);
    }

    @Override
    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener listener) {
        mAttacher.setOnDoubleTapListener(listener);
    }

    @Override
    public void setOnScaleChangeListener(OnScaleChangeListener listener) {
        mAttacher.setOnScaleChangeListener(listener);
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener listener) {
        mAttacher.setOnLongClickListener(listener);
    }

    @Override
    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        mAttacher.setOnPhotoTapListener(listener);
    }

    @Override
    public void setOnViewTapListener(OnViewTapListener listener) {
        mAttacher.setOnViewTapListener(listener);
    }

    @Override
    public OnPhotoTapListener getOnPhotoTapListener() {
        return mAttacher.getOnPhotoTapListener();
    }

    @Override
    public OnViewTapListener getOnViewTapListener() {
        return mAttacher.getOnViewTapListener();
    }

    @Override
    public void update(int imageInfoWidth, int imageInfoHeight) {
        mAttacher.update(imageInfoWidth, imageInfoHeight);
    }

    public void loadImage(String url, String lowurl, int placeHolderResId, int errorResId, ScalingUtils.ScaleType scaleType) {
        final boolean isHttp = url.startsWith("http");
        Uri uri = isHttp ? Uri.parse(url) : Uri.fromFile(new File(url));
        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(ImageRequest.fromUri(uri));
        if (lowurl != null && lowurl.startsWith("http")) {
            controller.setLowResImageRequest(ImageRequest.fromUri(lowurl));
        }
        getHierarchy().setActualImageScaleType(scaleType);
        getHierarchy().setPlaceholderImage(getResources().getDrawable(placeHolderResId), scaleType);
        getHierarchy().setFailureImage(getResources().getDrawable(errorResId), scaleType);
        controller.setOldController(this.getController()).setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                if (imageInfo == null) {
                    return;
                }
                update(imageInfo.getWidth(), imageInfo.getHeight());
            }
        });
        this.setController(controller.build());
    }

    public void loadFile(String filePath) {
        loadFile(filePath, getWidth(), getHeight());
    }

    public void loadFile(String file, int width, int height) {
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.fromFile(new File(file)))
                .setLocalThumbnailPreviewsEnabled(true)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setProgressiveRenderingEnabled(false)
                .setResizeOptions(new ResizeOptions(width, height))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(this.getController())
                .build();
        this.setController(controller);
    }

    public void loadImage(String url, int placeHolder, int errorResId) {
        loadImage(url, placeHolder, errorResId, ScalingUtils.ScaleType.CENTER_CROP, null);
    }

    public void loadImage(String url, int placeHolder, int errorResId, ScalingUtils.ScaleType scaleType, final BaseControllerListener<ImageInfo> l) {
        loadImage(url, null, placeHolder, errorResId, scaleType, getWidth(), getHeight(), l);
    }

    public void loadImage(String url, String lowUrl, int placeHolder, int errorResId,
                          ScalingUtils.ScaleType scaleType, final BaseControllerListener<ImageInfo> l) {
        loadImage(url, lowUrl, placeHolder, errorResId, scaleType, getWidth(), getHeight(), l);
    }

    /***
     * load net work image.
     *
     * @param url         the main url of image, from net
     * @param lowUrl      the low url, from net or local image file name, can be null.
     * @param placeHolder the place holder. 0 with no place holder
     * @param errorResId  error res id , 0 with no error res.
     * @param width       the width you want
     * @param height      the height you want
     * @param scaleType   scale type
     * @param l           BaseControllerListener,can be null
     */
    public void loadImage(String url, String lowUrl, int placeHolder, int errorResId,
                          ScalingUtils.ScaleType scaleType, int width, int height, final BaseControllerListener<ImageInfo> l) {
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(url))
                //.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setResizeOptions(new ResizeOptions(width, height))
                .build();
        getHierarchy().setActualImageScaleType(scaleType);
        if (placeHolder != 0) {
            getHierarchy().setPlaceholderImage(getResources().getDrawable(placeHolder), scaleType);
        }
        if (errorResId != 0) {
            getHierarchy().setFailureImage(getResources().getDrawable(errorResId), scaleType);
        }
        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(l)
                .setImageRequest(request)
                .setOldController(getController());
        if (lowUrl != null) {
            Uri lowUri = lowUrl.startsWith("http") ? Uri.parse(lowUrl) : Uri.fromFile(new File(lowUrl));
            controller.setLowResImageRequest(ImageRequestBuilder.newBuilderWithSource(lowUri)
                    .setResizeOptions(new ResizeOptions(width, height))
                    .build());
        }
        setController(controller.build());
    }
}
