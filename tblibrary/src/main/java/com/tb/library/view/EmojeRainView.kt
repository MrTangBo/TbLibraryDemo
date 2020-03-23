package com.tb.library.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.tb.library.R
import java.util.*

/**
 *@作者：tb
 *@时间：2019/11/22
 *@描述：表情雨控件
 */
class EmojeRainView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    private var paint: Paint? = null
    //图片处理
    private var mMatrix: Matrix? = null
    private var random: Random? = null
    //判断是否运行的，默认没有
    private var isRun: Boolean = false
    //表情包集合
    private var bitmapList: MutableList<ItemEmoje>? = null
    //表情图片
    var imgResIds = arrayListOf<Int>(R.drawable.icon_emoje)

    /*下落数量*/
    var num = 19

    init {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isRun) {
            //用于判断表情下落结束，结束即不再进行重绘
            var isInScreen = false
            for (i in bitmapList!!.indices) {
                mMatrix!!.reset()
                //缩放
                mMatrix!!.setScale(bitmapList!![i].scale, bitmapList!![i].scale)
                //下落过程坐标
                bitmapList!![i].x = bitmapList!![i].x + bitmapList!![i].offsetX
                bitmapList!![i].y = bitmapList!![i].y + bitmapList!![i].offsetY
                if (bitmapList!![i].y <= height) {//当表情仍在视图内，则继续重绘
                    isInScreen = true
                }
                //位移
                mMatrix!!.postTranslate(bitmapList!![i].x.toFloat(), bitmapList!![i].y.toFloat())
                canvas.drawBitmap(bitmapList!![i].bitmap!!, mMatrix!!, paint)
            }
            if (isInScreen) {
                postInvalidate()
            } else {
                release()
            }
        }
    }

    /**
     * 释放资源
     */
    private fun release() {
        if (bitmapList != null && bitmapList!!.size > 0) {
            for (itemEmoje in bitmapList!!) {
                if (!itemEmoje.bitmap!!.isRecycled) {
                    itemEmoje.bitmap!!.recycle()
                }
            }
            bitmapList!!.clear()
        }
    }

    private fun init() {
        paint = Paint()
        paint!!.isAntiAlias = true
        paint!!.isFilterBitmap = true
        paint!!.isDither = true
        mMatrix = Matrix()
        random = Random()
        bitmapList = ArrayList()
    }

    private fun initData() {
        for (i in 0..num) {
            val itemEmoje = ItemEmoje()
            val d = ContextCompat.getDrawable(context, imgResIds[(0 until imgResIds.size).random()])
            d?.let {
                itemEmoje.bitmap =
                    d.toBitmap(d.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888)
            }
            //起始横坐标在[100,getWidth()-100) 之间
            itemEmoje.x = random!!.nextInt(width - 200) + 100
            //起始纵坐标在(-getHeight(),0] 之间，即一开始位于屏幕上方以外
            itemEmoje.y = -random!!.nextInt(height)
            //横向偏移[-2,2) ，即左右摇摆区间
            itemEmoje.offsetX = random!!.nextInt(4) - 2
            //纵向固定下落12
            itemEmoje.offsetY = 12
            //缩放比例[0.8,1.2) 之间
            itemEmoje.scale = (random!!.nextInt(40) + 80).toFloat() / 100f
            bitmapList!!.add(itemEmoje)
        }
    }

    fun start(isRun: Boolean) {
        this.isRun = isRun
        initData()
        postInvalidate()
    }

    inner class ItemEmoje {
        //坐标
        var x: Int = 0
        var y: Int = 0
        // 横向偏移
        var offsetX: Int = 0
        //纵向偏移
        var offsetY: Int = 0
        //缩放
        var scale: Float = 0.toFloat()
        //图片资源
        var bitmap: Bitmap? = null
    }
}

