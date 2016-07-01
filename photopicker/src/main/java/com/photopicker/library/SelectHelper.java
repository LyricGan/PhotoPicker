package com.photopicker.library;

import java.util.ArrayList;
import java.util.List;

public class SelectHelper<T extends Selectable> {
    private static final int INVALID_POSITION = -1;
    private final SelectMode mSelectMode;

    private List<Integer> mSelectedPositionList;
    private List<Integer> mTempPositionList;
    private int mSelectedPosition = -1;

    private List<T> mSelectDataList;
    private Callback<T> mCallback;

    private SelectHelper(SelectMode selectMode) {
        if (SelectMode.MULTIPLE == selectMode) {
            this.mSelectedPositionList = new ArrayList<>();
        }
        this.mSelectMode = selectMode;
    }

    public SelectHelper(SelectMode selectMode, Callback<T> callback) {
        this(selectMode);
        this.mCallback = callback;
    }

    /**
     * select the target position with notify data.if currentPosition  == position.ignore it.
     * <li></>only support select mode = {@link SelectMode#SINGLE} ,this will auto update
     **/
    public void setSelected(int position) {
        if (mSelectMode == SelectMode.MULTIPLE)
            return;
        if (mSelectedPosition == position) {
            return;
        }
        if (position < 0)
            throw new IllegalArgumentException();
        final Callback<T> mCallback = this.mCallback;
        if (mSelectedPosition != INVALID_POSITION) {
            mCallback.getSelectedItemAtPosition(mSelectedPosition).setSelected(false);
            if (mCallback.isRecyclable()) {
                mCallback.notifyItemChanged(mSelectedPosition);
            }
        }
        mSelectedPosition = position;
        mCallback.getSelectedItemAtPosition(position).setSelected(true);
        if (mCallback.isRecyclable()) {
            mCallback.notifyItemChanged(position);
        } else
            mCallback.notifyDataSetChanged();
    }

    /**
     * {@link SelectMode#MULTIPLE} and {@link SelectMode#SINGLE} both support
     */
    public void unSelect(int position) {
        if (mSelectMode == SelectMode.MULTIPLE) {
            removeSelected(position);
        } else {
            setUnselected(position);
        }
    }

    /**
     * unselect the position of item ,
     * only support select mode = {@link SelectMode#SINGLE}
     */
    public void setUnselected(int position) {
        if (mSelectMode == SelectMode.MULTIPLE)
            return;
        //  mSelectedPosition must == position
        if (mSelectedPosition == INVALID_POSITION || mSelectedPosition != position) {
            return;
        }
        if (position < 0)
            throw new IllegalArgumentException();

        final Callback<T> mCallback = this.mCallback;
        mCallback.getSelectedItemAtPosition(position).setSelected(false);
        mSelectedPosition = INVALID_POSITION;
        if (mCallback.isRecyclable()) {
            mCallback.notifyItemChanged(position);
        } else {
            mCallback.notifyDataSetChanged();
        }
    }

    public void removeSelected(int position) {
        if (mSelectedPositionList == null || mSelectedPositionList.isEmpty()) {
            return;
        }
        if (!mSelectedPositionList.contains(position)) {
            return;
        }
        mSelectedPositionList.remove(Integer.valueOf(position));
        final Callback<T> callback = this.mCallback;
        callback.getSelectedItemAtPosition(position).setSelected(false);
        if (callback.isRecyclable()) {
            callback.notifyItemChanged(position);
        } else {
            callback.notifyDataSetChanged();
        }
    }

    public void addSelected(int position) {
        if (mSelectedPositionList == null) {
            mSelectedPositionList = new ArrayList<>();
        }
        if (mSelectedPositionList.contains(position)) {
            return;
        }
        mSelectedPositionList.add(position);
        final Callback<T> callback = this.mCallback;
        callback.getSelectedItemAtPosition(position).setSelected(true);
        if (callback.isRecyclable()) {
            callback.notifyItemChanged(position);
        } else {
            callback.notifyDataSetChanged();
        }
    }

