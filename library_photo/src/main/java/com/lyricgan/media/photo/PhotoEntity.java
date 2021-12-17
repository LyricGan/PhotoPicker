package com.lyricgan.media.photo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Lyric Gan
 */
public class PhotoEntity implements IPhoto, Parcelable {
    private int imageId;
    private String path;
    private boolean selected;

    public PhotoEntity(int imageId, String path) {
        this.imageId = imageId;
        this.path = path;
    }

    @Override
    public void setImageId(int id) {
        this.imageId = id;
    }

    @Override
    public int getImageId() {
        return this.imageId;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public void setSelected(boolean flag) {
        this.selected = flag;
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.imageId);
        dest.writeString(this.path);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    public PhotoEntity() {
    }

    protected PhotoEntity(Parcel in) {
        this.imageId = in.readInt();
        this.path = in.readString();
        this.selected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PhotoEntity> CREATOR = new Parcelable.Creator<PhotoEntity>() {
        @Override
        public PhotoEntity createFromParcel(Parcel source) {
            return new PhotoEntity(source);
        }

        @Override
        public PhotoEntity[] newArray(int size) {
            return new PhotoEntity[size];
        }
    };
}
