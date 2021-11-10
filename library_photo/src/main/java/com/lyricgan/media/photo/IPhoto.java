package com.lyricgan.media.photo;

/**
 * @author lyricgan
 * @description
 * @time 2016/7/1 14:31
 */
public interface IPhoto extends Selectable {

    void setImageId(int id);

    int getImageId();

    void setPath(String path);

    String getPath();
}
