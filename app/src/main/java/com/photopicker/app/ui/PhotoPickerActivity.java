package com.photopicker.app.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.photopicker.app.R;
import com.photopicker.library.picker.ISelectable;
import com.photopicker.library.picker.PhotoDirectoryEntity;
import com.photopicker.library.picker.PhotoFileEntity;
import com.photopicker.library.picker.PhotoPickerAdapter;
import com.photopicker.library.picker.PhotoPickerFactory;
import com.photopicker.library.picker.PhotoPickerHelper;
import com.photopicker.library.picker.ViewHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoPickerActivity extends Activity implements PhotoPickerHelper.PhotoLoadResultCallback<PhotoFileEntity> {
    private TextView tv_done_notice;
    private RecyclerView rv_photos;

    private PhotoPickerHelper mPickerHelper;
    private List<PhotoDirectoryEntity<PhotoFileEntity>> mPhotoDirs;
    private PhotoPickerAdapter<PhotoFileEntity> mPickerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);
        final ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        final TextView tv_all_image = (TextView) findViewById(R.id.tv_all_image);
        tv_done_notice = (TextView) findViewById(R.id.tv_done_notice);
        rv_photos = (RecyclerView) findViewById(R.id.rv_photos);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_all_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        rv_photos.setLayoutManager(layoutManager);
        rv_photos.addItemDecoration(new SpacesItemDecoration((int) getResources().getDimension(R.dimen.photo_width)));

        mPickerHelper = PhotoPickerFactory.createPhotoPickerHelper(this);
        if (savedInstanceState != null) {
            mPickerHelper.setPhotoPath(savedInstanceState.getString("image", null));
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            mPickerHelper.scanPhotoes(this);
        }
    }

    private final PhotoPickerAdapter.ICallback<PhotoFileEntity> mCallback = new PhotoPickerAdapter.ICallback<PhotoFileEntity>() {
        @Override
        public void onClickCamera(View itemView) {
            try {
                startActivityForResult(mPickerHelper.makeTakePhotoIntent(), PhotoPickerHelper.REQUEST_TAKE_PHOTO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClickItemView(View itemView, int position, PhotoFileEntity item) {
            List<PhotoFileEntity> selectItems = mPickerAdapter.getSelectHelper().getSelectedItems();
            ArrayList<PhotoFileEntity> photoes = new ArrayList<>(mPickerAdapter.getAdapterManager().getItems());
            if (mPickerAdapter.isShowCamera()) {
                photoes.remove(0);
                position -= 1;
            }
            Bundle bundle = new Bundle();
            bundle.putInt(PhotoPickerHelper.KEY_SELECT_INDEX, position);
            bundle.putParcelableArrayList(PhotoPickerHelper.KEY_PHOTOES, photoes);
            if (selectItems != null) {
                bundle.putParcelableArrayList(PhotoPickerHelper.KEY_PHOTOES_SELECTED, new ArrayList<>(mPickerAdapter.getSelectHelper().getSelectedItems()));
            }
            Intent intent = new Intent(PhotoPickerActivity.this, PhotoPagerActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, PhotoPickerHelper.REQUEST_CODE_SEE_BIG_PIC);
        }

        @Override
        public void onClickSelectIcon(View itemView, int position, PhotoFileEntity item, List<PhotoFileEntity> selectItems) {
            int size = selectItems != null ? selectItems.size() : 0;
            tv_done_notice.setText(getString(R.string.template_done, size));
        }

        @Override
        public boolean shouldIgnoreClickEventOfSelectIcon(int position, PhotoFileEntity item, List<PhotoFileEntity> selectItems) {
            return (selectItems != null && selectItems.size() == 9);
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("image", mPickerHelper.getCurrentPhotoPath());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPickerHelper.setPhotoPath(savedInstanceState.getString("image", null));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case PhotoPickerHelper.REQUEST_CODE_SEE_BIG_PIC:
                finishSelect();
                break;
            case PhotoPickerHelper.REQUEST_TAKE_PHOTO:
                mPickerHelper.scanFileToDatabase();
                // setting -> 开发者选项-》用户离开后即销毁每个活动。会造成当前的activity销毁。然后还没扫描完成就走 onActivityResult
                if (mPhotoDirs == null) {
                    mPickerHelper.scanPhotoes(this);
                    return;
                }
                String path = mPickerHelper.getCurrentPhotoPath();
                PhotoFileEntity entity = (PhotoFileEntity) PhotoPickerFactory.getPhotoFileEntityFactory()
                        .create(path.hashCode(), path);
                //add to dir
                final PhotoDirectoryEntity<PhotoFileEntity> dirs = mPhotoDirs.get(PhotoPickerHelper.INDEX_ALL_PHOTOS);
                dirs.getPhotos().add(0, entity);
                dirs.setPath(path);
                //notify adapter
                mPickerAdapter.clearAllSelected();
                mPickerAdapter.getAdapterManager().getItems().add(0, entity);
                // finishSelect();
                break;
        }
    }

    private void finishSelect() {
        Intent sIntent = new Intent();
        List<PhotoFileEntity> selectedPhotos = mPickerAdapter.getSelectHelper().getSelectedItems();
        sIntent.putParcelableArrayListExtra(PhotoPickerHelper.KEY_PHOTOES_SELECTED, (ArrayList<? extends Parcelable>) selectedPhotos);
        setResult(RESULT_OK, sIntent);
        finish();
    }

    @Override
    public void onResultCallback(List<PhotoDirectoryEntity<PhotoFileEntity>> directories) {
        this.mPhotoDirs = directories;
        final List<PhotoFileEntity> photos = directories.get(0).getPhotos();
        if (mPickerAdapter == null) {
            mPickerAdapter = new PhotoPickerAdapter<PhotoFileEntity>(R.layout.item_photo, photos, ISelectable.SELECT_MODE_MULTI) {
                @Override
                protected void applySelectState(ImageView selectIcon, boolean selected) {
                    selectIcon.setImageResource(selected ? R.mipmap.pic_check_select : R.mipmap.pic_check_normal);
                }

                @Override
                protected boolean bindCameraItemSuccess(Context context, int position, ViewHelper helper) {
                    return false;
                }

                @Override
                protected int getCameraItemLayoutId() {
                    return R.layout.item_photo_camera;
                }
            };
            mPickerAdapter.setCallback(mCallback);
            rv_photos.setAdapter(mPickerAdapter);
        } else {
            mPickerAdapter.getAdapterManager().replaceAllItems(photos);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mPickerHelper.scanPhotoes(this);
        }
    }

    static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if ((parent.getChildLayoutPosition(view) + 1) % 3 == 0) {
                outRect.right = space;
            } else {
                outRect.right = space;
            }
            outRect.bottom = space;
        }
    }
}
