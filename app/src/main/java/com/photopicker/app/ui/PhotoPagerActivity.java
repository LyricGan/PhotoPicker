package com.photopicker.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.photopicker.app.R;
import com.photopicker.library.PhotoEntity;
import com.photopicker.library.PhotoPagerAdapter;
import com.photopicker.library.PhotoPickerHelper;
import com.photopicker.library.attacher.PhotoDraweeView;
import com.photopicker.library.view.ImageScaleType;

import java.util.ArrayList;

public class PhotoPagerActivity extends Activity {
    private ViewPager vp_photos;
    private ImageView iv_selected;

    private ArrayList<PhotoEntity> mPhotoList;
    private ArrayList<PhotoEntity> mSelectPhotoList;
    private int mSelectIndex ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker_pager);
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        vp_photos = (ViewPager) findViewById(R.id.vp_photos);
        iv_selected = (ImageView) findViewById(R.id.iv_selected);

        initialize();
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initialize() {
        mPhotoList = getIntent().getParcelableArrayListExtra(PhotoPickerHelper.KEY_PHOTOS);
        mSelectPhotoList = getIntent().getParcelableArrayListExtra(PhotoPickerHelper.KEY_PHOTOS_SELECTED);
        mSelectIndex = getIntent().getIntExtra(PhotoPickerHelper.KEY_SELECT_INDEX, 0);

        vp_photos.setAdapter(new PhotoPagerAdapter<PhotoEntity>(mPhotoList) {
            @Override
            protected View onInstantiateItem(ViewGroup container, int position, PhotoEntity item) {
                PhotoDraweeView draweeView = new PhotoDraweeView(container.getContext());
                draweeView.load(item.getPath(), null, R.mipmap.ic_broken_image_black, R.mipmap.ic_broken_image_black, ImageScaleType.FIT_CENTER);
                container.addView(draweeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                return draweeView;
            }
        });
        vp_photos.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                onPageSelected(position);
            }

            @Override
            public void onPageSelected(int position) {
                mSelectIndex = position;
                update(mSelectIndex);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        vp_photos.setCurrentItem(mSelectIndex);
    }

    private void update(int position) {
        if (mSelectPhotoList != null && mSelectPhotoList.contains(mPhotoList.get(position))) {
            iv_selected.setImageResource(R.mipmap.pic_check_select);
        } else {
            iv_selected.setImageResource(R.mipmap.pic_check_normal);
        }
    }
}
