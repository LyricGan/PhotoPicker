package com.photopicker.library.picker;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Arrays;

public class PhotoFileEntity implements IPhotoFileEntity, Parcelable, Serializable {
    private int imageId;
    private String path;
    private boolean selected;

    public PhotoFileEntity(int imageId, String path) {
        this.imageId = imageId;
        this.path = path;
    }

    @Override
    public void setImageId(int id) {
        this.imageId = id;
    }

    @Override
    public int getImageId() {
        return imageId;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoFileEntity entity = (PhotoFileEntity) o;
        if (imageId == entity.imageId && path.equals(entity.path)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{imageId, path});
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.imageId);
        dest.writeString(this.path);
        dest.writeByte(selected ? (byte) 1 : (byte) 0);
    }

    @Override
    public String toString() {
        return "PhotoFileEntity{" +
                "imageId=" + imageId +
                ", path='" + path + '\'' +
                ", selected=" + selected +
                '}';
    }

    private PhotoFileEntity(Parcel in) {
        this.imageId = in.readInt();
        this.path = in.readString();
        this.selected = in.readByte() != 0;
    }

    public static final Creator<PhotoFileEntity> CREATOR = new Creator<PhotoFileEntity>() {
        public PhotoFileEntity createFromParcel(Parcel source) {
            return new PhotoFileEntity(source);
        }

        public PhotoFileEntity[] newArray(int size) {
            return new PhotoFileEntity[size];
        }
    };
}
