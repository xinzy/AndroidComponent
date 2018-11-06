package com.xinzy.component.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.xinzy.component.R
import com.xinzy.component.kotlin.widget.BannerView
import kotlinx.android.synthetic.main.activity_banner_view.*

class BannerViewActivity : AppCompatActivity(), BannerView.Adapter {

    private val images = arrayOf(R.drawable.image0, R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_view)

        bannerView.setAdapter(this)
    }

    override fun getCount() = images.size

    override fun convert(view: View, position: Int) {
        view.findViewById<ImageView>(R.id.itemImage).setImageResource(images[position])
    }

    override fun getLayout() = R.layout.item_img
}
