package com.xinzy.component.kotlin.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

class TabRadioGroup : LinearLayout, View.OnClickListener {

    private var mCheckedId = -1

    private var mChangedChangeListener: OnCheckedChangeListener? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        orientation = LinearLayout.HORIZONTAL
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        child?.let { setViewState(it) }
        super.addView(child, index, params)
    }

    private fun setViewState(child: View) {
        if (child is TabRadioButton) {
            if (child.isChecked) {
                if (mCheckedId != -1) {
                    setCheckedStateForView(mCheckedId, false)
                }
                setCheckedId(child.id)
            }
            child.setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            is TabRadioButton -> {
                if (!v.isChecked) {
                    setCheckedId(v.id)
                }
            }
        }
    }

    private fun setCheckedStateForView(viewId: Int, checked: Boolean) {
        val checkedView = findViewById<View>(viewId)
        if (checkedView != null && checkedView is TabRadioButton) {
            checkedView.isChecked = checked
        }
    }

    fun setCheckedId(id: Int) {
        mCheckedId = id
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            if (child is TabRadioButton) {
                child.isChecked = child.id == id
            }
        }

        mChangedChangeListener?.onCheckedChange(this, id)
    }

    fun setOnCheckedChangeListener(l: OnCheckedChangeListener?) {
        mChangedChangeListener = l
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(group: TabRadioGroup, checkedId: Int)
    }
}