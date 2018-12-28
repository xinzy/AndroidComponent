package com.xinzy.component.kotlin.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity

open class RadiusButton : RadiusTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        gravity = Gravity.CENTER
        isClickable = true
    }

}