package com.lyricgan.media.photo;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AdapterManager<T extends Selectable> implements SelectHelper.Callback<T> {
    private final IAdapterManagerCallback2 mCallback2;
    private final SelectHelper<T> mSelectHelper;
    private List<T> mDatas;
    private ArrayList<IPostRunnableCallback<T>> mPostCallbacks;

    /**
     * @param selectMode see {@link SelectMode#MULTIPLE} or {@link SelectMode#MULTIPLE}
     */
    AdapterManager(List<T> data, SelectMode selectMode, IAdapterManagerCallback2 callback2) {
        this.mDatas = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
        mSelectHelper = new SelectHelper<T>(selectMode, this);
        mSelectHelper.initSelectPositions(data);
        this.mCallback2 = callback2;
    }

    protected int getHeaderSize() {
        return getHeaderFooterManager() != null ? getHeaderFooterManager().getHeaderSize() : 0;
    }

    /***
     * the callbacks of IPostRunnableCallback.
     *
     * @since 1.7.5
     */
    ArrayList<IPostRunnableCallback<T>> getPostRunnableCallbacks() {
        return mPostCallbacks;
    }

    /**
     * add a IPostRunnableCallback to run the last of bind adapter data.
     *
     * @param callback the post callback
     * @since 1.7.5
     */
    public void addPostRunnableCallback(IPostRunnableCallback<T> callback) {
        if (mPostCallbacks == null) {
            mPostCallbacks = new ArrayList<>(4);
        }
        this.mPostCallbacks.add(callback);
    }

    /**
     * remove a IPostRunnableCallback to run the last of bind adapter data.
     *
     * @param callback the post callback
     * @since 1.7.5
     */
    public void removePostRunnableCallback(IPostRunnableCallback<T> callback) {
        if (mPostCallbacks != null) {
            mPostCallbacks.remove(callback);
        }
    }

    /**
     * clear the array of  IPostRunnableCallback
     *
     * @since 1.7.5
     */
    public void clearPostRunnableCallbacks() {
        if (mPostCallbacks != null) {
            mPostCallbacks.clear();
        }
    }
    //============================================
    public void addItem(T item, int position) {
        mDatas.add(position, item);
        if (isRecyclable()) {
            notifyItemInserted(mDatas.size() - 1 + getHeaderSize());
        } else {
            notifyDataSetChanged();
        }
    }

    public void addItem(T item) {
        addItem(item, mDatas.size());
    }

    public void addItems(T... items) {
        if (items == null || items.length == 0)
            return;
        addItems(Arrays.asList(items));
    }

    public void addItems(Collection<T> items) {
        final int preSize = mDatas.size();
        mDatas.addAll(items);
        if (isRecyclable()) {
            notifyItemRangeInserted(preSize + getHeaderSize(), items.size());
        } else {
            notifyDataSetChanged();
        }
    }

    public void setItem(T oldItem, T newItem) {
        setItem(mDatas.indexOf(oldItem), newItem);
    }

    public void setItem(int index, T newItem) {
        mDatas.set(index, newItem);
        if (isRecyclable()) {
            notifyItemChanged(index + getHeaderSize());
        } else {
            notifyDataSetChanged();
        }
    }

    public void removeItem(T item) {
        removeItem(mDatas.indexOf(item));
    }

    public void removeItem(int index) {
        mDatas.remove(index);
        if (isRecyclable()) {
            notifyItemRemoved(index + getHeaderSize());
        } else {
            notifyDataSetChanged();
        }
    }

    public void removeItems(List<T> ts) {
        if (ts == null || ts.size() == 0)
            return;
        List<T> mDatas = this.mDatas;
        for (int i = 0, size = ts.size(); i < size; i++) {
            mDatas.remove(ts.get(i));
        }
        notifyDataSetChanged();
    }

    public void removeItemsByPosition(List<Integer> positions) {
        if (positions == null || positions.size() == 0)
            return;
        List<T> mDatas = this.mDatas;
        int pos;
        for (int i = 0, size = positions.size(); i < size; i++) {
            pos = positions.get(i);
            mDatas.remove(pos);
        }
        notifyDataSetChanged();
    }

    public void replaceAllItems(List<T> items) {
        mDatas.clear();
        mDatas.addAll(items);
        mSelectHelper.initSelectPositions(items);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    public List<T> getItems() {
        return mDatas;
    }

    public boolean containsItem(T item) {
        return mDatas.contains(item);
    }

    @Override
    public final void notifyDataSetChanged() {
        mCallback2.beforeNotifyDataChanged();
        mCallback2.notifyDataSetChanged();
        mCallback2.afterNotifyDataChanged();
    }

    private void checkIfSupport() {
        if (!isRecyclable()) {
            throw new UnsupportedOperationException("only recycle view support");
        }
    }

    public final void notifyItemInserted(int position) {
        checkIfSupport();
        position += getHeaderSize();
        mCallback2.notifyItemInserted(position);
    }

    @Override
    public final void notifyItemChanged(int position) {
        checkIfSupport();
        position += getHeaderSize();
        mCallback2.notifyItemChanged(position);
    }

    public final void notifyItemRemoved(int position) {
        checkIfSupport();
        position += getHeaderSize();
        mCallback2.notifyItemRemoved(position);
    }

    public final void notifyItemMoved(int fromPosition, int toPosition) {
        checkIfSupport();
        fromPosition += getHeaderSize();
        toPosition += getHeaderSize();
        mCallback2.notifyItemMoved(fromPosition, toPosition);
    }

    public final void notifyItemRangeChanged(int positionStart, int itemCount) {
        checkIfSupport();
        positionStart += getHeaderSize();
        mCallback2.notifyItemRangeChanged(positionStart, itemCount);
    }

    public final void notifyItemRangeInserted(int positionStart, int itemCount) {
        checkIfSupport();
        positionStart += getHeaderSize();
        mCallback2.notifyItemRangeInserted(positionStart, itemCount);
    }

    public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
        checkIfSupport();
        positionStart += getHeaderSize();
        mCallback2.notifyItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    public boolean isRecyclable() {
        return mCallback2.isRecyclable();
    }


    @Override
    public T getSelectedItemAtPosition(int position) {
        return getItemAt(position);
    }

    public IHeaderFooterManager getHeaderFooterManager() {
        throw new UnsupportedOperationException();
    }

    public int getItemSize() {
        return mDatas.size();
    }

    public T getItemAt(int index) {
        return mDatas.get(index);
    }

    public SelectHelper<T> getSelectHelper() {
        return mSelectHelper;
    }

    /**
     * ths supporter of header and footer, call any method will automatic call
     * {@link PickerAdapter#notifyDataSetChanged()}.
     * in recyclerView .this is only can be used by LinearLayoutManager
     */
    public interface IHeaderFooterManager {

        void addHeaderView(View v);

        void removeHeaderView(View v);

        void addFooterView(View v);

        void removeFooterView(View v);

        int getHeaderSize();

        int getFooterSize();
    }

    public interface IAdapterManagerCallback<T extends Selectable> {

        AdapterManager<T> getAdapterManager();
    }

    /**
     * a callback will run after quickAdapter.onBindData(...) and in Runnable .
     * this is useful when you need post a runnable at last of bind adapter data in every position.
     *
     * @param <T>
     */
    public interface IPostRunnableCallback<T extends Selectable> {

        /**
         * called in every position's bind data.
         */
        void onPostCallback(int position, T item, int itemLayoutId, ViewHelper helper);
    }

    interface IAdapterManagerCallback2 {
        void notifyItemInserted(int position);

        void notifyItemChanged(int position);

        void notifyItemRemoved(int position);

        void notifyItemMoved(int fromPosition, int toPosition);

        void notifyItemRangeChanged(int positionStart, int itemCount);

        void notifyItemRangeInserted(int positionStart, int itemCount);

        void notifyItemRangeRemoved(int positionStart, int itemCount);

        // =========== end recycleview ==============//

        void notifyDataSetChanged();

        boolean isRecyclable();

        /**
         * this called before {@link #notifyDataSetChanged()}
         */
        void beforeNotifyDataChanged();

        /**
         * this called after {@link #notifyDataSetChanged()}
         */
        void afterNotifyDataChanged();
    }

    static abstract class SimpleAdapterManagerCallback2 implements IAdapterManagerCallback2 {

        public void notifyItemInserted(int position) {
        }

        public void notifyItemChanged(int position) {
        }

        public void notifyItemRemoved(int position) {
        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
        }
    }
}