package com.xinzy.component

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.widget.TextView
import com.xinzy.component.java.widget.GridDividerItemDecoration
import com.xinzy.component.java.widget.MultiAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mDatas = arrayListOf(
            Item("公告板", "NoticeBoard"),
            Item("PagerGridView", "PagerGridView"),
            Item("ActionSheet", "ActionSheet"),
            Item("BannerView", "BannerView"),
            Item("Dialog", "Dialog"),
            Item("Tab", "Tab"),
            Item("MultiAdapter", "MultiAdapter"),
            Item("RadiusTextView", "RadiusTextView"),
            Item("TipDialog", "TipDialog"),
            Item("RadiusImageView", "RadiusImageView")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = MultiAdapter()
        adapter.registerProvider(0, Provider())
        contentView.layoutManager = GridLayoutManager(this, 3)
        contentView.addItemDecoration(GridDividerItemDecoration(3, ContextCompat.getDrawable(this, R.drawable.divider)))
        contentView.adapter = adapter

        adapter.addAll(mDatas)
        adapter.setOnItemClickListener { _, position ->
            kotlin.run {
                val tag = mDatas[position].clazz
                val intent = Intent()
                intent.setClassName(packageName, "com.xinzy.component.activity.${tag}Activity")
                startActivity(intent)
            }
        }
    }

}

class Provider : MultiAdapter.ItemProvider<Item>() {
    override fun onBindViewHolder(viewHolder: MultiAdapter.MultiViewHolder, data: Item, position: Int) {
        viewHolder.get<TextView>(R.id.itemText).text = data.title
    }

    override fun getItemViewLayoutId() = R.layout.item_main

    override fun canClicked() = true
}

class Item(val title: String, val clazz: String) : MultiAdapter.ViewType {
    override fun getItemType() = 0
}
