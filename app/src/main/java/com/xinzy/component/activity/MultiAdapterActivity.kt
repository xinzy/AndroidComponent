package com.xinzy.component.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.ImageView
import android.widget.TextView
import com.xinzy.component.R
import com.xinzy.component.java.widget.MultiAdapter
import com.xinzy.component.util.toast
import kotlinx.android.synthetic.main.activity_multi_adapter.*

class MultiAdapterActivity : AppCompatActivity() {

    companion object {
        private const val TYPE_TAG = "tag"
        private const val TYPE_IMAGE = "image"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_adapter)

        val adapter = MultiAdapter()
        adapter.registerProvider(TYPE_TAG, TagProvider()).registerProvider(TYPE_IMAGE, ImageProvider())
        adapter.setOnItemClickListener { _, position -> toast("item click $position") }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val list = listOf(Image(R.drawable.image0), Tag("0"),
                Image(R.drawable.image1), Tag("1"),
                Image(R.drawable.image2), Tag("2"),
                Image(R.drawable.image3), Tag("3"),
                Image(R.drawable.image4), Tag("4"))

        adapter.addAll(list)
    }













    private class ImageProvider : MultiAdapter.ItemProvider<Image>() {
        override fun canClicked() = true

        override fun onBindViewHolder(viewHolder: MultiAdapter.MultiViewHolder, data: Image, position: Int) {
            viewHolder.get<ImageView>(R.id.itemImage).setImageResource(data.resId)
        }

        override fun getItemViewLayoutId() = R.layout.item_img

    }

    private class TagProvider : MultiAdapter.ItemProvider<Tag>() {
        override fun canClicked() = true

        override fun onBindViewHolder(viewHolder: MultiAdapter.MultiViewHolder, data: Tag, position: Int) {
            viewHolder.get<TextView>(R.id.itemText).text = data.text
        }

        override fun getItemViewLayoutId() = R.layout.item_grid
    }

    private class Image(val resId: Int) : MultiAdapter.ViewType {
        override fun getItemType() = TYPE_IMAGE
    }

    private class Tag(val text: String): MultiAdapter.ViewType {
        override fun getItemType() = TYPE_TAG
    }
}
