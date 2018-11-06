package com.xinzy.component.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.xinzy.component.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val count = container.childCount

        (0 until count).map { container.getChildAt(it) }.forEach { view ->
            view.setOnClickListener {
                val tag = it.tag as String
                if (!TextUtils.isEmpty(tag)) {
                    val intent = Intent()
                    intent.setClassName(packageName, "com.xinzy.component.activity.${tag}Activity")
                    startActivity(intent)
                }
            }
        }
    }


}
