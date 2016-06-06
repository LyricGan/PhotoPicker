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

import com.heaven7.adapter.ISelectable;
import com.heaven7.core.util.ViewHelper;
import com.photopicker.app.R;
import com.photopicker.library.picker.BasePhotoFileEntity;
import com.photopicker.library.picker.PhotoDirectory;
import com.photopicker.library.picker.PhotoGridAdapter;
import com.photopicker.library.picker.PhotoPickerFactory;
import com.photopicker.library.picker.PhotoPickerHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoPickerTestActivity extends Activity implements PhotoPickerHelper.PhotoLoadResultCallback<BasePhotoFileEntity> {
    TextView tv_all_image;
    TextView tv_done_notice;
    ImageView iv_Back;
    RecyclerView rv_photos;

    private PhotoPickerHelper mPickerHelper;
    private List<PhotoDirectory<BasePhotoFileEntity>> mPhotoDirs;

    private PhotoGridAdapter<BasePhotoFileEntity> mGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_photo_picker);
        tv_all_image = (TextView) findViewById(R.id.tv_all_image);
        tv_done_notice = (TextView) findViewById(R.id.tv_done_notice);
        iv_Back = (ImageView) findViewById(R.id.iv_back);
        rv_photos = (RecyclerView) findViewById(R.id.rv_photos);

        iv_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        rv_photos.setLayoutManager(layoutManager);
        rv_photos.addItemDecoration(new SpacesItemDecoration((int) getResources().getDimension(R.dimen.photo_width)));

        PhotoPickerFactory.setImageLoader(new DraweeImageLoader(0));
        //this also is the default factory
        PhotoPickerFactory.setPhotoFileEntityFactory(new PhotoPickerFactory.IPhotoFileEntityFactory<BasePhotoFileEntity>() {
            @Override
            public BasePhotoFileEntity create(int id, String path) {
                return new BasePhotoFileEntity(id, path);
            }
        });
        mPickerHelper = PhotoPickerFactory.createPhotoPickerHelper(this);

        if (savedInstanceState != null) {
            mPickerHelper.setPhotoPath(savedInstanceState.getString("image", null));
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            mPickerHelper.scanPhotoes(this);
        }
    }

    private final PhotoGridAdapter.ICallback<BasePhotoFileEntity> mCallback = new PhotoGridAdapter.ICallback<BasePhotoFileEntity>() {
        @Override
        public void onClickCamera(View itemView) {
            try {
                startActivityForResult(mPickerHelper.makeTakePhotoIntent(), PhotoPickerHelper.REQUEST_TAKE_PHOTO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClickItemView(View itemView, int position, BasePhotoFileEntity item) {
            List<BasePhotoFileEntity> selectItems = mGridAdapter.getSelectHelper().getSelectedItems();
            ArrayList<BasePhotoFileEntity> photoes = new ArrayList<>(mGridAdapter.getAdapterManager().getItems());
            if (mGridAdapter.isShowCamera()) {
                photoes.remove(0);
                position -= 1;
            }
            Bundle b = new Bundle();
            b.putInt(PhotoPickerHelper.KEY_SELECT_INDEX, position);
            b.putParcelableArrayList(PhotoPickerHelper.KEY_PHOTOES, photoes);
            if (selectItems != null) {
                b.putParcelableArrayList(PhotoPickerHelper.KEY_PHOTOES_SELECTED, new ArrayList<>(mGridAdapter.getSelectHelper().getSelectedItems()));
            }
            Intent intent = new Intent(PhotoPickerTestActivity.this, PhotoPagerActivity.class);
            intent.putExtras(b);
            startActivityForResult(intent, PhotoPickerHelper.REQUEST_CODE_SEE_BIG_PIC);
        }

        @Override
        public void onClickSelectIcon(View itemView, int position, BasePhotoFileEntity item, List<BasePhotoFileEntity> selectItems) {
            int size = selectItems != null ? selectItems.size() : 0;
            tv_done_notice.setText(getString(R.string.template_done, size));
        }

        @Override
        public boolean shouldIgnoreClickEventOfSelectIcon(int position, BasePhotoFileEntity item, List<BasePhotoFileEntity> selectItems) {
            if (selectItems != null && selectItems.size() == 9) {
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("image", mPickerHelper.getCurrentPhotoPath());
        super.onSaveInstanceState(outState);
    }

    @Override //有时候直接走oncreate.有时候走 onRestoreInstanceState
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
                BasePhotoFileEntity entity = (BasePhotoFileEntity) PhotoPickerFactory.getPhotoFileEntityFactory()
                        .create(path.hashCode(), path);
                //add to dir
                final PhotoDirectory<BasePhotoFileEntity> dirs = mPhotoDirs.get(PhotoPickerHelper.INDEX_ALL_PHOTOS);
                dirs.getPhotos().add(0, entity);
                dirs.setPath(path);
                //notify adapter
                mGridAdapter.clearAllSelected();
                mGridAdapter.getAdapterManager().getItems().add(0, entity);
                // finishSelect();
                break;
        }
    }

    private void finishSelect() {
        Intent sIntent = new Intent();
        List<BasePhotoFileEntity> selectedPhotos = mGridAdapter.getSelectHelper().getSelectedItems();
        sIntent.putParcelableArrayListExtra(PhotoPickerHelper.KEY_PHOTOES_SELECTED, (ArrayList<? extends Parcelable>) selectedPhotos);
        setResult(RESULT_OK, sIntent);
        finish();
    }

    @Override
    public void onResultCallback(List<PhotoDirectory<BasePhotoFileEntity>> directories) {
        this.mPhotoDirs = directories;
        ////directories.get(0) contains the all photoes. so this as the whole directory.
        final List<BasePhotoFileEntity> photos = directories.get(0).getPhotos();
        if (mGridAdapter == null) {
            if (photos.size() == 0) {
                // return;
            }
            mGridAdapter = new PhotoGridAdapter<BasePhotoFileEntity>(R.layout.item_photo,
                    photos, ISelectable.SELECT_MODE_MULTI) {
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
            mGridAdapter.setCallback(mCallback);
            rv_photos.setAdapter(mGridAdapter);
        } else {
            mGridAdapter.getAdapterManager().replaceAllItems(photos);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mPickerHelper.scanPhotoes(this);
        } else {
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
