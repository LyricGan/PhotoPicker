package com.photopicker.library;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lyricgan
 * @description
 * @time 2016/7/1 14:33
 */
public class PhotoDirectoryEntity<T extends IPhoto> implements Selectable {
    private String id;
    private String name;
    private String path;
    private long date;
    private List<T> photoList = new ArrayList<>();
    private boolean selected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<T> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<T> photoList) {
        this.photoList = photoList;
    }

    @Override
    public void setSelected(boolean flag) {
        this.selected = flag;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    public List<String> getPhotoPaths() {
        List<String> paths = new ArrayList<String>(photoList.size());
        for (T photo : photoList) {
            paths.add(photo.getPath());
        }
        return paths;
    }

    public void addPhoto(int id, String path) {
        photoList.add((T) PhotoPickerFactory.getPhotoFileEntityFactory().create(id, path));
    }
}
