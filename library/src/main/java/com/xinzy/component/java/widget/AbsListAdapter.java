package com.xinzy.component.java.widget;


import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsListAdapter<T> extends BaseAdapter {
    private final Object mLock = new Object();
    protected List<T> mDatas;
    private LayoutInflater mInflater;

    public AbsListAdapter() {
        this(new ArrayList<T>());
    }

    public AbsListAdapter(List<T> mDatas) {
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return mDatas != null ? mDatas.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        ViewHolder holder = ViewHolder.get(convertView, parent, mInflater, getLayout());
        onBindData(holder, mDatas.get(position), position);
        return holder.getConvertView();
    }


    public void clear() {
        if (mDatas != null) {
            mDatas.clear();

            synchronized (mLock) {
                notifyDataSetChanged();
            }
        }
    }

    public void replace(List<T> lists) {
        if (mDatas != null) {
            mDatas.clear();
            mDatas.addAll(lists);
        } else {
            mDatas = lists;
        }

        synchronized (mLock) {
            notifyDataSetChanged();
        }
    }

    public void remove(int index) {
        if (index >= 0 && mDatas != null && index < mDatas.size()) {
            mDatas.remove(index);

            synchronized (mLock) {
                notifyDataSetChanged();
            }
        }
    }

    public void addAll(int index, List<T> list) {
        if (list != null) {
            if (mDatas == null) {
                mDatas = list;
            } else if (index < 0 || index > mDatas.size()) {
                mDatas.addAll(list);
            } else {
                mDatas.addAll(index, list);
            }

            synchronized (mLock) {
                notifyDataSetChanged();
            }
        }
    }

    public void addAll(List<T> lists) {
        addAll(-1, lists);
    }

    public void add(T t) {
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }
        mDatas.add(t);

        synchronized (mLock) {
            notifyDataSetChanged();
        }
    }

    public void add(int index, T t) {
        if (index >= 0 && mDatas != null && mDatas.size() > index) {
            mDatas.add(index, t);

            synchronized (mLock) {
                notifyDataSetChanged();
            }
        } else {
            add(t);
        }
    }
    public abstract int getLayout();
    public abstract void onBindData(@NonNull ViewHolder holder, @NonNull T item, int position);

    public static class ViewHolder {

        private View mConvertView;
        private SparseArray<View> mViews;

        private ViewHolder(View convertView) {
            mConvertView = convertView;
            mViews = new SparseArray<>();
        }

        public static ViewHolder get(View convertView, ViewGroup parent, LayoutInflater inflater, int layoutId) {
            if (convertView == null) {
                convertView = inflater.inflate(layoutId, parent, false);
                ViewHolder holder = new ViewHolder(convertView);
                convertView.setTag(holder);
                return holder;
            } else {
                return (ViewHolder) convertView.getTag();
            }
        }

        public <T extends View> T getView(int id) {
            View view = mViews.get(id);
            if (view == null) {
                view = mConvertView.findViewById(id);
                mViews.put(id, view);
            }

            return (T) view;
        }

        public View getConvertView() {
            return mConvertView;
        }

    }
}