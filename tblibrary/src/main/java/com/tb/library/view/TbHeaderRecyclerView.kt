package com.tb.library.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Nullable
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tb.library.tbAdapter.HeaderViewGridLayoutManager
import com.tb.library.tbAdapter.TbHeaderViewAdapter



/**
*@作者：tb
*@时间：2019/8/9
*@描述：让使用者可以直接操作HeaderRecyclerView来给列表添加头部和尾部，而不需要跟HeaderViewAdapter打交道。
*/
class TbHeaderRecyclerView : RecyclerView {

    //内置的HeaderViewAdapter包装对象。
    private var mAdapter: TbHeaderViewAdapter? = null

    /**
     * 获取HeaderView的个数
     *
     * @return
     */
    val headersCount: Int
        get() = mAdapter!!.getHeadersCount()

    /**
     * 获取FooterView的个数
     *
     * @return
     */
    val footersCount: Int
        get() = mAdapter!!.getFootersCount()

    constructor(context: Context) : super(context) {
        wrapHeaderAdapter()
    }

    constructor(context: Context, @Nullable attrs: AttributeSet) : super(context, attrs) {
        wrapHeaderAdapter()
    }

    constructor(context: Context, @Nullable attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        wrapHeaderAdapter()
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        //如果要使用GridLayoutManager的话，只能使用HeaderViewGridLayoutManager。
        if (layout is GridLayoutManager && layout !is HeaderViewGridLayoutManager) {
            super.setLayoutManager(
                HeaderViewGridLayoutManager(
                    context,
                    layout.spanCount, mAdapter
                )
            )
        } else {
            super.setLayoutManager(layout)
        }
    }

    private fun wrapHeaderAdapter() {
        mAdapter = TbHeaderViewAdapter(super.getAdapter())
        super.setAdapter(mAdapter)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        mAdapter!!.adapter = adapter
    }

    override fun getAdapter(): Adapter<*>? {
        return mAdapter!!.adapter
    }

    /**
     * 添加HeaderView
     *
     * @param view
     */
    fun addHeaderView(view: View) {
        mAdapter!!.addHeaderView(view)
    }

    /**
     * 删除HeaderView
     *
     * @param view
     * @return 是否删除成功
     */
    fun removeHeaderView(view: View): Boolean {
        return mAdapter!!.removeHeaderView(view)
    }

    /**
     * 添加FooterView
     *
     * @param view
     */
    fun addFooterView(view: View) {
        mAdapter!!.addFooterView(view)
    }

    /**
     * 删除FooterView
     *
     * @param view
     * @return 是否删除成功
     */
    fun removeFooterView(view: View): Boolean {
        return mAdapter!!.removeFooterView(view)
    }
}