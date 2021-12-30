package com.lyricgan.media.photo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lyricgan.media.photo.view.ImageDraweeView;
import com.lyricgan.media.photo.view.ImageScaleType;

import java.util.List;

public abstract class PhotoPickerAdapter<T extends IPhoto> extends PickerAdapter<T> {
    private static final int VIEW_TYPE_CAMERA = -1;
    private boolean mShowCamera = true;
    private ICallback<T> mCallback;

    /**
     * default select mode {@link SelectMode#SINGLE}
     *
     * @param layoutId item layout id
     * @param mDatas   the data of the grid
     */
    public PhotoPickerAdapter(int layoutId, List<T> mDatas) {
        super(layoutId, mDatas, SelectMode.SINGLE);
    }

    /**
     * @param layoutId   item layout id
     * @param mDatas     the data of the grid
     * @param selectMode the select mode ,see {@link SelectMode#MULTIPLE}
     *                   or  {@link SelectMode#SINGLE}
     */
    public PhotoPickerAdapter(int layoutId, List<T> mDatas, SelectMode selectMode) {
        super(layoutId, mDatas, selectMode);
        if (isShowCamera()) {
            T t = (T) PhotoPickerFactory.getPhotoEntityFactory().create(-1, "camera");
            getAdapterManager().addItem(t, 0);//add a place holder for show camera
        }
    }

    public void setCallback(ICallback<T> mCallback) {
        this.mCallback = mCallback;
    }

    public ICallback getCallback() {
        return mCallback;
    }

    public boolean isShowCamera() {
        return mShowCamera;
    }

    /**
     * set if show the first item as camera. default is true.
     *
     * @param show
     */
    public void setShowCamera(boolean show) {
        if (this.mShowCamera != show) {
            boolean oldShow = this.mShowCamera;
            this.mShowCamera = show;
            final AdapterManager<T> am = getAdapterManager();
            // false -> true
            if (!oldShow) {
                am.addItem(am.getItemAt(0));  //just add a holder for camera
            } else {
                //true -> false
                am.removeItem(0);
            }
        }
    }

    @Override
    protected int getItemViewTypeImpl(HeaderFooterHelper hfHelper, int position) {
        if (isShowCamera() && position == 0) {
            return VIEW_TYPE_CAMERA;
        }
        return super.getItemViewTypeImpl(hfHelper, position);
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolderImpl(HeaderFooterHelper hfHelper, ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CAMERA) {
            return new ViewHolder2(LayoutInflater.from(parent.getContext()).inflate(getCameraItemLayoutId(), parent, false));
        }
        return super.onCreateViewHolderImpl(hfHelper, parent, viewType);
    }

    /**
     * get the camera item layout id . default is equal to getItemLayoutId(0,null);
     */
    protected int getCameraItemLayoutId() {
        // throw new UnsupportedOperationException("if you need camera , you must overiride method 'getCameraItemLayoutId()'");
        return getItemLayoutId(0, null);
    }

    @Override
    protected void onBindDataImpl(RecyclerView.ViewHolder holder, int position, T item) {
        IRecyclerViewHolder vh = (IRecyclerViewHolder) holder;
        onBindData(vh.getViewHelper().getContext(), position, item, vh.getLayoutId(), vh.getViewHelper());
    }

    @Override
    protected void onBindData(Context context, final int position, final T item, int itemLayoutId, final ViewHelper helper) {
        if (itemLayoutId == 0) {
            if (bindCameraItemSuccess(context, position, helper)) {
                return;
            }
            helper.getRootView().setBackgroundColor(Color.LTGRAY);
            helper.setRootOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onCameraViewClick(v);
                    }
                }
            });
        } else {
            final ImageDraweeView view = helper.getView(R.id.photo_picker_iv_image);
            view.setImageScaleType(ImageScaleType.CENTER_CROP);
            ImageView iv = helper.getView(R.id.photo_picker_iv_select_icon);
            //apply select state
            applySelectState(iv, item.isSelected());

            //bind image and event
            helper.setImageUrl(R.id.photo_picker_iv_image, item.getPath(), PhotoPickerFactory.getImageLoader())
                    .setOnClickListener(R.id.photo_picker_iv_image, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCallback != null) {
                                mCallback.onItemViewClick(v, position, item);
                            }
                        }
                    })
                    .setOnClickListener(R.id.photo_picker_iv_select_icon, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCallback == null || mCallback.shouldIgnoreSelectViewClick(position, item, getSelectHelper().getSelectedItems())) {
                                return;
                            }
                            getSelectHelper().toggleSelected(position);
                            mCallback.onSelectViewClick(helper.getRootView(), position, item, getSelectHelper().getSelectedItems());
                        }
                    });
        }
    }

    /**
     * if you want to bind camera item by your self,please overrider this.
     *
     * @return false means bind the camera item with default action.
     */
    protected boolean bindCameraItemSuccess(Context context, int position, ViewHelper helper) {
        return false;
    }

    /**
     * apply the select state.
     *
     * @param selectIcon the imageview of select icon.
     * @param selected   the state , indicate is selected or not.
     */
    protected abstract void applySelectState(ImageView selectIcon, boolean selected);

    private static class ViewHolder2 extends RecyclerView.ViewHolder implements IRecyclerViewHolder {
        private final ViewHelper mViewHelper;

        public ViewHolder2(View itemView) {
            super(itemView);
            mViewHelper = new ViewHelper(itemView);
        }

        @Override
        public int getLayoutId() {
            return 0;
        }

        @Override
        public ViewHelper getViewHelper() {
            return mViewHelper;
        }
    }

    /**
     * the callback of item event
     *
     * @param <T>
     */
    public interface ICallback<T> {

        /**
         * called when the user click the camera item view.
         */
        void onCameraViewClick(View itemView);

        /**
         * called when the user click this whole item view , not the select icon.
         */
        void onItemViewClick(View itemView, int position, T item);

        /**
         * called when clicked the select icon.
         *
         * @param position    the position
         * @param item        the current item.
         * @param selectItems the select items after switch the select state of select icon.
         */
        void onSelectViewClick(View itemView, int position, T item, List<T> selectItems);

        /**
         * return true if you don't want to switch the select state, that means the click event of select icon is ignored.
         * only be called when user click the unselect item.
         * but if the target position's item was selected. the state is auto switch to unselected.
         *
         * @param position    the position
         * @param item        the current item.
         * @param selectItems the select items before this click event of  select icon.
         * @return true to ignore the event
         */
        boolean shouldIgnoreSelectViewClick(int position, T item, List<T> selectItems);
    }
}
