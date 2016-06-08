package com.photopicker.library.picker;

public interface IPhotoFileEntity extends ISelectable {

    void setImageId(int id);

    int getImageId();

    void setPath(String path);

    String getPath();
}
