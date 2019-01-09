package com.xinzy.component.kotlin.widget

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.xinzy.component.R
import kotlinx.android.synthetic.main.c_dialog_progress.*

class ProgressDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.c_dialog_progress)

        this.window?.setBackgroundDrawable(ColorDrawable(0))
        setCanceledOnTouchOutside(false)
    }

    override fun onStart() {
        super.onStart()
        val animation = RotateAnimation(0f, -360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        animation.duration = 1000
        animation.repeatCount = Integer.MAX_VALUE
        progressImage.startAnimation(animation)
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
}