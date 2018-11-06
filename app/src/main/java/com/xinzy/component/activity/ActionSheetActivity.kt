package com.xinzy.component.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.xinzy.component.R
import com.xinzy.component.kotlin.widget.ActionSheet
import com.xinzy.component.util.toast

class ActionSheetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_sheet)
    }

    fun onShowActionSheet(v: View) {
        ActionSheet.Builder(this).items(arrayOf("米饭", "面条", "回锅肉")).title("晚饭吃什么").listener(object : ActionSheet.ActionSheetListener {
            override fun onItemClick(actionSheet: ActionSheet, index: Int) {
                toast("Click $index")
            }

            override fun onCancel(actionSheet: ActionSheet) {
                toast("Cancel")
            }
        }).show()
    }
}