    /**
     * clear the select state but not notify data changed.
     */
    public void clearSelectedPositions() {
        if (mSelectMode == SelectMode.MULTIPLE) {
            mSelectedPositionList.clear();
        } else {
            mSelectedPosition = INVALID_POSITION;
        }
    }

    /**
     * clear the all selected state  and notify data change.
     */
    public void clearAllSelected() {
        final Callback<T> mCallback = this.mCallback;
        final boolean recyclable = mCallback.isRecyclable();
        if (mSelectMode == SelectMode.MULTIPLE) {
            int pos;
            final List<Integer> mSelectedPositions = this.mSelectedPositionList;
            for (int i = 0, size = mSelectedPositions.size(); i < size; i++) {
                pos = mSelectedPositions.get(i);
                mCallback.getSelectedItemAtPosition(pos).setSelected(false);
                if (recyclable) {
                    mCallback.notifyItemChanged(pos);
                }
            }
            mSelectedPositions.clear();
            if (!recyclable) {
                mCallback.notifyDataSetChanged();
            }
        } else {
            if (mSelectedPosition != INVALID_POSITION) {
                int preSelectPos = mSelectedPosition;
                mSelectedPosition = INVALID_POSITION;
                mCallback.getSelectedItemAtPosition(preSelectPos).setSelected(false);
                if (recyclable) {
                    mCallback.notifyItemChanged(preSelectPos);
                } else {
                    mCallback.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * toggle the all selected state and notify data change.
     */
    public void toggleSelected(int position) {
        if (position < 0) {
            return;
        }
        if (SelectMode.MULTIPLE == mSelectMode) {
            if (mSelectedPositionList.contains(position)) {
                removeSelected(position);
            } else {
                addSelected(position);
            }
        } else {
            if (mSelectedPosition == position) {
                setUnselected(position);
            } else {
                setSelected(position);
            }
        }
    }

    public T getSelectedItem() {
        if (mSelectedPosition == INVALID_POSITION)
            return null;
        return mCallback.getSelectedItemAtPosition(mSelectedPosition);
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public List<Integer> getSelectedPositions() {
        if (mTempPositionList == null) {
            mTempPositionList = new ArrayList<>();
        }
        mTempPositionList.clear();
        mTempPositionList.addAll(mSelectedPositionList);
        return mTempPositionList;
    }

    public List<T> getSelectedItems() {
        if (mSelectedPositionList == null || mSelectedPositionList.isEmpty()) {
            return null;
        }
        if (mSelectDataList == null) {
            mSelectDataList = new ArrayList<>();
        }
        mSelectDataList.clear();
        final Callback<T> callback = this.mCallback;
        for (int i = 0, size = mSelectedPositionList.size(); i < size; i++) {
            mSelectDataList.add(callback.getSelectedItemAtPosition(mSelectedPositionList.get(i)));
        }
        return mSelectDataList;
    }

    /*public*/ void initSelectPositions(List<T> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i = 0, size = list.size(); i < size; i++) {
            if (list.get(i).isSelected()) {
                initSelectPosition(i);
            }
        }
    }

    /**
     * this will only called once.
     */
    private void initSelectPosition(int position) {
        if (mSelectMode == SelectMode.SINGLE) {
            if (mSelectedPosition == INVALID_POSITION) {
                mSelectedPosition = position;
            }
        } else if (mSelectMode == SelectMode.MULTIPLE) {
            if (!mSelectedPositionList.contains(position))
                mSelectedPositionList.add(position);
        } else {
            //can't reach here
            throw new RuntimeException();
        }
    }

    /**
     * @param <T>
     * @since 1.7.5
     */
    public interface Callback<T> {
        /**
         * indicate it use BaseAdapter/BaseExpandableListAdapter or QuickRecycleViewAdapter
         */
        boolean isRecyclable();

        /**
         * update the datas of adapter
         */
        void notifyDataSetChanged();

        /**
         * only used for  RecycleViewAdapter
         */
        void notifyItemChanged(int itemPosition);

        T getSelectedItemAtPosition(int position);
    }

}