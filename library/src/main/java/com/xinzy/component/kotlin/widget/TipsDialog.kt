package com.xinzy.component.kotlin.widget

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.support.annotation.IntDef
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.xinzy.component.R
import com.xinzy.component.util.dp2px

class TipsDialog private constructor(context: Context, private val mContent: String, private val mIconType: Int,
                                     private val mCancelable: Boolean, private val mDuration: Long,
                                     private val mBackgroundColor: Int, private val mBackgroundRadius: Int,
                                     private val mTextColor: Int, private val mTextSize: Float)
    : Dialog(context, R.style.Component_TipDialog) {

    companion object {
        /**
         * 不显示任何icon
         */
        const val ICON_TYPE_NOTHING = 0
        /**
         * 显示 Loading 图标
         */
        const val ICON_TYPE_LOADING = 1
        /**
         * 显示成功图标
         */
        const val ICON_TYPE_SUCCESS = 2
        /**
         * 显示失败图标
         */
        const val ICON_TYPE_FAIL = 3
        /**
         * 显示信息图标
         */
        const val ICON_TYPE_INFO = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        this.window?.let {
            it.setBackgroundDrawable(ColorDrawable(0))
        }
        setCancelable(mCancelable)
        setCanceledOnTouchOutside(mCancelable)


        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        setContentView(createView(), lp)
    }

    private fun createView(): View {
        val container = RadiusLinearLayout(context)
        container.minimumWidth = context.dp2px(128)
        container.minimumHeight = context.dp2px(56)
        container.gravity = Gravity.CENTER
        val paddingH = context.dp2px(16)
        val paddingV = context.dp2px(12)
        container.setPadding(paddingH, paddingV, paddingH, paddingV)
        container.setBackground(mBackgroundColor, context.dp2px(mBackgroundRadius).toFloat())
        container.orientation = LinearLayout.VERTICAL

        val size = context.dp2px(32)
        when (mIconType) {
            ICON_TYPE_LOADING -> {
                val loadingView = LoadingView(context, size)
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                container.addView(loadingView, lp)
            }
            ICON_TYPE_FAIL, ICON_TYPE_INFO, ICON_TYPE_SUCCESS -> {
                val resId = if (mIconType == ICON_TYPE_FAIL) R.drawable.icon_notify_error else
                    ( if (mIconType == ICON_TYPE_INFO) R.drawable.icon_notify_error else R.drawable.icon_notify_success )
                val imageView = ImageView(context)
                imageView.setImageResource(resId)
                imageView.setBackgroundColor(0)
                val lp = LinearLayout.LayoutParams(size, size)
                container.addView(imageView, lp)
            }
        }

        if (!TextUtils.isEmpty(mContent)) {
            val textView = TextView(context)
            textView.setTextColor(mTextColor)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            textView.text = mContent
            textView.ellipsize = TextUtils.TruncateAt.END
            textView.maxLines = 2
            textView.gravity = Gravity.CENTER

            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            if (mIconType != ICON_TYPE_NOTHING) {
                lp.topMargin = context.dp2px(12)
            }
            container.addView(textView, lp)
        }

        return container
    }

    override fun show() {
        try {
            super.show()
            autoClose()
        } catch (e: Exception) { }
    }

    override fun dismiss() {
        try {
            super.dismiss()
        } catch (e: Exception) { }
    }

    private fun autoClose() {
        if (mDuration > 0) {
            Handler().postDelayed({ if (isShowing) dismiss() }, mDuration)
        }
    }

    class Builder {
        @IconType
        private var mIconType = ICON_TYPE_NOTHING
        private var mCancelable = true
        private var mDuration = 0L

        private var mText = ""
        private var mTextSize = 14f
        private var mTextColor = 0xFFFFFFFF.toInt()

        private var mBackgroundColor = 0xC0000000.toInt()
        private var mBackgroundRadius = 12

        fun content(content: String): Builder {
            mText = content
            return this
        }

        fun textSize(sp: Float): Builder {
            mTextSize = sp
            return this
        }

        fun textColor(color: Int): Builder {
            mTextColor = color
            return this
        }

        fun iconType(@IconType type: Int): Builder {
            mIconType = type
            return this
        }

        fun cancelable(cancelable: Boolean): Builder {
            mCancelable = cancelable
            return this
        }

        fun autoDismiss(duration: Long): Builder {
            mDuration = duration
            return this
        }

        fun backgroundColor(color: Int): Builder {
            mBackgroundColor = color
            return this
        }

        fun backgroundRadius(radius: Int): Builder {
            mBackgroundRadius = radius
            return this
        }

        fun create(context: Context): TipsDialog {
            return TipsDialog(context, mText, mIconType, mCancelable, mDuration, mBackgroundColor,
                    mBackgroundRadius, mTextColor, mTextSize)
        }
    }

    @IntDef ( value = [ICON_TYPE_FAIL, ICON_TYPE_INFO, ICON_TYPE_LOADING, ICON_TYPE_NOTHING, ICON_TYPE_SUCCESS] )
    @Retention(AnnotationRetention.SOURCE)
    @Target ( AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER,
            AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.TYPE_PARAMETER )
    annotation class IconType
}