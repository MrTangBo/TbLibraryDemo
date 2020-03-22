package com.tb.library.tbExtend

import androidx.recyclerview.widget.RecyclerView

/**
 * @CreateDate: 2020/3/7 5:41
 * @Description: 别名扩展
 * @Author: TangBo
 */
/*单击*/
typealias TbOnClick = (() -> Unit)?//点击事件别名

typealias TbOnClickInfo = ((info: Any) -> Unit)?//点击事件别名（携带参数）
/*列表点击*/
typealias TbItemClick = ((position: Int) -> Unit)?//列表item点击事件别名

typealias TbItemClickInfo = ((position: Int, info: Any) -> Unit)?//列表item点击事件别名（携带参数）
/*RecyclerView滑动*/
typealias TbOnScrolled = ((recyclerView: RecyclerView?, dx: Int, dy: Int) -> Unit)?
typealias TbOnScrollStateChanged = ((recyclerView: RecyclerView?, newState: Int) -> Unit)?

/*ViewPager滑动*/
typealias TbOnPageScrolled=((position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit)?
typealias TbOnPageScrollStateChanged=((state: Int) -> Unit)?