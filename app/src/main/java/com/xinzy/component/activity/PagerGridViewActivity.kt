package com.xinzy.component.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.xinzy.component.R
import com.xinzy.component.java.widget.AbsListAdapter
import com.xinzy.component.java.widget.PagerGridView
import com.xinzy.component.util.toast
import kotlinx.android.synthetic.main.activity_pager_grid_view.*

class PagerGridViewActivity : AppCompatActivity(), PagerGridView.OnItemClickListener,
        PagerGridView.OnItemLongClickListener, PagerGridView.OnPageChangeListener, Runnable {

    companion object {
        private const val TAG = "PagerGridViewActivity"
    }

    private val mAdapter = Adapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager_grid_view)

        adapterGridView.setAdapter(mAdapter)
        adapterGridView.setOnItemClickListener(this)
        adapterGridView.setOnItemLongClickListener(this)
        adapterGridView.setOnPageChangeListener(this)

        pageText.post(this)
    }

    override fun onItemLongClick(pagerGridView: PagerGridView?, view: View?, position: Int, id: Long) {
        toast("item long click: $position")
    }

    override fun onItemClick(pagerGridView: PagerGridView?, view: View?, position: Int, id: Long) {
        toast("item click: $position")
    }

    override fun onPageScrollStateChanged(state: Int) {
        Log.i(TAG, "onPageScrollStateChanged: state=$state")
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        Log.i(TAG, "onPageScrolled: position=$position, positionOffset=$positionOffset, positionOffsetPixels=$positionOffsetPixels")
    }

    override fun onPageSelected(position: Int) {
        Log.i(TAG, "onPageScrollStateChanged: position=$position")
        run()
    }

    override fun run() {
        pageText.text = "${adapterGridView.currentPage} / ${adapterGridView.pageSize}"
    }

    fun onAddOne(view: View) {
        mAdapter.add("30")
    }

    fun onDelOne(view: View) {
        mAdapter.remove(0)
    }

    fun onReplace(view: View) {
        val list = mutableListOf<String>()
        (40 .. 50).forEach { list.add("$it") }
        mAdapter.replace(list)
    }

    fun onClear(view: View) {
        mAdapter.clear()
    }

    fun onPrevPage(view: View) {
        adapterGridView.previousPage()
    }

    fun onNextPage(view: View) {
        adapterGridView.nextPage()
    }

    private inner class Adapter : AbsListAdapter<String>() {
        init {
            (0 .. 15).forEach { mDatas.add("$it") }
        }

        override fun getLayout() = R.layout.item_grid

        override fun onBindData(holder: ViewHolder, item: String, position: Int) {
            holder.getView<TextView>(R.id.itemText).text = item
        }
    }
}
