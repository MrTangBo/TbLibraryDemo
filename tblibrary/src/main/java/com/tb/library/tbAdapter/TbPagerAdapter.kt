package com.tb.library.tbAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bm.library.PhotoView
import com.tb.library.base.TbConfig
import com.tb.library.tbExtend.showImage
import com.tb.library.util.FontUtil

/**
 * @CreateDate: 2020/3/7 5:38
 * @Description: TODO
 * @Author: TangBo
 */

 class TbPagerAdapter<T>(var dataList: List<T>, var context: Context) :
    PagerAdapter() {

    // 获取要滑动的控件的数量，在这里我们以滑动的广告栏为例，那么这里就应该是展示的广告图片的ImageView数量
    override fun getCount(): Int {
        return dataList.size
    }

    // 来判断显示的是否是同一张图片，这里我们将两个参数相比较返回即可
    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1
    }

    // PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
    override fun destroyItem(view: ViewGroup, position: Int, `object`: Any) {
        view.removeView(`object` as View)
    }

    // 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
    override fun instantiateItem(view: ViewGroup, position: Int): Any {

        when {
            (dataList[position] is String) or (dataList[position] is Int) -> {
                val photoView = PhotoView(context)
                // 启用图片缩放功能
                photoView.enable()
                // 设置 动画持续时间
                photoView.animaDuring = 500
                // 设置 最大缩放倍数
                photoView.maxScale = 3f
                // 设置动画的插入器
                photoView.setInterpolator(AccelerateDecelerateInterpolator())
                if (dataList[position] is String) {
                    val url = dataList[position] as String
                    photoView.showImage(url, scaleType = ImageView.ScaleType.CENTER_INSIDE)
                } else {
                    photoView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    photoView.setImageResource(dataList[position] as Int)
                }
                view.addView(photoView)
                return photoView
            }
            dataList[position] is View -> {
                if (TbConfig.getInstance().fontType.isNotEmpty()) {
                    FontUtil.replaceFont(
                        dataList[position] as View,
                        TbConfig.getInstance().fontType
                    )
                }
                view.addView(dataList[position] as View)
                return (dataList[position])!!
            }
        }
        return ""
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

}
