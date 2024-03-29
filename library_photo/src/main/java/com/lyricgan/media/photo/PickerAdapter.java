package com.lyricgan.media.photo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class PickerAdapter<T extends Selectable> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements AdapterManager.IAdapterManagerCallback, AdapterManager.IHeaderFooterManager, AdapterManager.IAdapterManagerCallback2 {
    private final int mLayoutId;
    private HeaderFooterHelper mHeaderFooterHelper;
    private final AdapterManager<T> mAdapterManager;

    /**
     * create adapter with the layout id. if layoutId==0, the method
     * {@link #getItemLayoutId(int, Selectable)} will be called.
     *
     * @param layoutId the layout id you want to inflate, or 0 if you want multi item.
     * @param items list items
     */
    public PickerAdapter(int layoutId, List<T> items) {
        this(layoutId, items, SelectMode.SINGLE);
    }

    /**
     * create adapter with the layout id. if layoutId==0, the method
     * {@link #getItemLayoutId(int, Selectable)} will be called.
     *
     * @param layoutId the layout id you want to inflate, or 0 if you want multi item.
     * @param items list items
     * @param selectMode select mode
     */
    public PickerAdapter(int layoutId, List<T> items, SelectMode selectMode) {
        this(layoutId, items, selectMode, true);
    }

    /**
     * internal
     */
    PickerAdapter(int layoutId, List<T> items, SelectMode selectMode, boolean callFinalInit) {
        if (layoutId < 0) {
            throw new IllegalArgumentException("layoutId can't be negative ");
        }
        this.mLayoutId = layoutId;
        mAdapterManager = new AdapterManager<T>(items, selectMode, this) {
            @Override
            public IHeaderFooterManager getHeaderFooterManager() {
                return PickerAdapter.this;
            }
        };
        if (callFinalInit) {
            onFinalInit();
        }
    }

    /**
     * called before {@link #notifyDataSetChanged()}
     */
    @Override
    public void beforeNotifyDataChanged() {
    }

    /**
     * this is called after data {@link #notifyDataSetChanged()}
     */
    @Override
    public void afterNotifyDataChanged() {
    }

    /**
     * the init operation of the last, called in constructor
     */
    protected void onFinalInit() {
    }

    @Override
    public void addHeaderView(View v) {
        if (mHeaderFooterHelper == null)
            mHeaderFooterHelper = new HeaderFooterHelper();
        int headerSize = getHeaderSize();
        mHeaderFooterHelper.addHeaderView(v);
        notifyItemInserted(headerSize);
    }

    @Override
    public void removeHeaderView(View v) {
        if (mHeaderFooterHelper != null) {
            int index = mHeaderFooterHelper.removeHeaderView(v);
            if (index != -1) {
                notifyItemRemoved(index);
            }
        }
    }

    @Override
    public void addFooterView(View v) {
        if (mHeaderFooterHelper == null)
            mHeaderFooterHelper = new HeaderFooterHelper();
        int itemCount = getItemCount();
        mHeaderFooterHelper.addFooterView(v);
        notifyItemInserted(itemCount);
    }

    @Override
    public void removeFooterView(View v) {
        if (mHeaderFooterHelper != null) {
            int index = mHeaderFooterHelper.removeFooterView(v);
            if (index != -1) {
                notifyItemRemoved(index + getHeaderSize() + mAdapterManager.getItemSize());
            }
        }
    }

    @Override
    public int getHeaderSize() {
        return mHeaderFooterHelper == null ? 0 : mHeaderFooterHelper.getHeaderViewSize();
    }

    @Override
    public int getFooterSize() {
        return mHeaderFooterHelper == null ? 0 : mHeaderFooterHelper.getFooterViewSize();
    }

    @Override
    public final boolean isRecyclable() {
        return true;
    }

    public SelectHelper<T> getSelectHelper() {
        return getAdapterManager().getSelectHelper();
    }

    public final T getItem(int position) {
        return mAdapterManager.getItems().get(position);
    }

    @Override
    public AdapterManager<T> getAdapterManager() {
        return mAdapterManager;
    }

    @Override
    public final int getItemViewType(int position) {
        if (mHeaderFooterHelper != null) {
            //in header or footer
            if (mHeaderFooterHelper.isInHeader(position) || mHeaderFooterHelper.isInFooter(position, mAdapterManager.getItemSize()))
                return position;

            position -= mHeaderFooterHelper.getHeaderViewSize();
        }
        return getItemViewTypeImpl(mHeaderFooterHelper, position);
    }

    //extract for swipe adapter
    protected int getItemViewTypeImpl(HeaderFooterHelper hfHelper, int position) {
        int layoutId = getItemLayoutId(position, getItem(position));
        if (hfHelper != null)
            hfHelper.recordLayoutId(layoutId);
        return layoutId;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateViewHolderImpl(mHeaderFooterHelper, parent, viewType);
    }

    @NonNull
    protected RecyclerView.ViewHolder onCreateViewHolderImpl(HeaderFooterHelper hfHelper, ViewGroup parent, int viewType) {
        if (this.mHeaderFooterHelper == null || this.mHeaderFooterHelper.isLayoutIdInRecord(viewType)) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false), viewType);
        } else {
            return new ViewHolder(this.mHeaderFooterHelper.findView(viewType, mAdapterManager.getItemSize()));
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mHeaderFooterHelper != null) {
            if (mHeaderFooterHelper.isInHeader(position) || mHeaderFooterHelper.isInFooter(position, mAdapterManager.getItemSize())) {
                return;
            }
            position -= mHeaderFooterHelper.getHeaderViewSize();
        }
        if (!(holder instanceof IRecyclerViewHolder)) {
            throw new RuntimeException("all quick adapter's viewHolder must implement" +
                    " the interface IRecyclerViewHolder");
        }
        //not in header or footer populate it
        final T item = getItem(position);
        final int layoutId = ((IRecyclerViewHolder) holder).getLayoutId();
        final ViewHelper helper = ((IRecyclerViewHolder) holder).getViewHelper();

        onBindDataImpl(holder, position, item);

        if (getAdapterManager().getPostRunnableCallbacks() != null) {
            final int pos = position;
            for (final AdapterManager.IPostRunnableCallback<T> callback : getAdapterManager().getPostRunnableCallbacks()) {
                holder.itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onPostCallback(pos, item, layoutId, helper);
                    }
                });
            }
        }
    }

    protected void onBindDataImpl(RecyclerView.ViewHolder holder, int position, T item) {
        final ViewHolder vh = (ViewHolder) holder;
        onBindData(vh.getContext(), position, item, vh.getLayoutId(),
                vh.getViewHelper());
    }

    @Override
    public final int getItemCount() {
        return mHeaderFooterHelper == null ? mAdapterManager.getItemSize() :
                mAdapterManager.getItemSize() + mHeaderFooterHelper.getHeaderViewSize() + mHeaderFooterHelper.getFooterViewSize();
    }

    // may use
    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof IRecyclerViewHolder) {
            ((IRecyclerViewHolder) holder).getViewHelper().getRootView().clearAnimation();
        }
    }

    /**
     * if you use multi item ,override this
     */
    @LayoutRes
    protected int getItemLayoutId(int position, T t) {
        return mLayoutId;
    }

    protected abstract void onBindData(Context context, int position, T item, int itemLayoutId, ViewHelper helper);

    static class ViewHolder extends RecyclerView.ViewHolder implements IRecyclerViewHolder {

        public final ViewHelper mViewHelper;
        /**
         * if is in header or footer ,mLayoutId = 0
         */
        public final int mLayoutId;

        public ViewHolder(View itemView, int layoutId) {
            super(itemView);
            this.mLayoutId = layoutId;
            this.mViewHelper = new ViewHelper(itemView);
        }

        public ViewHolder(View itemView) {
            this(itemView, 0);
        }

        public Context getContext() {
            return mViewHelper.getContext();
        }

        @Override
        public int getLayoutId() {
            return mLayoutId;
        }

        @Override
        public ViewHelper getViewHelper() {
            return mViewHelper;
        }
    }

    public interface IRecyclerViewHolder {
        /**
         * get the item layout id
         */
        int getLayoutId();

        /**
         * get the ViewHelper
         */
        ViewHelper getViewHelper();
    }
}