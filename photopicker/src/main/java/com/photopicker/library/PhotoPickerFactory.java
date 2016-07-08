package com.photopicker.library;

import android.app.Activity;

import com.photopicker.library.view.ImageHelper;

public final class PhotoPickerFactory {
    private static IPhotoFileEntityFactory sPhotoFactory;
    private static IImageLoader sImageLoader;

    private static final IPhotoFileEntityFactory<PhotoEntity> sDefaultFactory
            = new IPhotoFileEntityFactory<PhotoEntity>() {
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
    public interface IPhotoFileEntityFactory<T extends IPhoto> {
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
    public static <T extends IPhoto> void setPhotoFileEntityFactory(IPhotoFileEntityFactory<T> factory) {
        sPhotoFactory = factory;
    }

    /**
     * set the image loader to load image .this will use in {@link PhotoPickerAdapter}
     * you can cross this use frecro image library or glide or other image library to load.
     * this  is useful.
     *
     * @param imageLoader the image loader
     */
    public static void setImageLoader(IImageLoader imageLoader) {
        sImageLoader = imageLoader;
    }

    /**
     * get the photo picker entity factory, if not set the default factory wil be returned.
     *
     * @return photo picker entity factory
     */
    public static IPhotoFileEntityFactory getPhotoFileEntityFactory() {
        return sPhotoFactory != null ? sPhotoFactory : sDefaultFactory;
    }

    /**
     * get the image loader
     */
    public static IImageLoader getImageLoader() {
        if (sImageLoader == null) {
            return new DefaultImageLoader(0);
        }
        return sImageLoader;
    }

    public static ImageHelper getImageHelper() {
        return ImageHelper.getInstance();
    }
}
