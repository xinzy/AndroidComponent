package com.xinzy.component.util

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Process
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.os.HandlerCompat
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

fun View.toBitmap(): Bitmap {
    isDrawingCacheEnabled = true
    measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
    layout(0, 0, measuredWidth, measuredHeight)
    buildDrawingCache()
    return drawingCache
}

fun View.touchDelegate(dp: Int) {
    val view = parent
    if (view is View) {
        view.post {
            isEnabled = true
            val bound = Rect()
            getHitRect(bound)

            val px = dp2px(dp)
            bound.left -= px
            bound.top -= px
            bound.right += px
            bound.bottom += px

            val delegate = TouchDelegate(bound, this)
            view.touchDelegate = delegate
        }
    }
}

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
fun Context.isNetworkConnected(): Boolean {
    val manager = getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager
    val networkInfo = manager?.activeNetworkInfo
    return true == networkInfo?.isConnected && networkInfo.state == NetworkInfo.State.CONNECTED
}
fun Context.isWifi(): Boolean {
    val manager = getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager
    val networkInfo = manager?.activeNetworkInfo
    return ConnectivityManager.TYPE_WIFI == networkInfo?.type
}
fun Context.getVersionName(): String {
    return try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        ""
    }
}
fun Context.getVersionCode(): Int {
    return try {
        packageManager.getPackageInfo(packageName, 0).versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        0
    }
}
/**当前进程是否是主进程*/
fun Context.isMainProcess(): Boolean {
    val manager = getSystemService(ACTIVITY_SERVICE) as? ActivityManager ?: return false
    val pid = Process.myPid()
    try {
        val processes = manager.runningAppProcesses ?: return false
        processes.forEach {
            if (it.pid == pid) {
                return it.processName == packageName
            }
        }
    } catch (e: Exception) { }
    return false
}
fun Context.isActivityActive(className: String): Boolean {
    val intent = Intent()
    intent.setClassName(packageName, className)
    val name = intent.resolveActivity(packageManager) ?: return false
    try {
        val manager = getSystemService(ACTIVITY_SERVICE) as? ActivityManager ?: return false
        val tasks = manager.getRunningTasks(100) ?: return false
        tasks.forEach {
            if (it.topActivity == name || it.baseActivity == name) {
                return true
            }
        }
    } catch (e: Exception) { }
    return false
}
/**打开拍照*/
fun Activity.openCamaraForCapture(requestCode: Int, outputUri: Uri? = null) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    outputUri?.let { intent.putExtra(MediaStore.EXTRA_OUTPUT, it) }
    startActivityForResult(intent, requestCode)
}
/**相册选择图片*/
fun Activity.selectImage(requestCode: Int) {
    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    startActivityForResult(intent, requestCode)
}
/**相册选中后的图片*/
fun Activity.getSelectedImage(data: Intent): String? {
    val selectedImageUri = data.data ?: return null

    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
    try {
        val cursor = contentResolver.query(selectedImageUri, filePathColumn, null, null, null) ?: return null
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        val filepath = cursor.getString(columnIndex)
        cursor.close()

        return filepath
    } catch (e: Exception) {
        return null
    }
}

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

fun AbsListView.edge(
        bottomAction: (view: AbsListView) -> Unit = {},
        topAction: (view: AbsListView) -> Unit = {}
) {
    setOnScrollListener(object : AbsListView.OnScrollListener {
        override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}

        override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                if (view.lastVisiblePosition == view.adapter.count - 1) {
                    bottomAction(view)
                } else if (view.firstVisiblePosition == 0) {
                    val first = view.getChildAt(0)
                    if (first?.top == 0) {
                        topAction(view)
                    }
                }
            }
        }
    })
}

fun RecyclerView.edge(
        bottomAction: (view: RecyclerView) -> Unit = {},
        topAction: (view: RecyclerView) -> Unit = {}
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (recyclerView.canScrollVertically(1)) {
                    bottomAction(recyclerView)
                } else if (recyclerView.canScrollVertically(-1)) {
                    topAction(recyclerView)
                }
            }
        }
    })
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

    val (oldLeft, oldTop, oldRight, oldBottom) = bounds

    val bitmap = Bitmap.createBitmap(width, height, config ?: Bitmap.Config.ARGB_8888)
    setBounds(0, 0, width, height)
    draw(Canvas(bitmap))

    setBounds(oldLeft, oldTop, oldRight, oldBottom)
    return bitmap
}

fun Bitmap.saveAsFile(file: File, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 100) {
    try  {
        val stream = FileOutputStream(file)
        compress(format, quality, stream)
        stream.flush()
        stream.close()
    } catch (e: Exception) {}
}

fun Bitmap.saveAsFile(path: String, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 100) {
    saveAsFile(File(path), format, quality)
}

fun Bitmap.zoom(width: Int, height: Int): Bitmap {
    if (width <= 0 || height <= 0) throw IllegalArgumentException("width or height must greater than 0")
    val scaleWidth = width.toFloat() / this.width
    val scaleHeight = height.toFloat() / this.height
    val m = Matrix()
    m.postScale(scaleWidth, scaleHeight)
    return Bitmap.createBitmap(this, 0, 0, width, height, m, true)
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



fun String.sha1() = hash("SHA-1")
fun String.sha256() = hash("SHA-256")
fun String.sha512() = hash("SHA-512")
fun String.md5() = hash("MD5")

internal fun String.hash(type: String) = try {
    MessageDigest.getInstance(type).digest(toByteArray()).joinToString(separator = "") { String.format("%02x", it) }
} catch (e: Exception) {
    ""
}
