package com.xinzy.component.kotlin.widget


import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Handler
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.xinzy.component.util.dp2px

class BannerView : FrameLayout, ViewPager.OnPageChangeListener, Runnable {



    private val mViewPager = ViewPager(context)
    private val mLinearLayout: LinearLayout = LinearLayout(context)

    private val mHandler = Handler()

    private var mAdapter: Adapter? = null
    private val mPagerAdapter = InnerAdapter()

    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemSelectedListener: OnItemSelectedListener? = null

    private var mAutoStart = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    companion object {
        private const val INDICATION_SIZE = 6
    }

    init {
        addView(mViewPager, FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        mViewPager.adapter = mPagerAdapter
        mViewPager.addOnPageChangeListener(this)

        mLinearLayout.orientation = LinearLayout.HORIZONTAL
        mLinearLayout.gravity = Gravity.CENTER_HORIZONTAL
        val lp = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.BOTTOM
        lp.bottomMargin = dp2px(4)
        addView(mLinearLayout, lp)
    }

    fun setAutoStart(b: Boolean) {
        mAutoStart = b
    }

    fun setAdapter(adapter: Adapter) {
        mAdapter = adapter
        mViewPager.removeAllViews()
        mPagerAdapter.notifyDataSetChanged()

        createIndication()
        if (mAutoStart) {
            startFlip()
        }
    }

    fun setOnItemClickListener(callback: (position: Int) -> Unit) {
        mOnItemClickListener = object : OnItemClickListener {
            override fun onItemClick(view: BannerView, position: Int) {
                callback(position)
            }
        }
    }

    fun setOnItemClickListener(l: OnItemClickListener?) {
        mOnItemClickListener = l
    }

    fun setOnItemSelectedListener(l: OnItemSelectedListener?) {
        mOnItemSelectedListener = l
    }

    fun startFlip() {
        mHandler.removeCallbacks(this)
        if (mAutoStart && mAdapter != null && mAdapter!!.getCount() > 0) {
            mHandler.postDelayed(this, 3000)
        }
    }

    fun stopFlip() {
        mHandler.removeCallbacks(this)
    }

    override fun run() {
        mViewPager.currentItem++
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (state == ViewPager.SCROLL_STATE_DRAGGING) stopFlip()
        else if (state == ViewPager.SCROLL_STATE_IDLE) startFlip()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        val count = mLinearLayout.childCount
        for (i in 0 until count) {
            mLinearLayout.getChildAt(i).isSelected = ((position % count) == i)
        }
        mOnItemSelectedListener?.onItemSelected(this, position % count)
    }

    private fun createIndication() {
        mLinearLayout.removeAllViews()
        mAdapter?.let {
            val size = dp2px(6)

            for (i in 0 until mAdapter!!.getCount()) {
                val lp = ViewGroup.MarginLayoutParams(size, size)
                lp.leftMargin  = size
                lp.rightMargin = size
                mLinearLayout.addView(indication(i == 0), lp)
            }
        }
    }

    private fun indication(isSelected: Boolean): View {
        val img = View(context)

        val drawable = StateListDrawable()
        val selected = GradientDrawable()
        selected.setColor(0xFFCCCCCC.toInt())
        selected.cornerRadius = dp2px(INDICATION_SIZE).toFloat()

        val default = GradientDrawable()
        default.cornerRadius = dp2px(INDICATION_SIZE).toFloat()
        default.setColor(0xFFEEEEEE.toInt())

        drawable.addState(intArrayOf(android.R.attr.state_selected), selected)
        drawable.addState(intArrayOf(), default)

        img.background = drawable
        img.isSelected = isSelected

        return img
    }


    private inner class InnerAdapter : PagerAdapter() {

        private var mLayoutInflater: LayoutInflater? = null

        override fun getCount() = if (mAdapter == null) 0 else Int.MAX_VALUE

        override fun getItemPosition(`object`: Any) = POSITION_NONE

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {}

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            if (mAdapter == null) return 0
            if (mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(container.context)

            val rawPosition = position % mAdapter!!.getCount()
            val view = mLayoutInflater!!.inflate(mAdapter!!.getLayout(), container, false)
            mAdapter!!.convert(view, rawPosition)
            view.setOnClickListener(ClickListener(rawPosition))
            container.addView(view)

            return view
        }

        override fun isViewFromObject(view: View, obj: Any) = view == obj
    }

    private inner class ClickListener(position: Int) : OnClickListener {
        private val mPosition = position
        override fun onClick(v: View?) {
            mOnItemClickListener?.onItemClick(this@BannerView, mPosition)
        }
    }

    interface Adapter {
        fun getCount(): Int
        fun convert(view: View, position: Int)
        fun getLayout(): Int
    }

    interface OnItemSelectedListener {
        fun onItemSelected(view: BannerView, position: Int)
    }

    interface OnItemClickListener {
        fun onItemClick(view: BannerView, position: Int)
    }
}