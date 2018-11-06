package com.xinzy.component.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.xinzy.component.R
import com.xinzy.component.kotlin.widget.TabRadioGroup
import kotlinx.android.synthetic.main.activity_tab.*

class TabActivity : AppCompatActivity(), TabRadioGroup.OnCheckedChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        tabGroup.setOnCheckedChangeListener(this)
    }

    fun onAutoFitTrue(v: View) {
        tabBtn1.setAutoFit(true)
        tabBtn2.setAutoFit(true)
    }

    fun onAutoFitFalse(v: View) {
        tabBtn1.setAutoFit(false)
        tabBtn2.setAutoFit(false)
    }

    override fun onCheckedChange(group: TabRadioGroup, checkedId: Int) {
        when (checkedId) {
            R.id.tabBtn1 -> flipper.displayedChild = 0
            R.id.tabBtn2 -> flipper.displayedChild = 1
        }
    }
}
