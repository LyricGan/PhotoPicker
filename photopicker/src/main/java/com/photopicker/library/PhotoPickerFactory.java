package com.photopicker.library;

import android.app.Activity;

import com.photopicker.library.view.DefaultImageLoader;
import com.photopicker.library.view.IImageLoader;
import com.photopicker.library.view.ImageHelper;

public final class PhotoPickerFactory {
    private static IPhotoEntityFactory mPhotoFactory;
    private static IImageLoader mImageLoader;

    private static final IPhotoEntityFactory<PhotoEntity> mDefaultFactory = new IPhotoEntityFactory<PhotoEntity>() {
        @Override
        public PhotoEntity create(int id, String path) {
            return new PhotoEntity(id, path);
        }
    };

    /**
     * the photo entity factory for create the photo file entity
     *
     * @param <T>
     */
    public interface IPhotoEntityFactory<T extends IPhoto> {
        T create(int id, String path);
    }

    /**
     * create an instance of PhotoPickerHelper
     *
     * @param activity the activity
     */
    public static PhotoPickerHelper createPhotoPickerHelper(Activity activity) {
        return new PhotoPickerHelper(activity);
    }

    /***
     * set the photo entity factory
     *
     * @param factory the factory
     */
    public static <T extends IPhoto> void setPhotoEntityFactory(IPhotoEntityFactory<T> factory) {
        mPhotoFactory = factory;
    }

    /**
     * set the image loader to load image .this will use in {@link PhotoPickerAdapter}
     * you can cross this use frecro image library or glide or other image library to load.
     * this  is useful.
     *
     * @param imageLoader the image loader
     */
    public static void setImageLoader(IImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    /**
     * get the photo picker entity factory, if not set the default factory wil be returned.
     *
     * @return photo picker entity factory
     */
    public static IPhotoEntityFactory getPhotoEntityFactory() {
        return mPhotoFactory != null ? mPhotoFactory : mDefaultFactory;
    }

    /**
     * get the image loader
     */
    public static IImageLoader getImageLoader() {
        if (mImageLoader == null) {
            return new DefaultImageLoader(0);
        }
        return mImageLoader;
    }

    public static ImageHelper getImageHelper() {
        return ImageHelper.getInstance();
    }
}
