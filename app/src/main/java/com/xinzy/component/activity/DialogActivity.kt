package com.xinzy.component.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.xinzy.component.R
import com.xinzy.component.kotlin.widget.ConfirmDialog
import com.xinzy.component.kotlin.widget.ProgressDialog
import com.xinzy.component.util.toast

class DialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
    }

    fun onProgress(v: View) {
        ProgressDialog(this).show()
    }

    fun onSingleConfirm(v: View) {
        ConfirmDialog.Builder(this).cancelable(false).title("晚上吃什么").message("吃面").ok("好的")
                .listener(object : ConfirmDialog.OnButtonClickListener {
                    override fun onClick(dialog: ConfirmDialog, which: Int) {
                        toast("ok")
                    }
                }).show()
    }

    fun onDoubleConfirm(v: View) {
        ConfirmDialog.Builder(this).cancelable(false).title("晚上吃什么").message("吃面").ok("好的").cancel("算了")
                .listener(object : ConfirmDialog.OnButtonClickListener {
                    override fun onClick(dialog: ConfirmDialog, which: Int) {
                        if (which == ConfirmDialog.BUTTON_OK) {
                            toast("ok")
                        } else {
                            toast("cancel")
                        }
                    }
                }).show()
    }
}
