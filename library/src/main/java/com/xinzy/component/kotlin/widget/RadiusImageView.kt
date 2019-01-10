package com.xinzy.component.kotlin.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import com.xinzy.component.R
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Build
import com.xinzy.component.util.isBottomLeft
import com.xinzy.component.util.isBottomRight
import com.xinzy.component.util.isTopLeft
import com.xinzy.component.util.isTopRight


class RadiusImageView : AppCompatImageView {

    private var mCornerRadius = 0f  // 圆角半径
    private var mIsCircle = false   // 是否是圆
    private var mDirection = 0      // 圆角位置

    private var mBorderWidth = 0f   // 边框宽度
    private var mBorderColor = 0    // 边框颜色

    private val mBitmapPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBorderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mWidth = 0
    private var mHeight = 0

    private val mBorderRect = RectF()   // 边框rect
    private val mSrcRect = RectF()  // 图片rect

    private val mBorderRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private val mSrcRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    private val mXfermode: Xfermode

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {


        val ta = context.obtainStyledAttributes(attrs, R.styleable.RadiusImageView, defStyleAttr, 0)

        mBorderWidth = ta.getDimension(R.styleable.RadiusImageView_borderWidth, 5f)
        mBorderColor = ta.getColor(R.styleable.RadiusImageView_borderColor, Color.RED)
        mCornerRadius = ta.getDimension(R.styleable.RadiusImageView_radius, 48f)
        mDirection = ta.getInt(R.styleable.RadiusImageView_direction, 0)
        mIsCircle = ta.getBoolean(R.styleable.RadiusImageView_isCircle, false)

        ta.recycle()

        mBorderPaint.style = Paint.Style.STROKE
        mBitmapPaint.color = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            mXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        } else {
            mXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        }

        calculateRadii()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mIsCircle) {
            val size = Math.min(measuredWidth, measuredHeight)
            setMeasuredDimension(size, size)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h

        calculateBorderRect()
        calculateSrcRect()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

    }

    private fun calculateBorderRect() {
//        if (!mIsCircle) { }
        mBorderRect.set(mBorderWidth / 2.0f, mBorderWidth / 2.0f, mWidth - mBorderWidth / 2.0f, mHeight - mBorderWidth / 2.0f)
    }

    private fun calculateSrcRect() {
        mSrcRect.set(0f, 0f, mWidth.toFloat(), mHeight.toFloat())
    }

    private fun calculateRadii() {
        if (mIsCircle || mCornerRadius == 0f) return

        val srcRadius = mCornerRadius - mBorderWidth / 2f
        if (mDirection == 0) {
            (0 until 8).forEach {
                mBorderRadii[it] = mCornerRadius
                mSrcRadii[it] = srcRadius
            }
        } else {
            if (isTopLeft(mDirection)) {
                mBorderRadii[0] = mCornerRadius
                mBorderRadii[1] = mCornerRadius
                mSrcRadii[0] = srcRadius
                mSrcRadii[1] = srcRadius
            }
            if (isTopRight(mDirection)) {
                mBorderRadii[2] = mCornerRadius
                mBorderRadii[3] = mCornerRadius
                mSrcRadii[2] = srcRadius
                mSrcRadii[3] = srcRadius
            }
            if (isBottomRight(mDirection)) {
                mBorderRadii[4] = mCornerRadius
                mBorderRadii[5] = mCornerRadius
                mSrcRadii[4] = srcRadius
                mSrcRadii[5] = srcRadius
            }
            if (isBottomLeft(mDirection)) {
                mBorderRadii[6] = mCornerRadius
                mBorderRadii[7] = mCornerRadius
                mSrcRadii[6] = srcRadius
                mSrcRadii[7] = srcRadius
            }
        }
    }

    private fun getBitmap(): Bitmap? {
        val d = drawable ?: return null
        if (d is BitmapDrawable) {
            val bitmap = d.bitmap ?: return null

            val bmWidth = bitmap.width
            val bmHeight = bitmap.height
            if (bmWidth == 0 || bmHeight == 0) {
                return null
            }
            val minScaleX = minimumWidth / bmWidth.toFloat()
            val minScaleY = minimumHeight / bmHeight.toFloat()
            return if (minScaleX > 1 || minScaleY > 1) {
                val scale = Math.max(minScaleX, minScaleY)
                val matrix = Matrix()
                matrix.postScale(scale, scale)

                Bitmap.createBitmap(bitmap, 0, 0, bmWidth, bmHeight, matrix, false)
            } else {
                bitmap
            }
        }

        return try {
            val bitmap = if (d is ColorDrawable) Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888) else Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            d.setBounds(0, 0, canvas.width, canvas.height)
            d.draw(canvas)
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}