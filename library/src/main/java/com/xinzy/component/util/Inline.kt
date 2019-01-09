package com.xinzy.component.util

import android.animation.Animator
import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.os.HandlerCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.io.File

fun View.snack(msg: String, actionText: String? = null, duration: Int = Snackbar.LENGTH_LONG, actionTextColor: Int? = null, action: () -> Unit = {}) {
    val bar = Snackbar.make(this, msg, duration)
    actionText?.let {
        bar.setAction(it) { action() }

        actionTextColor?.let { color -> bar.setActionTextColor(color) }
    }

    bar.show()
}

fun View.setPadding(px: Int) {
    setPadding(px, px, px, px)
}

fun View.setPadding(h: Int, v: Int) {
    setPadding(h, v, h, v)
}

fun View.postDelayed(delay: Long, action: () -> Unit) {
    val runnable = Runnable { action() }
    postDelayed(runnable, delay)
}

inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

inline var View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.INVISIBLE
    }

inline var View.isGone: Boolean
    get() = visibility == View.GONE
    set(value) {
        visibility = if (value) View.GONE else View.VISIBLE
    }

fun View.dp2px(dp: Int) = context.dp2px(dp)

fun Context.dp2px(dp: Int) = (resources.displayMetrics.density * dp + .5f).toInt()

fun Context.toast(msgId: Int) = toast(getString(msgId))

fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

operator fun ViewGroup.contains(child: View) = indexOfChild(child) != -1

operator fun ViewGroup.get(index: Int) = getChildAt(index) ?: throw IndexOutOfBoundsException("index=$index, childrenCount=$childCount")

operator fun ViewGroup.plusAssign(child: View) = addView(child)

operator fun ViewGroup.minusAssign(child: View) = removeView(child)

fun ViewGroup.isEmpty() = childCount == 0

fun ViewGroup.isNotEmpty() = !isEmpty()

fun ViewGroup.forEach(action: (child: View) -> Unit) {
    for (index in 0 .. childCount) {
        action(getChildAt(index))
    }
}

fun ViewGroup.forEachIndexed(action: (index: Int, child: View) -> Unit) {
    for (index in 0 .. childCount) {
        action(index, getChildAt(index))
    }
}

fun ViewGroup.iterator() = object : MutableIterator<View> {
    private var index = 0
    override fun hasNext() = index < childCount
    override fun next() = get(index++)
    override fun remove() = removeViewAt(--index)
}

fun SharedPreferences.edit(commit: Boolean = false, action: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    action(editor)

    if (commit) {
        editor.commit()
    } else {
        editor.apply()
    }
}

fun String.toUri() = Uri.parse(this)

fun File.toUri() = Uri.fromFile(this)

fun Uri.toFile(): File {
    require(scheme == "file") { "Uri lacks 'file' scheme: $this" }
    return File(path)
}

fun Animator.doOnStart(action: (animation: Animator) -> Unit) = addListener(animStart = action)

fun Animator.doOnEnd(action: (animation: Animator) -> Unit) = addListener(animEnd = action)

fun Animator.doOnCancel(action: (animation: Animator) -> Unit) = addListener(animCancel = action)

fun Animator.doOnRepeat(action: (animation: Animator) -> Unit) = addListener(animRepeat = action)

fun Animator.addListener(
        animStart: (animation: Animator) -> Unit = {},
        animEnd: (animation: Animator) -> Unit = {},
        animRepeat: (animation: Animator) -> Unit = {},
        animCancel: (animation: Animator) -> Unit = {}
): Animator.AnimatorListener {
    val l = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator) = animRepeat(animation)
        override fun onAnimationEnd(animation: Animator) = animEnd(animation)
        override fun onAnimationStart(animation: Animator) = animStart(animation)
        override fun onAnimationCancel(animation: Animator) = animCancel(animation)
    }
    addListener(l)
    return l
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun Animator.doOnResume(action: (animation: Animator) -> Unit) = addPauseListener(animResume = action)

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun Animator.doOnPause(action: (animation: Animator) -> Unit) = addPauseListener(animPause = action)

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun Animator.addPauseListener(
        animPause: (animation: Animator) -> Unit = {},
        animResume: (animation: Animator) -> Unit = {}
): Animator.AnimatorPauseListener {
    val l = @RequiresApi(Build.VERSION_CODES.KITKAT)
    object : Animator.AnimatorPauseListener {
        override fun onAnimationPause(animation: Animator) = animPause(animation)
        override fun onAnimationResume(animation: Animator) = animResume(animation)
    }

    addPauseListener(l)
    return l
}

