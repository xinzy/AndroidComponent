package com.xinzy.component.kotlin.widget.util

import android.util.Log

internal const val TOP_LEFT = 1
internal const val TOP_RIGHT = 2
internal const val BOTTOM_LEFT = 4
internal const val BOTTOM_RIGHT = 8

internal fun isTopLeft(direction: Int) = TOP_LEFT and direction == TOP_LEFT
internal fun isTopRight(direction: Int) = TOP_RIGHT and direction == TOP_RIGHT
internal fun isBottomLeft(direction: Int) = BOTTOM_LEFT and direction == BOTTOM_LEFT
internal fun isBottomRight(direction: Int) = BOTTOM_RIGHT and direction == BOTTOM_RIGHT





internal const val TAG = "COMPONENT"

internal fun log(msg: String, e: Exception? = null) {
    if (e != null) {
        Log.e(TAG, msg, e)
    } else {
        Log.d(TAG, msg)
    }
}