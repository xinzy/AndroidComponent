package com.xinzy.component.kotlin.widget


import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.TextView
import com.xinzy.component.R

class ConfirmDialog private constructor(context: Context, private val mTitle: String?, private val mMessage: String?,
                                        private val mOk: String?, private val mCancel: String?, private val mCancelable: Boolean,
                                        private val mClickListener: OnButtonClickListener?) : Dialog(context), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_confirm)

        window?.let {
            window.setBackgroundDrawable(ColorDrawable(0))
            val lp = window.attributes
            lp.width = (context.resources.displayMetrics.widthPixels * 0.8f).toInt()
            window.attributes = lp
        }
        setCanceledOnTouchOutside(false)
        if (!mCancelable) {
            setCancelable(false)
        }

        val titleText = findViewById<TextView>(R.id.dialogTitle)
        val contentText = findViewById<TextView>(R.id.dialogContent)
        val okText = findViewById<TextView>(R.id.dialogOk)
        val cancelText = findViewById<TextView>(R.id.dialogCancel)

        if (!TextUtils.isEmpty(mTitle)) {
            titleText.text = mTitle
        } else {
            titleText.visibility = View.GONE
        }
        contentText.text = mMessage
        okText.text = mOk
        if (TextUtils.isEmpty(mCancel)) {
            cancelText.visibility = View.GONE
            findViewById<View>(R.id.dialogDivide).visibility = View.GONE
        } else {
            cancelText.text = mCancel
        }

        okText.setOnClickListener(this)
        cancelText.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.dialogOk) {
            dismiss()
            mClickListener?.onClick(this, BUTTON_OK)
        } else if (id == R.id.dialogCancel) {
            dismiss()
            mClickListener?.onClick(this, BUTTON_CANCEL)
        }
    }

    interface OnButtonClickListener {
        fun onClick(dialog: ConfirmDialog, which: Int)
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

    class Builder(private val mContext: Context) {

        private var title: String? = null
        private var message: String? = null
        private var cancel: String? = null
        private var ok: String? = null
        private var cancelable = true
        private var listener: OnButtonClickListener? = null

        fun title(title: String): Builder {
            this.title = title
            return this
        }

        fun message(message: String): Builder {
            this.message = message
            return this
        }

        fun cancel(cancel: String): Builder {
            this.cancel = cancel
            return this
        }

        fun ok(ok: String): Builder {
            this.ok = ok
            return this
        }

        fun cancelable(b: Boolean): Builder {
            this.cancelable = b
            return this
        }

        fun listener(listener: OnButtonClickListener?): Builder {
            this.listener = listener
            return this
        }

        fun create(): ConfirmDialog {
            if (TextUtils.isEmpty(title)) {
                title = ""
            }
            if (TextUtils.isEmpty(message)) {
                message = ""
            }
            if (TextUtils.isEmpty(ok)) {
                ok = "确定"
            }

            return ConfirmDialog(mContext, title, message, ok, cancel, cancelable, listener)
        }

        fun show() {
            create().show()
        }
    }

    companion object {
        const val BUTTON_OK = 1
        const val BUTTON_CANCEL = 0
    }
}