fun TextView.doBeforeTextChanged(action: (s: CharSequence?, start: Int, count: Int, after: Int) -> Unit) = addTextChangedListener(beforeTextChanged = action)
fun TextView.doAfterTextChanged(action: (s: Editable?) -> Unit) = addTextChangedListener(afterTextChanged = action)
fun TextView.doOnTextChanged(action: (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit) = addTextChangedListener(onTextChanged = action)

fun TextView.addTextChangedListener(
        beforeTextChanged: (s: CharSequence?, start: Int, count: Int, after: Int) -> Unit = { _, _, _, _ ->  },
        afterTextChanged: (s: Editable?) -> Unit = {  },
        onTextChanged: (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit = { _, _, _,_ ->  }
): TextWatcher {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = afterTextChanged(s)
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = beforeTextChanged(s, start, count, after)
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = onTextChanged(s, start, before, count)
    }
    addTextChangedListener(watcher)

    return watcher
}

fun Drawable.toBitmap(width: Int = intrinsicWidth, height: Int = intrinsicHeight, config: Bitmap.Config? = Bitmap.Config.ARGB_8888): Bitmap {
    if (this is BitmapDrawable) {
        if (config == null || bitmap.config == config) {
            if (width == intrinsicWidth && height == intrinsicHeight) {
                return bitmap
            }
            return Bitmap.createScaledBitmap(bitmap, width, height, true)
        }
    }
    val oldLeft = bounds.left
    val oldTop = bounds.top
    val oldRight = bounds.right
    val oldBottom = bounds.bottom

    val bitmap = Bitmap.createBitmap(width, height, config ?: Bitmap.Config.ARGB_8888)
    setBounds(0, 0, width, height)
    draw(Canvas(bitmap))

    setBounds(oldLeft, oldTop, oldRight, oldBottom)
    return bitmap
}

operator fun Rect.component1() = left
operator fun Rect.component2() = top
operator fun Rect.component3() = right
operator fun Rect.component4() = bottom
operator fun Rect.plus(r: Rect) = Rect(this).apply { union(r) }
operator fun Rect.plus(xy: Int) = Rect(this).apply { offset(xy, xy) }
operator fun Rect.plus(p: Point) = Rect(this).apply { offset(p.x, p.y) }
operator fun Rect.minus(xy: Int) = Rect(this).apply { offset(-xy, -xy) }
fun Rect.toRectF() = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())

operator fun RectF.component1() = left
operator fun RectF.component2() = top
operator fun RectF.component3() = right
operator fun RectF.component4() = bottom
operator fun RectF.plus(r: RectF) = RectF(this).apply { union(r) }
operator fun RectF.plus(xy: Float) = RectF(this).apply { offset(xy, xy) }
operator fun RectF.plus(p: PointF) = RectF(this).apply { offset(p.x, p.y) }
operator fun RectF.minus(xy: Float) = RectF(this).apply { offset(-xy, -xy) }
fun RectF.toRect() = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())

operator fun Point.component1() = x
operator fun Point.component2() = y
operator fun Point.unaryMinus() = Point(-x, -y)
operator fun Point.plus(p: Point) = Point(x, y).apply { offset(p.x, p.y) }
operator fun Point.plus(xy: Int) = Point(x, y).apply { offset(xy, xy) }
operator fun Point.minus(p: Point) = Point(x, y).apply { offset(-p.x, -p.y) }
operator fun Point.minus(xy: Int) = Point(x, y).apply { offset(-xy, -xy) }

operator fun PointF.component1() = x
operator fun PointF.component2() = y
operator fun PointF.unaryMinus() = PointF(-x, -y)
operator fun PointF.plus(p: PointF) = PointF(x, y).apply { offset(p.x, p.y) }
operator fun PointF.plus(xy: Float) = PointF(x, y).apply { offset(xy, xy) }
operator fun PointF.minus(p: PointF) = PointF(x, y).apply { offset(-p.x, -p.y) }
operator fun PointF.minus(xy: Float) = PointF(x, y).apply { offset(-xy, -xy) }


fun Handler.postDelayed(delay: Long, token: Any? = null, action: () -> Unit): Runnable {
    val runnable = Runnable { action() }
    if (token == null) {
        postDelayed(runnable, delay)
    } else {
        HandlerCompat.postDelayed(this, runnable, token, delay)
    }

    return runnable
}