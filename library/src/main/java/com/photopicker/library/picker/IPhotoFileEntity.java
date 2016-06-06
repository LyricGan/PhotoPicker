package com.photopicker.library.picker;

import com.heaven7.adapter.ISelectable;

public interface IPhotoFileEntity extends ISelectable {

    void setImageId(int id);

    int getImageId();

    void setPath(String path);

    String getPath();
}
