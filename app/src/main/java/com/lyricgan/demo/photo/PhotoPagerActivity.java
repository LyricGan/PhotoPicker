package com.lyricgan.demo.photo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

import com.lyricgan.media.photo.PhotoEntity;
import com.lyricgan.media.photo.PhotoPagerAdapter;
import com.lyricgan.media.photo.PhotoPickerHelper;
import com.lyricgan.media.photo.attacher.PhotoDraweeView;
import com.lyricgan.media.photo.view.ImageScaleType;

import java.util.ArrayList;

public class PhotoPagerActivity extends Activity {
    private ImageView ivSelected;

    private ArrayList<PhotoEntity> mPhotoList;
    private ArrayList<PhotoEntity> mSelectPhotoList;
    private int mSelectIndex ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker_pager);
        ViewPager vpPhotos = findViewById(R.id.vp_photos);
        ivSelected = findViewById(R.id.iv_selected);

        Intent intent = getIntent();
        mPhotoList = intent.getParcelableArrayListExtra(PhotoPickerHelper.KEY_PHOTOS);
        mSelectPhotoList = intent.getParcelableArrayListExtra(PhotoPickerHelper.KEY_PHOTOS_SELECTED);
        mSelectIndex = intent.getIntExtra(PhotoPickerHelper.KEY_SELECT_INDEX, 0);

        vpPhotos.setAdapter(new PhotoPagerAdapter<PhotoEntity>(mPhotoList) {
            @Override
            protected View onInstantiateItem(ViewGroup container, int position, PhotoEntity item) {
                PhotoDraweeView draweeView = new PhotoDraweeView(container.getContext());
                draweeView.load(item.getPath(), null, R.mipmap.ic_broken_image_black, R.mipmap.ic_broken_image_black, ImageScaleType.FIT_CENTER);
                container.addView(draweeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                return draweeView;
            }
        });
        vpPhotos.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mSelectIndex = position;
                update(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        vpPhotos.setCurrentItem(mSelectIndex);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        ivSelected.setVisibility(View.GONE);
    }

    private void update(int position) {
        if (mSelectPhotoList == null || mPhotoList == null) {
            return;
        }
        if (mSelectPhotoList.contains(mPhotoList.get(position))) {
            ivSelected.setImageResource(R.mipmap.pic_check_select);
        } else {
            ivSelected.setImageResource(R.mipmap.pic_check_normal);
        }
    }
}
