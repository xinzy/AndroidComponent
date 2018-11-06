package com.xinzy.component.kotlin.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.SparseIntArray
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.xinzy.component.R

/**
 * Created by yang.yang on 2018/8/3.
 */

class ActionSheet private constructor(context: Context) : Dialog(context, R.style.Theme_Light_NoTitle_Dialog), View.OnClickListener {

    private lateinit var mContainer: LinearLayout

    private val mIdIndex = SparseIntArray()

    private var mItems: Array<String>? = null
    private var mTitle: String? = null
    private var mCancelText = "取消"

    private var mListener: ActionSheetListener? = null

    companion object {
        private val TITLE_ID = View.generateViewId()
        private val CANCEL_ID = View.generateViewId()

        private const val ITEM_HEIGHT = 40

        private const val TITLE_TEXT_SIZE = 14f
        private const val ITEM_TEXT_SIZE = 14f
        private const val CANCEL_TEXT_SIZE = 14f

        private const val TITLE_TEXT_COLOR = 0xFF249DEA.toInt()
        private const val ITEM_TEXT_COLOR = 0xFF249DEA.toInt()
        private const val CANCEL_TEXT_COLOR = 0xFF999999.toInt()

        private const val DIVIDE_HEIGHT = 1
        private const val DIVIDE_COLOR = 0xFFF0F0F0.toInt()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.decorView.setPadding(0, 0, 0, 0)
        window.setBackgroundDrawable(ColorDrawable(0))
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
        window.setGravity(Gravity.BOTTOM)
        window.setWindowAnimations(R.style.Animation_Bottom)

        setCanceledOnTouchOutside(true)

        mContainer = LinearLayout(context)
        mContainer.orientation = LinearLayout.VERTICAL
        mContainer.setBackgroundColor(Color.WHITE)

        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.BOTTOM
        mContainer.layoutParams = params

        setContentView(mContainer)
        createItems()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            CANCEL_ID -> {
                dismiss()
                mListener?.onCancel(this)
            }
            else -> {
                val index = mIdIndex.get(v!!.id, -1)
                if (index >= 0) {
                    dismiss()
                    mListener?.onItemClick(this, index)
                }
            }
        }
    }

    override fun show() {
        try {
            super.show()
        } catch (e: Exception) {
        }
    }

    override fun dismiss() {
        try {
            super.dismiss()
        } catch (e: Exception) {
        }
    }

    private fun createItems() {
        if (!TextUtils.isEmpty(mTitle)) {
            val titleText = TextView(context)
            titleText.id = TITLE_ID
            titleText.text = mTitle
            titleText.gravity = Gravity.CENTER
            titleText.setTextColor(TITLE_TEXT_COLOR)
            titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, TITLE_TEXT_SIZE)

            mContainer.addView(titleText, itemLayoutParam(ITEM_HEIGHT))

            addDivide()
        }

        mIdIndex.clear()
        if (mItems != null && mItems!!.isNotEmpty()) {
            mItems!!.forEachIndexed{index, item ->
                run {
                    val id = View.generateViewId()
                    mIdIndex.put(id, index)

                    val text = TextView(context)
                    text.id = id
                    text.text = item
                    text.gravity = Gravity.CENTER
                    text.setTextColor(ITEM_TEXT_COLOR)
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, ITEM_TEXT_SIZE)
                    text.setOnClickListener(this)

                    mContainer.addView(text, itemLayoutParam(ITEM_HEIGHT))
                    addDivide()
                }
            }
        }

        val cancelText = TextView(context)
        cancelText.id = CANCEL_ID
        cancelText.text = mCancelText
        cancelText.gravity = Gravity.CENTER
        cancelText.setTextColor(CANCEL_TEXT_COLOR)
        cancelText.setTextSize(TypedValue.COMPLEX_UNIT_SP, CANCEL_TEXT_SIZE)
        cancelText.setOnClickListener(this)

        mContainer.addView(cancelText, itemLayoutParam(ITEM_HEIGHT))
    }

    private fun addDivide() {
        val divide = View(context)
        divide.setBackgroundColor(DIVIDE_COLOR)
        mContainer.addView(divide, itemLayoutParam(DIVIDE_HEIGHT))
    }

    private fun itemLayoutParam(height: Int) = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(height))

    private fun dp2px(dp: Int) = (context.resources.displayMetrics.density * dp + .5f).toInt()

    interface ActionSheetListener {
        fun onItemClick(actionSheet: ActionSheet, index: Int)
        fun onCancel(actionSheet: ActionSheet)
    }

    class Builder constructor(private val context: Context) {
        private var title: String? = null
        private var items: Array<String>? = null
        private var listener: ActionSheetListener? = null

        fun title(title: String): Builder {
            this.title = title
            return this
        }

        fun items(items: Array<String>): Builder {
            this.items = items
            return this
        }

        fun listener(listener: ActionSheetListener): Builder {
            this.listener = listener
            return this
        }

        fun show() {
            val sheet = ActionSheet(context)
            sheet.mItems = items
            sheet.mTitle = title
            sheet.mListener = listener
            sheet.show()
        }
    }
}