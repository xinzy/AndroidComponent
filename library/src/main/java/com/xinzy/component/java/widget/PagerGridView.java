package com.xinzy.component.java.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.Scroller;

import com.xinzy.component.R;

public class PagerGridView extends ViewGroup implements GestureDetector.OnGestureListener {
    private static final String TAG = "PagerGridView";
    private static final boolean DEBUG = true;

    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;

    private int mColumnNumber;
    private int mRowNumber;
    private int mVerticalSpacing;
    private int mHorizontalSpacing;

    private int mItemPageWidth; //单页的宽度
    private int mWidth;     // View 宽度
    private int mHeight;    // View 高度

    private int mPaddingStart;
    private int mPaddingEnd;
    private int mPaddingTop;
    private int mPaddingBottom;

    private int mCurrentPage = 0;
    private int mScrollDistance;

    private int mScrollState = SCROLL_STATE_IDLE;

    private GestureDetector mGestureDetector;
    private Scroller mScroller;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnPageChangeListener mOnPageChangeListener;

    private ListAdapter mAdapter;
    private AdapterDataSetObserver mDataSetObserver;

    public PagerGridView(Context context) {
        this(context, null);
    }

    public PagerGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mScroller = new Scroller(context);
        mGestureDetector = new GestureDetector(context, this);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PagerGridView);
        mColumnNumber = ta.getInt(R.styleable.PagerGridView_numColumns, 1);
        mRowNumber = ta.getInt(R.styleable.PagerGridView_numRows, 1);
        mVerticalSpacing = ta.getDimensionPixelSize(R.styleable.PagerGridView_verticalSpacing, 0);
        mHorizontalSpacing = ta.getDimensionPixelSize(R.styleable.PagerGridView_horizontalSpacing, 0);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mItemPageWidth = MeasureSpec.getSize(widthMeasureSpec);

        mPaddingStart = ViewCompat.getPaddingStart(this);
        mPaddingEnd = ViewCompat.getPaddingEnd(this);
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();

        int contentWidth = mItemPageWidth - mPaddingStart - mPaddingEnd;
        final int itemWidth = (contentWidth - (mColumnNumber - 1) * mHorizontalSpacing) / mColumnNumber;
        final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY);

        final int itemPageViewCount = mColumnNumber * mRowNumber;
        final int childrenCount = getChildCount();
        final int pageSize = getPageSize();
        debug("onMeasure: childrenCount=" + childrenCount + "; pages=" + pageSize);

        int height = 0;
        for (int i = 0; i < pageSize; i++) {
            int rows = currentPageRows(i);

            int itemPageHeight = mPaddingTop + mPaddingBottom + (rows - 1) * mVerticalSpacing;
            for (int j = 0; j < rows; j++) {
                int lineHeight = 0;
                for (int k = 0; k < mColumnNumber; k++) {
                    int index = i * itemPageViewCount + j * mColumnNumber + k;
                    if (index >= childrenCount) break;

                    View child = getChildAt(index);
                    LayoutParams lp = child.getLayoutParams();
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
                itemPageHeight += lineHeight;
            }
            if (height < itemPageHeight) {
                height = itemPageHeight;
            }
        }

        mHeight = height;
        mWidth = pageSize * mItemPageWidth;
        setMeasuredDimension(mWidth, mHeight);

        debug("mWidth = " + mWidth + "; mHeight = " + mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int itemPageViewCount = mColumnNumber * mRowNumber;

        final int contentWidth = mItemPageWidth - mPaddingStart - mPaddingEnd;
        final int itemWidth = (contentWidth - (mColumnNumber - 1) * mHorizontalSpacing) / mColumnNumber;

        final int childrenCount = getChildCount();
        final int pageSize = getPageSize();

        for (int i = 0; i < pageSize; i++) {
            int rows = currentPageRows(i);

            int height = mPaddingTop;
            for (int j = 0; j < rows; j++) {

                int startWidth = i * mItemPageWidth;
                int lineHeight = 0;
                for (int k = 0; k < mColumnNumber; k++) {
                    int index = itemPageViewCount * i + j * mColumnNumber + k;
                    if (index >= childrenCount) break;

                    View child = getChildAt(index);
                    int itemHeight = child.getMeasuredHeight();

                    final int left = startWidth + mPaddingStart + k * itemWidth + k * mHorizontalSpacing;
                    final int right = left + itemWidth;
                    final int top = height;
                    final int bottom = height + itemHeight;
                    child.layout(left, top, right, bottom);

                    if (lineHeight < itemHeight) {
                        lineHeight = itemHeight;
                    }
                }
                height += lineHeight + mVerticalSpacing;
            }
        }

        if (mCurrentPage >= getPageSize()) {    // 防止由于adapter数据源变更，页码减少后，页面却展示在最后一个空白页的问题
            smoothScrollToPage(getPageSize() - 1);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handle = mGestureDetector.onTouchEvent(event);
        if (handle) return true;

        final int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_UP) {
            onStateChange(SCROLL_STATE_SETTLING);
            if (mScrollDistance < 0) { //向左滑动
                if (Math.abs(mScrollDistance) > mItemPageWidth / 3) {
                    smoothScrollToPage(mCurrentPage + 1);
                } else {
                    smoothScrollToPage(mCurrentPage);
                }
                return true;
            } else if (mScrollDistance > 0) {
                if (mScrollDistance > mItemPageWidth / 3) {
                    smoothScrollToPage(mCurrentPage - 1);
                } else {
                    smoothScrollToPage(mCurrentPage);
                }
                return true;
            }
        }
        return false;
    }

    private int currentPageRows(int page) {
        if (page == getPageSize() - 1) {
            int itemCount = getChildCount() - mColumnNumber * mRowNumber * page;
            return (int) Math.ceil(itemCount * 1f / mColumnNumber);
        }
        return mRowNumber;
    }

    public int getPageSize() {
        return (int) Math.ceil(getChildCount() * 1f / (mColumnNumber * mRowNumber));
    }

    public boolean isFirstPage() {
        return mCurrentPage == 0;
    }

    public boolean isLastPage() {
        return mCurrentPage == getPageSize() - 1;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void nextPage() {
        nextPage(true);
    }

    public void nextPage(boolean smooth) {
        if (!isLastPage()) {
            if (smooth) {
                smoothScrollToPage(mCurrentPage + 1);
            } else {
                scrollToPage(mCurrentPage + 1);
            }
        }
    }

    public void previousPage() {
        previousPage(true);
    }

    public void previousPage(boolean smooth) {
        if (!isFirstPage()) {
            if (smooth) {
                smoothScrollToPage(mCurrentPage - 1);
            } else {
                scrollToPage(mCurrentPage - 1);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mOnItemClickListener = l;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener l) {
        this.mOnItemLongClickListener = l;
    }

    public void setOnPageChangeListener(OnPageChangeListener l) {
        this.mOnPageChangeListener = l;
    }

    private void onStateChange(int state) {
        if (mScrollState != state) {
            mScrollState = state;
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    }

    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        mAdapter = adapter;
        if (mAdapter != null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
            mDataSetObserver.onChanged();
        }
    }

    public void scrollToPage(int page) {
        if (page < 0 || page >= getPageSize()) return;
        mCurrentPage = page;
        int x = mItemPageWidth * page;
        scrollTo(x, 0);
    }

    public void smoothScrollToPage(int page) {
        if (page < 0 || page >= getPageSize()) return;
        mCurrentPage = page;

        int scrollX = getScrollX();
        int destX = page * mItemPageWidth;
        debug("smoothScrollToPage; scrollX=" + scrollX +"; destX=" + destX);

        mScroller.startScroll(scrollX, 0, destX - scrollX, 0);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            debug("computeScroll offset");
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        } else {
            debug("computeScroll end");
            onStateChange(SCROLL_STATE_IDLE);
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(mCurrentPage);
            }
        }
    }

    private void debug(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mScrollDistance = 0;
        debug("onDown");
        return !mScroller.computeScrollOffset();
    }

    @Override
    public void onShowPress(MotionEvent e) {
        debug("onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        debug("onSingleTapUp");
        return tapUp(e, false);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (Math.abs(distanceY) > Math.abs(distanceX)) return false;
        onStateChange(SCROLL_STATE_DRAGGING);

        float startX = e1.getX();
        float endX = e2.getX();
        mScrollDistance = (int) (endX - startX);

        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(mCurrentPage, distanceX, mScrollDistance);
        }

        if (mScrollDistance > 0) {    //向右滑动
            if (isFirstPage()) return false;
            int x = mCurrentPage * mItemPageWidth - mScrollDistance;
            scrollTo(x, 0);
        } else if (mScrollDistance < 0) {    //向左滑动
            if (isLastPage()) return false;
            int x = mCurrentPage * mItemPageWidth - mScrollDistance;
            scrollTo(x, 0);
        } else {
            return false;
        }

        debug("onScroll; distanceX=" + distanceX + "; distanceY=" + distanceY + "; mScrollDistance=" + mScrollDistance);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        debug("onLongPress");
        tapUp(e, true);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        onStateChange(SCROLL_STATE_SETTLING);

        if (mScrollDistance > 0) {    //向右滑动
            if (isFirstPage()) return false;
            if (velocityX < 0) {        // 速度向左
                smoothScrollToPage(mCurrentPage);
            } else {
                smoothScrollToPage(mCurrentPage - 1);
            }
        } else if (mScrollDistance < 0) {    //向左滑动
            if (isLastPage()) return false;

            if (velocityX < 0) {        // 速度向左
                smoothScrollToPage(mCurrentPage + 1);
            } else {        // 速度向右
                smoothScrollToPage(mCurrentPage);
            }
        } else {
            return false;
        }

        debug("onFling; velocityX=" + velocityX + "; velocityY=" + velocityY);
        return true;
    }

    private boolean tapUp(MotionEvent e, boolean isLongTap) {
        final int x = (int) e.getX() + mCurrentPage * mItemPageWidth;
        final int y = (int) e.getY();
        final int childrenCount = getChildCount();
        final int currentPageChildIndexStart = mCurrentPage * mColumnNumber * mRowNumber;
        if (currentPageChildIndexStart >= childrenCount) return false;
        int currentPageChildIndexEnd = (mCurrentPage + 1) * mColumnNumber * mRowNumber;
        if (currentPageChildIndexEnd > childrenCount) currentPageChildIndexEnd = childrenCount;

        for (int i = currentPageChildIndexStart; i < currentPageChildIndexEnd; i++) {
            View child = getChildAt(i);
            if (x >= child.getLeft() && x < child.getRight() && y > child.getTop() && y < child.getBottom()) {
                if (isLongTap && mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemLongClick(this, child, i, child.getId());
                } else if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(this, child, i, child.getId());
                }
                return true;
            }
        }
        return false;
    }

    private class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            PagerGridView.this.removeAllViews();
            if (mAdapter == null) return;
            final int count = mAdapter.getCount();
            for (int i = 0; i < count; i++) {
                View child = mAdapter.getView(i, null, PagerGridView.this);
                addView(child);
            }
            requestLayout();
        }
    }

    public interface OnPageChangeListener {
        void onPageScrollStateChanged(int state);
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
        void onPageSelected(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(PagerGridView pagerGridView, View view, int position, long id);
    }

    public interface OnItemClickListener {
        void onItemClick(PagerGridView pagerGridView, View view, int position, long id);
    }
}
