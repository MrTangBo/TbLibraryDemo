package com.tb.library.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 *@作者：tb
 *@时间：2019/7/12
 *@描述：
 */
open class TbFullRecyclerView : RecyclerView {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle)

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        var expandSpec = MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthSpec, expandSpec)
    }
}