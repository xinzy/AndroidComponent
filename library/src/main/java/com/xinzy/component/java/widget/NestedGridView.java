package com.xinzy.component.java.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinzy.component.R;

import java.util.ArrayList;
import java.util.List;

public class NestedGridView extends ViewGroup {
    private static final String TAG = "NestedGridView";
    private static final boolean DEBUG = false;

    protected Context mContext;

    private int mColumnNumber;
    private int mVerticalSpacing;
    private int mHorizontalSpacing;

    private int mWidth;
    private int mHeight;

    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingTop;
    private int mPaddingBottom;

    private NestedGridAdapter mAdapter;
    private List<View> mCachedItemViews;
    private LayoutInflater mInflater;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public NestedGridView(Context context) {
        this(context, null);
    }

    public NestedGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mCachedItemViews = new ArrayList<>();
        mInflater = LayoutInflater.from(mContext);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NestedGridView);
        mColumnNumber = ta.getInt(R.styleable.NestedGridView_numColumns, 1);
        mVerticalSpacing = ta.getDimensionPixelSize(R.styleable.NestedGridView_verticalSpacing, 0);
        mHorizontalSpacing = ta.getDimensionPixelSize(R.styleable.NestedGridView_horizontalSpacing, 0);
        ta.recycle();
    }

    public void setColumnNumber(int columnNumber) {
        if (columnNumber > 0) {
            this.mColumnNumber = columnNumber;
            requestLayout();
        }
    }

    public int getColumnNumber() {
        return mColumnNumber;
    }

    public void setAdapter(NestedGridAdapter adapter) {
        this.mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.setGridView(this);
            mAdapter.notifyDataSetChanged();
        }
    }

    public NestedGridAdapter getAdapter() {
        return mAdapter;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        mPaddingLeft = ViewCompat.getPaddingStart(this);
        mPaddingRight = ViewCompat.getPaddingEnd(this);
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();

        int contentWidth = width - mPaddingLeft - mPaddingRight;
        final int itemWidth = (contentWidth - (mColumnNumber - 1) * mHorizontalSpacing) / mColumnNumber;
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY);

        final int childrenCount = getChildCount();
        final int lines = (int) Math.ceil(childrenCount * 1f / mColumnNumber);
        debug("onMeasure: childrenCount = " + childrenCount + "; lines = " + lines);

        int height = mPaddingTop + mPaddingBottom;
        if (lines > 0) {
            height += (lines - 1) * mVerticalSpacing;
        }
        for (int i = 0; i < lines; i++) {
            int lineHeight = 0;
            for (int j = 0; j < mColumnNumber; j++) {
                int index = i * mColumnNumber + j;
                if (index < childrenCount) {
                    View child = getChildAt(index);
                    ViewGroup.LayoutParams lp = child.getLayoutParams();
                    int childHeightMeasureSpec;
                    if (lp.height == LayoutParams.WRAP_CONTENT || lp.height == LayoutParams.MATCH_PARENT) {
                        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.UNSPECIFIED);
                    } else {
                        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
                    }
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                    int itemHeight = child.getMeasuredHeight();
                    if (itemHeight > lineHeight) {
                        lineHeight = itemHeight;
                    }
                    debug("child" + index + " height = " + itemHeight + "; width = " + child.getMeasuredWidth());
                }
            }
            height += lineHeight;
        }
        setMeasuredDimension(width, height);
        mWidth = width;
        mHeight = height;
        debug("mWidth = " + mWidth + "; mHeight = " + mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int contentWidth = mWidth - mPaddingLeft - mPaddingRight;
        final int itemWidth = (contentWidth - mHorizontalSpacing * (mColumnNumber - 1)) / mColumnNumber;
        debug("itemWidth=" + itemWidth);

        final int childrenCount = getChildCount();
        final int lines = (int) Math.ceil(childrenCount * 1f / mColumnNumber);

        int height = mPaddingTop;
        for (int i = 0; i < lines; i++) {
            int lineHeight = 0;
            for (int j = 0; j < mColumnNumber; j++) {
                int index = i * mColumnNumber + j;
                if (index < childrenCount) {
                    View child = getChildAt(index);
                    int itemHeight = child.getMeasuredHeight();

                    final int left = mPaddingLeft + j * itemWidth + j * mHorizontalSpacing;
                    final int right = left + itemWidth;
                    final int top = height;
                    final int bottom = height + itemHeight;
                    child.layout(left, top, right, bottom);

                    if (itemHeight > lineHeight) {
                        lineHeight = itemHeight;
                    }
                }
            }
            height += lineHeight + mVerticalSpacing;
        }
    }

    public void updateUI() {
        if (mAdapter == null || mAdapter.getCount() == 0) {
            removeAllViews();
        } else {
            final int size = mAdapter.getCount();
            final int childrenCount = getChildCount();
            int cacheSize = mCachedItemViews.size();
            if (size < childrenCount) {
                removeViews(size, childrenCount - size);
                while (cacheSize > size) {
                    mCachedItemViews.remove(--cacheSize);
                }
            }

            for (int i = 0; i < size; i++) {
                View convertView;
                if (cacheSize > i) {
                    convertView = mCachedItemViews.get(i);
                } else {
                    convertView = mInflater.inflate(mAdapter.getLayout(), this, false);
                    mCachedItemViews.add(convertView);
                }
                mAdapter.bindData(convertView, i);
                convertView.setOnClickListener(new OnItemClick(convertView, i));
                convertView.setOnLongClickListener(new OnItemLongClick(convertView, i));

                if (convertView.getParent() == null) {
                    addView(convertView);
                }
            }
        }
    }

    private void debug(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static abstract class NestedGridAdapter<T> {
        private final Object mLock = new Object();
        protected List<T> mDatas = new ArrayList<>();
        private NestedGridView mGridView;

        public NestedGridAdapter() {
        }

        public NestedGridAdapter(List<T> datas) {
            this.mDatas = datas;
        }

        void setGridView(NestedGridView view) {
            this.mGridView = view;
        }

        public int getCount() {
            return mDatas == null ? 0 : mDatas.size();
        }

        public T getItem(int position) {
            return mDatas != null && mDatas.size() > position ? mDatas.get(position) : null;
        }

        void bindData(View convertView, int position) {
            onBindData(convertView, getItem(position), position);
        }

        public abstract int getLayout();

        protected abstract void onBindData(View convertView, T item, int position);

        public void clear() {
            if (mDatas != null) {
                synchronized (mLock) {
                    mDatas.clear();
                    notifyDataSetChanged();
                }
            }
        }

        public void replace(List<T> lists) {
            synchronized (mLock) {
                if (mDatas == null) {
                    mDatas = lists;
                } else {
                    mDatas.clear();
                    if (lists != null && lists.size() > 0) {
                        mDatas.addAll(lists);
                    }
                }
                notifyDataSetChanged();
            }
        }

        public void remove(int index) {
            if (index >= 0 && mDatas != null && index < mDatas.size()) {
                synchronized (mLock) {
                    mDatas.remove(index);
                    notifyDataSetChanged();
                }
            }
        }

        public void addAll(List<T> lists) {
            if (lists != null) {
                synchronized (mLock) {
                    if (mDatas == null) {
                        mDatas = lists;
                    } else {
                        mDatas.addAll(lists);
                    }
                    notifyDataSetChanged();
                }
            }
        }

        public void add(T t) {
            if (mDatas == null) {
                mDatas = new ArrayList<>();
            }

            synchronized (mLock) {
                mDatas.add(t);
                notifyDataSetChanged();
            }
        }

        public void add(int index, T t) {
            if (index >= 0 && mDatas != null && mDatas.size() > index) {
                synchronized (mLock) {
                    mDatas.add(index, t);
                    notifyDataSetChanged();
                }
            }
        }

        public void notifyDataSetChanged() {
            if (mGridView != null) {
                mGridView.updateUI();
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(NestedGridView parent, View itemView, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(NestedGridView parent, View itemView, int position);
    }

    private class OnItemClick implements OnClickListener {
        private View mConvertView;
        private int mPosition;

        public OnItemClick(View view, int position) {
            this.mConvertView = view;
            this.mPosition = position;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(NestedGridView.this, mConvertView, mPosition);
            }
        }
    }

    private class OnItemLongClick implements OnLongClickListener {
        private View mConvertView;
        private int mPosition;

        public OnItemLongClick(View view, int position) {
            this.mConvertView = view;
            this.mPosition = position;
        }

        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onItemLongClick(NestedGridView.this, mConvertView, mPosition);
                return true;
            }
            return false;
        }
    }
}