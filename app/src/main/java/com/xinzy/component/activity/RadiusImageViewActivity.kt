package com.xinzy.component.activity

import android.graphics.*
import android.os.Bundle
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.app.AppCompatActivity
import com.xinzy.component.R
import kotlinx.android.synthetic.main.activity_radius_image_view.*

class RadiusImageViewActivity : AppCompatActivity() {

    val corner = 50f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radius_image_view)
    }

    override fun onResume() {
        super.onResume()

        image.post { setBitmap1() }
    }

    private fun setBitmap1() {
        val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.image1)
        val drawable = RoundedBitmapDrawableFactory.create(resources, srcBitmap)
        drawable.cornerRadius = corner
//        drawable.isCircular = true

        image.setImageDrawable(drawable)

    }

    private fun setBitmap() {
        val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.image1)
        val width = image.measuredWidth
        val height = image.measuredHeight

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val radii = floatArrayOf(corner, corner, corner, corner, corner, corner, corner, corner)
        val path = Path()
        path.addRoundRect(rect, radii, Path.Direction.CW)

        canvas.drawPath(path, paint)
        image.setImageBitmap(bitmap)
    }
}
