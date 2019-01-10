package com.xinzy.component.util

internal const val TOP_LEFT = 1
internal const val TOP_RIGHT = 1 shl 1
internal const val BOTTOM_LEFT = 1 shl 2
internal const val BOTTOM_RIGHT = 1 shl 3

internal fun isTopLeft(direction: Int) = TOP_LEFT and direction == TOP_LEFT
internal fun isTopRight(direction: Int) = TOP_RIGHT and direction == TOP_RIGHT
internal fun isBottomLeft(direction: Int) = BOTTOM_LEFT and direction == BOTTOM_LEFT
internal fun isBottomRight(direction: Int) = BOTTOM_RIGHT and direction == BOTTOM_RIGHT

