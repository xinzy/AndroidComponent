package com.xinzy.component.kotlin.widget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.widget.LinearLayout
import com.xinzy.component.R
import com.xinzy.component.util.isBottomLeft
import com.xinzy.component.util.isBottomRight
import com.xinzy.component.util.isTopLeft
import com.xinzy.component.util.isTopRight

class RadiusLinearLayout : LinearLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RadiusLinearLayout, defStyleAttr, 0)

        val strokeWidth = ta.getDimensionPixelOffset(R.styleable.RadiusLinearLayout_borderWidth, 0)
        val strokeColor = ta.getColor(R.styleable.RadiusLinearLayout_borderColor, 0)
        val color = ta.getColor(R.styleable.RadiusLinearLayout_solidColor, 0)
        val radius = ta.getDimension(R.styleable.RadiusLinearLayout_radius, 0f)
        val direction = ta.getInt(R.styleable.RadiusLinearLayout_direction, 0)

        val radii: FloatArray? = if (direction == 0) null else {
            val tl = if (isTopLeft(direction)) radius else 0f
            val tr = if (isTopRight(direction)) radius else 0f
            val bl = if (isBottomLeft(direction)) radius else 0f
            val br = if (isBottomRight(direction)) radius else 0f
            floatArrayOf(tl, tl, tr, tr, br, br, bl, bl)
        }

        setBackground(color, radius, strokeWidth, strokeColor, radii)

        ta.recycle()
    }

    fun setBackground(color: Int, radius: Float, strokeWidth: Int = 0, strokeColor: Int = 0, radii: FloatArray? = null) {
        val drawable = GradientDrawable()
        if (radii != null && radii.size == 8) drawable.cornerRadii = radii
        else if (radius != 0f) drawable.cornerRadius = radius
        drawable.setColor(color)
        if (strokeWidth != 0 && strokeColor != 0) drawable.setStroke(strokeWidth, strokeColor)

        ViewCompat.setBackground(this, drawable)
    }
}