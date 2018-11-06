package com.xinzy.component.kotlin.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.util.SparseArray

/**
 * Created by yangyang on 2018/4/10.
 */

abstract class AbsListAdapter<T> : BaseAdapter() {
    protected val mData = mutableListOf<T>()

    private var mLayoutInflater: LayoutInflater? = null

    fun getData() = mData

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if (mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(parent!!.context)
        val holder = ViewHolder.get(convertView, parent, mLayoutInflater!!, getLayout())
        onBindData(holder, getItem(position), position)

        return holder.getConvertView()
    }

    override fun getItem(position: Int) = mData[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = mData.size

    fun add(t: T) {
        mData.add(t)
        notifyDataSetChanged()
    }

    fun addAll(list: List<T>?) {
        if (list != null && list.isNotEmpty()) {
            mData.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun remove(position: Int) {
        if (mData.size > position) {
            mData.removeAt(position)
            notifyDataSetChanged()
        }
    }

    fun insert(t: T, position: Int) {
        if (mData.size >= position && position >= 0) {
            mData.add(position, t)
            notifyDataSetChanged()
        }
    }

    fun clear() {
        mData.clear()
        notifyDataSetChanged()
    }

    fun replace(list: List<T>?) {
        mData.clear()
        if (list != null && list.isNotEmpty()) {
            mData.addAll(list)
        }
        notifyDataSetChanged()
    }

    abstract fun onBindData(holder: ViewHolder, item: T, position: Int)

    abstract fun getLayout(): Int

    class ViewHolder(private val mConvertView: View) {
        private val mViews = SparseArray<View>()

        companion object {
            fun get(convertView: View?, parent: ViewGroup?, inflater: LayoutInflater, layoutId: Int): ViewHolder {
                if (convertView == null) {
                    val view = inflater.inflate(layoutId, parent, false)
                    val holder = ViewHolder(view)
                    view.tag = holder
                    return holder
                }
                return convertView.tag as ViewHolder
            }
        }

        fun getConvertView() = mConvertView

        fun <T : View> getView(id: Int): T {
            var view = mViews.get(id)
            if (view == null) {
                view = mConvertView.findViewById(id)
                mViews.put(id, view)
            }

            return view as T
        }
    }
}