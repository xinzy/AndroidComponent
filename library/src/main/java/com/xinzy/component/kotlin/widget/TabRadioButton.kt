package com.xinzy.component.kotlin.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.RelativeLayout
import com.xinzy.component.R
import kotlinx.android.synthetic.main.c_view_tab_radio_button.view.*

class TabRadioButton : RelativeLayout, Checkable {
    private var mChecked = false

    private var mAutoFit = true

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        LayoutInflater.from(context).inflate(R.layout.c_view_tab_radio_button, this)

        val ta = context!!.obtainStyledAttributes(attrs, R.styleable.TabRadioButton)

        val isChecked = ta.getBoolean(R.styleable.TabRadioButton_checked, false)
        setChecked(isChecked)

        val text = ta.getString(R.styleable.TabRadioButton_text)
        setText(text)

        val autoFit = ta.getBoolean(R.styleable.TabRadioButton_autoFit, true)
        setAutoFit(autoFit)

        ta.recycle()
    }

    fun setText(text: String?) {
        tabViewText.text = text
        fit()
    }

    fun setAutoFit(b: Boolean) {
        mAutoFit = b
        fit()
    }

    private fun fit() {
        val width = if (mAutoFit) tabViewText.paint.measureText(tabViewText.text.toString()).toInt() else ViewGroup.LayoutParams.MATCH_PARENT
        val lp = tabViewIndication.layoutParams
        lp.width = width
        tabViewIndication.layoutParams = lp
    }

    override fun isChecked() = mChecked

    override fun toggle() {
        mChecked = !mChecked
    }

    override fun setChecked(checked: Boolean) {
        mChecked = checked
        tabViewText.isSelected = checked
        tabViewIndication.isSelected = checked
    }
}