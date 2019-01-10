package com.xinzy.component.kotlin.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.annotation.StringRes
import android.widget.LinearLayout
import com.xinzy.component.R

class CDialog : Dialog {
    constructor(context: Context) : this(context, R.style.Component_CDialog)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

}

abstract class CDialogBuild<out T : CDialogBuild<T>>(protected val mContext: Context) {

    protected var mTitle: CharSequence? = null
    protected var mCancelable = true
    protected var mCanceledOnTouchOutside = true

    protected lateinit var mRootView: LinearLayout
    protected lateinit var mDialog: CDialog


    fun setTitle(title: CharSequence): T {
        mTitle = title
        return this as T
    }

    fun setTitle(@StringRes resid: Int): T {
        return setTitle(mContext.getString(resid))
    }

}


