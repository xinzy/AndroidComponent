package com.xinzy.component.kotlin.widget

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.*
import android.os.Build
import android.support.v4.view.ViewCompat
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import com.xinzy.component.R
import com.xinzy.component.util.isBottomLeft
import com.xinzy.component.util.isBottomRight
import com.xinzy.component.util.isTopLeft
import com.xinzy.component.util.isTopRight

open class RadiusTextView : AppCompatTextView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init(context, attrs) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init(context, attrs, defStyleAttr) }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RadiusTextView, defStyleAttr, 0)

        val color = parseColor(ta)
        setTextColor(color)

        val drawable = parseBackground(ta)
        val rippleColor = ta.getColor(R.styleable.RadiusTextView_rippleColor, 0)
        val rippleEnabled = ta.getBoolean(R.styleable.RadiusTextView_rippleEnabled, false)
        if (!rippleEnabled || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || rippleColor == 0) {
            ViewCompat.setBackground(this, drawable)
        } else {
            val csl = parseRippleState(ta, rippleColor)
            val contentDrawable = if (drawable is ColorDrawable) null else drawable
            val rippleDrawable = RippleDrawable(csl, contentDrawable, null)
            ViewCompat.setBackground(this, rippleDrawable)
        }

        ta.recycle()
    }

    private fun parseRippleState(ta: TypedArray, defaultColor: Int): ColorStateList {
        return generateColorStateList(ta.getColor(R.styleable.RadiusTextView_pressedSolidColor, 0),
                ta.getColor(R.styleable.RadiusTextView_selectedSolidColor, 0),
                ta.getColor(R.styleable.RadiusTextView_disableSolidColor, 0),
                defaultColor)
    }

    private fun parseColor(ta: TypedArray): ColorStateList {
        return generateColorStateList(ta.getColor(R.styleable.RadiusTextView_pressedTextColor, 0),
                ta.getColor(R.styleable.RadiusTextView_selectedTextColor, 0),
                ta.getColor(R.styleable.RadiusTextView_disableTextColor, 0),
                ta.getColor(R.styleable.RadiusTextView_android_textColor, 0))
    }

    private fun generateColorStateList(pressedColor: Int, selectedColor: Int, disabledColor: Int, defaultColor: Int): ColorStateList {

        val pairs = mutableListOf<Pair<Int, Int>>()

        pairs.add(android.R.attr.state_pressed to pressedColor)
        pairs.add(android.R.attr.state_selected to selectedColor)
        pairs.add(-android.R.attr.state_enabled to disabledColor)
        val states = mutableListOf<IntArray>()
        val colors = mutableListOf<Int>()
        pairs.filter { it.second != 0 }.forEach {
            states.add(intArrayOf(it.first))
            colors.add(it.second)
        }
        states.add(intArrayOf())
        colors.add(defaultColor)
        return ColorStateList(states.toTypedArray(), colors.toTypedArray().toIntArray())
    }

    private fun parseBackground(ta: TypedArray): Drawable {

        val radius = ta.getDimension(R.styleable.RadiusTextView_radius, 0f)
        val direction = ta.getInt(R.styleable.RadiusTextView_direction, 0)

        val radii: FloatArray? = if (direction == 0) null else {
            val tl = if (isTopLeft(direction)) radius else 0f
            val tr = if (isTopRight(direction)) radius else 0f
            val bl = if (isBottomLeft(direction)) radius else 0f
            val br = if (isBottomRight(direction)) radius else 0f
            floatArrayOf(tl, tl, tr, tr, br, br, bl, bl)
        }

        val defaultDrawable = generateDrawable(radius, radii, ta.getDimensionPixelSize(R.styleable.RadiusTextView_borderWidth, 0),
                ta.getColor(R.styleable.RadiusTextView_borderColor, 0), ta.getColor(R.styleable.RadiusTextView_solidColor, 0))
        val pressedDrawable = generateDrawable(radius, radii, ta.getDimensionPixelSize(R.styleable.RadiusTextView_pressedBorderWidth, 0),
                ta.getColor(R.styleable.RadiusTextView_pressedBorderColor, 0), ta.getColor(R.styleable.RadiusTextView_pressedSolidColor, 0))
        val disableDrawable = generateDrawable(radius, radii, ta.getDimensionPixelSize(R.styleable.RadiusTextView_disableBorderWidth, 0),
                ta.getColor(R.styleable.RadiusTextView_disableBorderColor, 0), ta.getColor(R.styleable.RadiusTextView_disableSolidColor, 0))
        val selectedDrawable = generateDrawable(radius, radii, ta.getDimensionPixelSize(R.styleable.RadiusTextView_selectedBorderWidth, 0),
                ta.getColor(R.styleable.RadiusTextView_selectedBorderColor, 0), ta.getColor(R.styleable.RadiusTextView_selectedSolidColor, 0))

        if (pressedDrawable == null && disableDrawable == null && selectedDrawable == null) return defaultDrawable ?: ColorDrawable()

        val drawable = StateListDrawable()
        pressedDrawable?.let { drawable.addState(intArrayOf(android.R.attr.state_pressed), it) }
        disableDrawable?.let { drawable.addState(intArrayOf(-android.R.attr.state_enabled), it) }
        selectedDrawable?.let { drawable.addState(intArrayOf(android.R.attr.state_selected), it) }
        drawable.addState(intArrayOf(), defaultDrawable ?: ColorDrawable())

        return drawable
    }

    private fun generateDrawable(radius: Float, radii: FloatArray?, stokeWidth: Int, stokeColor: Int, solidColor: Int): GradientDrawable? {
        if (stokeWidth == 0 && stokeColor == 0 && solidColor == 0) return null
        val drawable = GradientDrawable()
        if (radii != null && radii.size == 8) drawable.cornerRadii = radii
        else if (radius != 0f) drawable.cornerRadius = radius

        drawable.setStroke(stokeWidth, stokeColor)
        drawable.setColor(solidColor)

        return drawable
    }
}