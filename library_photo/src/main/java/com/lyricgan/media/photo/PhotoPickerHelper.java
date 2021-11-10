package com.lyricgan.media.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.MIME_TYPE;

import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public final class PhotoPickerHelper {
    public static final String KEY_PHOTOS = "key_photos";
    public static final String KEY_PHOTOS_SELECTED = "key_photos_selected";
    public static final String KEY_SELECT_INDEX = "key_photo_select_index";
    /**
     * the request code for take photo
     */
    public static final int REQUEST_TAKE_PHOTO = 101;
    /**
     * the request code for big picture
     */
    public final static int REQUEST_CODE_SEE_BIG_PIC = 102;
    /**
     * the index of all photo in the {@link OnPhotoResultCallback#callback(List< PhotoDirectoryEntity >)}
     */
    public static final int INDEX_ALL_PHOTOS = 0;
    /**
     * the root dir to save the capture image.
     */
    public static final String ROOT_DIR = Environment.getExternalStorageDirectory().getPath() + File.separator + "photo_picker";

    private static final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
    };
    private final PhotoCaptureManager mCaptureManager;

    /**
     * the photo async loader callback
     *
     * @param <T>
     */
    public interface OnPhotoResultCallback<T extends IPhoto> {
        /**
         * called on load finished.
         *
         * @param directoryList the all photo dirs , every dir contains some photoes/images.
         *                    and the index = 0 of list is the whole dir contains all photoes.
         */
        void callback(List<PhotoDirectoryEntity<T>> directoryList);
    }

    PhotoPickerHelper(Activity activity) {
        this.mCaptureManager = new PhotoCaptureManager(activity);
    }

    /**
     * make an activity intent of take photo.
     *
     * @return the intent to to launch take photo activity.
     * @throws IOException if create temp image file failed.
     */
    public Intent makeTakePhotoIntent() throws IOException {
        return mCaptureManager.createTakePhotoIntent(ROOT_DIR);
    }

    /**
     * get the absolute path of take photo.
     *
     * @return the absolute path of take photo
     */
    public String getCurrentPhotoPath() {
        return mCaptureManager.getPhotoPath();
    }

    public void setPhotoPath(String image) {
        mCaptureManager.setPhotoPath(image);
    }

    /**
     * scan the photo file to the local media database which is saved by take photo.
     */
    public void scanFileToDatabase() {
        mCaptureManager.scanPhoto();
    }

    /**
     * scan the photos, this is async.
     *
     * @param resultCallback the callback
     */
    public <T extends IPhoto> void scanPhotoList(OnPhotoResultCallback<T> resultCallback) {
        Context context = mCaptureManager.getContext();
        if (context instanceof FragmentActivity) {
            ((FragmentActivity) context).getSupportLoaderManager().initLoader(0, null, new PhotoDirLoaderCallbacks<>(context, resultCallback));
        } else {
            ((Activity) context).getLoaderManager().initLoader(0, null, new PhotoDirLoaderCallbacks2<>(context, resultCallback));
        }
    }

    /**
     * an abstract class for loader callback
     */
    public static class AbsLoaderCallbacks<T extends IPhoto> {
        private final Context context;
        private final OnPhotoResultCallback<T> resultCallback;

        public AbsLoaderCallbacks(Context context, OnPhotoResultCallback<T> resultCallback) {
            this.context = context;
            this.resultCallback = resultCallback;
        }

        public Context getContext() {
            return context;
        }

        public OnPhotoResultCallback<T> getResultCallback() {
            return resultCallback;
        }

        /**
         * set up the directory of the all photo's. default return false.
         *
         * @param allPhotoDirectoryList the directory of the all photo's
         * @return true if you set up it.otherwise return false.
         */
        protected boolean setUpAllPhotoedDirectory(PhotoDirectoryEntity<T> allPhotoDirectoryList) {
            return false;
        }

        protected void doOnLoadFinished(Cursor data) {
            if (data == null) return;
            List<PhotoDirectoryEntity<T>> dirs = new ArrayList<>();
            //all photo with directory
            PhotoDirectoryEntity<T> allPhotoDirectoryList = new PhotoDirectoryEntity<>();
            if (!setUpAllPhotoedDirectory(allPhotoDirectoryList)) {
                allPhotoDirectoryList.setName("all");
                allPhotoDirectoryList.setId("all");
            }
            int imageId;
            String bucketId;
            String name;
            String path;

            PhotoDirectoryEntity<T> photoDirectory;
            int index;// index of dir
            while (data.moveToNext()) {
                imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
                bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
                name = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
                path = data.getString(data.getColumnIndexOrThrow(DATA));

                photoDirectory = new PhotoDirectoryEntity<>();
                photoDirectory.setId(bucketId);
                photoDirectory.setName(name);
                if ((index = dirs.indexOf(photoDirectory)) == -1) {
                    photoDirectory.setPath(path);
                    photoDirectory.addPhoto(imageId, path);
                    photoDirectory.setDate(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));
                    dirs.add(photoDirectory);
                } else {
                    dirs.get(index).addPhoto(imageId, path);
                }
                allPhotoDirectoryList.addPhoto(imageId, path);
            }
            if (allPhotoDirectoryList.getPhotoPaths().size() > 0) {
                allPhotoDirectoryList.setPath(allPhotoDirectoryList.getPhotoPaths().get(0));
            }
            dirs.add(INDEX_ALL_PHOTOS, allPhotoDirectoryList);
            if (getResultCallback() != null) {
                getResultCallback().callback(dirs);
            }
        }
    }

    private static class PhotoDirLoaderCallbacks2<T extends IPhoto> extends AbsLoaderCallbacks<T>
            implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

        public PhotoDirLoaderCallbacks2(Context context, OnPhotoResultCallback<T> resultCallback) {
            super(context, resultCallback);
        }

        @Override
        public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new PhotoDirectoryLoader2(getContext());
        }

        @Override
        public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
            doOnLoadFinished(data);
        }

        @Override
        public void onLoaderReset(android.content.Loader<Cursor> loader) {
        }
    }

    private static class PhotoDirLoaderCallbacks<T extends IPhoto> extends AbsLoaderCallbacks<T>
            implements LoaderManager.LoaderCallbacks<Cursor> {

        public PhotoDirLoaderCallbacks(Context context, OnPhotoResultCallback<T> resultCallback) {
            super(context, resultCallback);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new PhotoDirectoryLoader(getContext());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            doOnLoadFinished(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }

    private static class PhotoDirectoryLoader2 extends android.content.CursorLoader {

        public PhotoDirectoryLoader2(Context context) {
            super(context);
            setProjection(IMAGE_PROJECTION);
            setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            setSortOrder(MediaStore.Images.Media.DATE_ADDED + " DESC");
            setSelection(MIME_TYPE + "=? or " + MIME_TYPE + "=? ");

            String[] selectionArgs;
            selectionArgs = new String[]{"image/jpeg", "image/png"};
            setSelectionArgs(selectionArgs);
        }
    }

    private static class PhotoDirectoryLoader extends CursorLoader {

        public PhotoDirectoryLoader(Context context) {
            super(context);
            setProjection(IMAGE_PROJECTION);
            setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            setSortOrder(MediaStore.Images.Media.DATE_ADDED + " DESC");
            setSelection(MIME_TYPE + "=? or " + MIME_TYPE + "=? ");

            String[] selectionArgs;
            selectionArgs = new String[]{"image/jpeg", "image/png"};
            setSelectionArgs(selectionArgs);
        }
    }
}
