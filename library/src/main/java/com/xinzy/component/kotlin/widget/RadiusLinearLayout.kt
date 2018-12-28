package com.xinzy.component.kotlin.widget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.widget.LinearLayout
import com.xinzy.component.R

class RadiusLinearLayout : LinearLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RadiusLinearLayout, defStyleAttr, 0)

        val strokeWidth = ta.getDimensionPixelOffset(R.styleable.RadiusLinearLayout_borderWidth, 0)
        val strokeColor = ta.getColor(R.styleable.RadiusLinearLayout_borderColor, 0)
        val color = ta.getColor(R.styleable.RadiusLinearLayout_solidColor, 0)
        val radius = ta.getDimension(R.styleable.RadiusLinearLayout_radius, 0f)

        val radiusLT = ta.getDimension(R.styleable.RadiusLinearLayout_radiusLT, 0f)
        val radiusRT = ta.getDimension(R.styleable.RadiusLinearLayout_radiusRT, 0f)
        val radiusLB = ta.getDimension(R.styleable.RadiusLinearLayout_radiusLB, 0f)
        val radiusRB = ta.getDimension(R.styleable.RadiusLinearLayout_radiusRB, 0f)
        val radii = floatArrayOf(radiusLT, radiusLT, radiusRT, radiusRT, radiusRB, radiusRB, radiusLB, radiusLB)

        setBackground(color, radius, strokeWidth, strokeColor, radii)

        ta.recycle()
    }

    fun setBackground(color: Int, radius: Float, strokeWidth: Int = 0, strokeColor: Int = 0, radii: FloatArray = floatArrayOf()) {
        val drawable = GradientDrawable()
        if (radius == 0f) {
            if (radii.size == 8) drawable.cornerRadii = radii
        } else {
            drawable.cornerRadius = radius
        }
        drawable.setColor(color)
        if (strokeWidth != 0 && strokeColor != 0) drawable.setStroke(strokeWidth, strokeColor)

        ViewCompat.setBackground(this, drawable)
    }
}