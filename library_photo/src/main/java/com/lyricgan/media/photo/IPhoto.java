package com.lyricgan.media.photo;

/**
 * @author Lyric Gan
 */
public interface IPhoto extends Selectable {

    void setImageId(int id);

    int getImageId();

    void setPath(String path);

    String getPath();
}
