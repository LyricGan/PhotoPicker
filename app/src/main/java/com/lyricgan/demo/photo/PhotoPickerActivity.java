package com.lyricgan.demo.photo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.lyricgan.media.photo.PhotoDirectoryEntity;
import com.lyricgan.media.photo.PhotoEntity;
import com.lyricgan.media.photo.PhotoPickerAdapter;
import com.lyricgan.media.photo.PhotoPickerFactory;
import com.lyricgan.media.photo.PhotoPickerHelper;
import com.lyricgan.media.photo.SelectMode;
import com.lyricgan.media.photo.ViewHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoPickerActivity extends Activity implements PhotoPickerHelper.OnPhotoResultCallback<PhotoEntity> {
    private static final int TOTAL_COUNT = 9;
    private TextView tvDoneNotice;
    private RecyclerView rvPhotos;

    private PhotoPickerHelper mPickerHelper;
    private List<PhotoDirectoryEntity<PhotoEntity>> mPhotoDirectoryList;
    private PhotoPickerAdapter<PhotoEntity> mPickerAdapter;
    private int mCurrentCount = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XXPermissions.with(this).permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {
                if (all) {
                    initViews(savedInstanceState);
                } else {
                    finish();
                }
            }

            @Override
            public void onDenied(List<String> permissions, boolean never) {
                finish();
            }
        });
    }

    private void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_photo_picker);
        ImageView ivBack = findViewById(R.id.iv_back);
        tvDoneNotice = findViewById(R.id.tv_done_notice);
        rvPhotos = findViewById(R.id.rv_photos);

        updateTitle(mCurrentCount);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        rvPhotos.setLayoutManager(layoutManager);
        rvPhotos.addItemDecoration(new SpacesItemDecoration((int) getResources().getDimension(R.dimen.photo_width)));

        mPickerHelper = PhotoPickerFactory.createPhotoPickerHelper(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            mPickerHelper.scanPhotoList(this);
        }

        ivBack.setOnClickListener(v -> finish());
        if (mPickerHelper != null && savedInstanceState != null) {
            mPickerHelper.setPhotoPath(savedInstanceState.getString("image", null));
        }
    }

    private final PhotoPickerAdapter.ICallback<PhotoEntity> mCallback = new PhotoPickerAdapter.ICallback<PhotoEntity>() {
        @Override
        public void onCameraViewClick(View itemView) {
            try {
                startActivityForResult(mPickerHelper.makeTakePhotoIntent(), PhotoPickerHelper.REQUEST_TAKE_PHOTO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onItemViewClick(View itemView, int position, PhotoEntity item) {
            List<PhotoEntity> selectItems = mPickerAdapter.getSelectHelper().getSelectedItems();
            ArrayList<PhotoEntity> photoList = new ArrayList<>(mPickerAdapter.getAdapterManager().getItems());
            if (mPickerAdapter.isShowCamera()) {
                photoList.remove(0);
                position -= 1;
            }
            Bundle bundle = new Bundle();
            bundle.putInt(PhotoPickerHelper.KEY_SELECT_INDEX, position);
            bundle.putParcelableArrayList(PhotoPickerHelper.KEY_PHOTOS, photoList);
            if (selectItems != null) {
                bundle.putParcelableArrayList(PhotoPickerHelper.KEY_PHOTOS_SELECTED, new ArrayList<>(mPickerAdapter.getSelectHelper().getSelectedItems()));
            }
            Intent intent = new Intent(PhotoPickerActivity.this, PhotoPagerActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, PhotoPickerHelper.REQUEST_CODE_SEE_BIG_PIC);
        }

        @Override
        public void onSelectViewClick(View itemView, int position, PhotoEntity item, List<PhotoEntity> selectItems) {
            mCurrentCount = selectItems != null ? selectItems.size() : 0;
            updateTitle(mCurrentCount);
        }

        @Override
        public boolean shouldIgnoreSelectViewClick(int position, PhotoEntity item, List<PhotoEntity> selectItems) {
            return (selectItems != null && selectItems.size() == TOTAL_COUNT && !item.isSelected());
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("image", mPickerHelper.getCurrentPhotoPath());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPickerHelper.setPhotoPath(savedInstanceState.getString("image", null));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PhotoPickerHelper.REQUEST_CODE_SEE_BIG_PIC: {
                finishSelect();
            }
                break;
            case PhotoPickerHelper.REQUEST_TAKE_PHOTO: {
                mPickerHelper.scanFileToDatabase();
                // setting -> 开发者选项-》用户离开后即销毁每个活动。会造成当前的activity销毁。然后还没扫描完成就走 onActivityResult
                if (mPhotoDirectoryList == null) {
                    mPickerHelper.scanPhotoList(this);
                    return;
                }
                String path = mPickerHelper.getCurrentPhotoPath();
                PhotoEntity entity = (PhotoEntity) PhotoPickerFactory.getPhotoEntityFactory().create(path.hashCode(), path);
                //add to dir
                final PhotoDirectoryEntity<PhotoEntity> dirs = mPhotoDirectoryList.get(PhotoPickerHelper.INDEX_ALL_PHOTOS);
                dirs.getPhotoList().add(0, entity);
                dirs.setPath(path);
                //notify adapter
                mPickerAdapter.getSelectHelper().clearAllSelected();
                mPickerAdapter.getAdapterManager().getItems().add(0, entity);
                // finishSelect();
            }
                break;
        }
    }

    private void updateTitle(int currentCount) {
        tvDoneNotice.setText(getString(R.string.template_done, currentCount, TOTAL_COUNT));
    }

    private void finishSelect() {
        Intent sIntent = new Intent();
        List<PhotoEntity> selectedPhotos = mPickerAdapter.getSelectHelper().getSelectedItems();
        sIntent.putParcelableArrayListExtra(PhotoPickerHelper.KEY_PHOTOS_SELECTED, (ArrayList<? extends Parcelable>) selectedPhotos);
        setResult(RESULT_OK, sIntent);
        finish();
    }

    @Override
    public void callback(List<PhotoDirectoryEntity<PhotoEntity>> directoryList) {
        this.mPhotoDirectoryList = directoryList;
        final List<PhotoEntity> photos = directoryList.get(0).getPhotoList();
        if (mPickerAdapter == null) {
            mPickerAdapter = new PhotoPickerAdapter<PhotoEntity>(R.layout.item_photo, photos, SelectMode.MULTIPLE) {
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
            rvPhotos.setAdapter(mPickerAdapter);
        } else {
            mPickerAdapter.getAdapterManager().replaceAllItems(photos);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mPickerHelper.scanPhotoList(this);
        }
    }

    private static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.right = space;
            outRect.bottom = space;
        }
    }
}
