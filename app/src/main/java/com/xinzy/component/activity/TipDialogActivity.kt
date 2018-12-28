package com.xinzy.component.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.xinzy.component.R
import com.xinzy.component.kotlin.widget.TipsDialog

class TipDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tip_dialog)
    }

    fun onProgress(view: View) {
        TipsDialog.Builder().content("加载中...").iconType(TipsDialog.ICON_TYPE_LOADING).create(this).show()
    }

    fun onSuccess(view: View) {
        TipsDialog.Builder().content("操作成功").iconType(TipsDialog.ICON_TYPE_SUCCESS).create(this).show()
    }

    fun onInfo(view: View) {
        TipsDialog.Builder().content("提示").iconType(TipsDialog.ICON_TYPE_INFO).autoDismiss(1000).create(this).show()
    }

    fun onError(view: View) {
        TipsDialog.Builder().content("错误").iconType(TipsDialog.ICON_TYPE_FAIL).autoDismiss(1000).create(this).show()
    }

    fun onTip(view: View) {
        TipsDialog.Builder().content("这里是提示文字").iconType(TipsDialog.ICON_TYPE_NOTHING).autoDismiss(1000).create(this).show()
    }
}
