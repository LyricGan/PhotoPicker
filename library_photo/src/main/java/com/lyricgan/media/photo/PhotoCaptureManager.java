package com.lyricgan.media.photo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author lyricgan
 * @description
 * @time 2016/7/1 14:53
 */
public class PhotoCaptureManager {
    private static final String _JPG = ".jpg";
    private Context mContext;
    private String mPhotoPath;

    public PhotoCaptureManager(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public Intent createTakePhotoIntent(String dir) throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile = createPhotoFile(dir);
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            }
        }
        return intent;
    }

    public void scanPhoto() {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(getPhotoPath()));
        intent.setData(contentUri);
        getContext().sendBroadcast(intent);
    }

    private File createPhotoFile(String dir) throws IOException {
        File file = File.createTempFile(UUID.randomUUID().toString(), _JPG, createPhotoDir(dir));
        setPhotoPath(file.getPath());
        return file;
    }

    private File createPhotoDir(String dir) {
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return dirFile;
    }

    public String getPhotoPath() {
        return mPhotoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.mPhotoPath = photoPath;
    }
}
