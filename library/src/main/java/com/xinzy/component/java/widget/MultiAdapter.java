package com.xinzy.component.java.widget;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MultiAdapter extends RecyclerView.Adapter<MultiAdapter.MultiViewHolder> {
    private static final boolean DEBUG = true;
    private static final String TAG = "MultiAdapter";

    private List<ViewType> mDatas;
    private ItemTypePool mItemPool;

    private LayoutInflater mLayoutInflater;

    private OnItemClickListener mOnItemClickListener;
    private OnEventListener mOnEventListener;

    public MultiAdapter() {
        this(null);
    }

    public MultiAdapter(@Nullable List<ViewType> datas) {
        if (datas != null) {
            this.mDatas = datas;
        } else {
            mDatas = new ArrayList<>();
        }
        mItemPool = new ItemTypePool();
    }

    @NonNull
    @Override
    public MultiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemProvider provider = mItemPool.get(viewType);
        return provider.createViewHolder(mLayoutInflater, parent, mOnItemClickListener, mOnEventListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiViewHolder holder, int position) {
        final ViewType data = mDatas.get(position);
        debug(position + ": " + data);
        ItemProvider provider = mItemPool.get(data.getItemType());
        provider.bindViewHolder(holder, data, position);
    }

    @Override
    public int getItemViewType(int position) {
        ViewType type = mDatas.get(position);
        return mItemPool.indexOf(type.getItemType());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onViewAttachedToWindow(MultiViewHolder holder) {
        holder.onViewAttachedToWindow();
    }

    @Override
    public void onViewDetachedFromWindow(MultiViewHolder holder) {
        holder.onViewDetachedFromWindow();
    }

    @Override
    public void onViewRecycled(MultiViewHolder holder) {
        holder.onViewRecycled();
    }

    public Object getItem(int position) {
        return mDatas.get(position);
    }

    public List<ViewType> getData() {
        return mDatas;
    }

    public synchronized void add(ViewType data) {
        if (data == null) return;
        final int size = mDatas.size();
        mDatas.add(data);
        notifyItemInserted(size);
    }

    public synchronized void addAll(List<? extends ViewType> list) {
        if (list == null || list.size() == 0) return;
        final int count = list.size();
        final int size = mDatas.size();
        mDatas.addAll(list);
        notifyItemRangeInserted(size, count);
    }

    public synchronized void remove(int position) {
        if (position < 0 || position >= mDatas.size()) return;
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    public synchronized void replace(List<ViewType> list) {
        if (list == null || list.size() == 0) {
            clear();
        } else {
            mDatas = list;
            notifyDataSetChanged();
        }
    }

    public synchronized void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    public MultiAdapter registerProvider(@NonNull Object viewType, @NonNull ItemProvider provider) {
        mItemPool.registerProvider(viewType, provider);
        return this;
    }

    public MultiAdapter unregisterProvider(@NonNull Object viewType) {
        mItemPool.unregisterProvider(viewType);
        return this;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnEventListener(OnEventListener l) {
        this.mOnEventListener = l;
    }

    private void checkNotNull(Object param) {
        if (param == null) {
            throw new NullPointerException("argument cannot be null");
        }
    }

    private void debug(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }

    private class ItemTypePool {
        List<Object> mViewTypes = new ArrayList<>();
        List<ItemProvider> mProviders = new ArrayList<>();

        void registerProvider(Object viewType, ItemProvider provider) {
            checkNotNull(viewType);
            checkNotNull(provider);

            final int index = mViewTypes.indexOf(viewType);
            if (index >= 0) {
                mProviders.set(index, provider);
            } else {
                mViewTypes.add(viewType);
                mProviders.add(provider);
            }
        }

        boolean unregisterProvider(Object viewType) {
            checkNotNull(viewType);

            final int index = mViewTypes.indexOf(viewType);
            if (index >= 0) {
                mViewTypes.remove(index);
                mProviders.remove(index);
                return true;
            }
            return false;
        }

        int indexOf(Object viewType) {
            checkNotNull(viewType);

            final int index = mViewTypes.indexOf(viewType);
            if (index < 0) {
                throw new IllegalStateException("have not register provider");
            }
            return index;
        }

        ItemProvider get(int index) {
            return mProviders.get(index);
        }

        ItemProvider get(Object viewType) {
            return mProviders.get(indexOf(viewType));
        }
    }

    public static abstract class ItemProvider<T> {
        protected OnEventListener mOnEventListener;

        final MultiViewHolder createViewHolder(LayoutInflater inflater, ViewGroup parent,
                                               OnItemClickListener itemClickListener,
                                               OnEventListener eventListener) {
            mOnEventListener = eventListener;
            View view = inflater.inflate(getItemViewLayoutId(), parent, false);
            return new MultiViewHolder(view, itemClickListener, canClicked()).setItemProvider(this);
        }

        final void bindViewHolder(MultiViewHolder viewHolder, T data, int position) {
            viewHolder.setPosition(position);
            onBindViewHolder(viewHolder, data, position);
        }

        protected final void onEvent(View view, short event, int position, Object... params) {
            if (mOnEventListener != null) {
                mOnEventListener.onEvent(view, event, position, params);
            }
        }

        protected void onViewAttachedToWindow(MultiViewHolder holder) {}

        protected void onViewDetachedFromWindow(MultiViewHolder holder) {}

        protected void onViewRecycled(MultiViewHolder holder) {}

        protected boolean canClicked() {
            return false;
        }

        protected abstract void onBindViewHolder(@NonNull MultiViewHolder viewHolder, @NonNull T data, int position);

        @LayoutRes
        protected abstract int getItemViewLayoutId();
    }

    public static final class MultiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private SparseArray<View> mViews = new SparseArray<>();

        private int position;
        private ItemProvider itemProvider;

        private OnItemClickListener mOnItemClickListener;

        MultiViewHolder(View itemView, OnItemClickListener itemClickListener, boolean canClicked) {
            super(itemView);
            mOnItemClickListener = itemClickListener;
            if (canClicked) itemView.setOnClickListener(this);
        }

        void setPosition(int position) {
            this.position = position;
        }

        MultiViewHolder setItemProvider(ItemProvider provider) {
            itemProvider = provider;
            return this;
        }

        void onViewAttachedToWindow() {
            itemProvider.onViewAttachedToWindow(this);
        }

        void onViewDetachedFromWindow() {
            itemProvider.onViewDetachedFromWindow(this);
        }

        void onViewRecycled() {
            itemProvider.onViewRecycled(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(itemView, position);
            }
        }

        public View rootView() {
            return itemView;
        }

        public <V extends View> V get(@IdRes int resId) {
            View view = mViews.get(resId);
            if (view == null) {
                view = itemView.findViewById(resId);

                if (view == null) {
                    throw new IllegalArgumentException("no id");
                }
            }
            return (V) view;
        }
    }

    public interface ViewType {
        Object getItemType();
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public interface OnEventListener {
        void onEvent(View view, short event, int position, Object... param);
    }
}
