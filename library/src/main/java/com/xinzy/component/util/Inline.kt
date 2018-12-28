package com.xinzy.component.util

import android.content.Context
import android.view.View
import android.widget.Toast

fun View.dp2px(dp: Int) = context.dp2px(dp)

fun Context.dp2px(dp: Int) = (resources.displayMetrics.density * dp + .5f).toInt()

fun Context.toast(msgId: Int) = toast(getString(msgId))

fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()