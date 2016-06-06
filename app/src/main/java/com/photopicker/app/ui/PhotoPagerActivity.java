package com.photopicker.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.photopicker.app.R;
import com.photopicker.library.drawee.PhotoDraweeView;
import com.photopicker.library.picker.BasePhotoFileEntity;
import com.photopicker.library.picker.PhotoPagerAdapter;
import com.photopicker.library.picker.PhotoPickerHelper;

import java.util.ArrayList;

public class PhotoPagerActivity extends Activity {
    ViewPager vp_photos;
    ImageView iv_selected;

    private ArrayList<BasePhotoFileEntity> mPhotos;
    private ArrayList<BasePhotoFileEntity> mSelectPhotoes;
    private int mSelectIndex ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_photo_picker_pager);
        vp_photos = (ViewPager) findViewById(R.id.vp_photos);
        iv_selected = (ImageView) findViewById(R.id.iv_selected);
        initData(savedInstanceState);
    }

    protected void initData(Bundle savedInstanceState) {
        mPhotos = getIntent().getParcelableArrayListExtra(PhotoPickerHelper.KEY_PHOTOES);
        mSelectPhotoes = getIntent().getParcelableArrayListExtra(PhotoPickerHelper.KEY_PHOTOES_SELECTED);
        mSelectIndex = getIntent().getIntExtra(PhotoPickerHelper.KEY_SELECT_INDEX, 0);

        vp_photos.setAdapter(new PhotoPagerAdapter<BasePhotoFileEntity>(mPhotos) {
            @Override
            protected View onInstantiateItem(ViewGroup container, int position, BasePhotoFileEntity item) {
                PhotoDraweeView view = new PhotoDraweeView(container.getContext());
                view.loadImage(item.getPath(), null, R.mipmap.ic_launcher, R.mipmap.ic_broken_image_black, ScalingUtils.ScaleType.CENTER);
                container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                return view;
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
                if (mSelectPhotoes != null && mSelectPhotoes.contains(mPhotos.get(position))) {
                    iv_selected.setImageResource(R.mipmap.pic_check_select);
                } else {
                    iv_selected.setImageResource(R.mipmap.pic_check_normal);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        vp_photos.setCurrentItem(mSelectIndex);
    }
}
