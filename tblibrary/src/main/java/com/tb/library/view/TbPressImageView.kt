package com.tb.library.view

import android.content.Context
import android.graphics.ColorMatrixColorFilter
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 *@作者：tb
 *@时间：2019/9/2
 *@描述：带有点击效果的ImageView
 */
class TbPressImageView : AppCompatImageView {

    val BG_PRESSED = floatArrayOf(1f, 0f, 0f, 0f, -50f, 0f, 1f, 0f, 0f, -50f, 0f, 0f, 1f, 0f, -50f, 0f, 0f, 0f, 1f, 0f)
    val BG_NOT_PRESSED = floatArrayOf(1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun setPressed(pressed: Boolean) {
        updateView(pressed)
        super.setPressed(pressed)
    }

    /**
     * 根据是否按下去来刷新bg和src
     * @param pressed
     */
    private fun updateView(pressed: Boolean) {
        if (pressed && drawable != null) {//点击
            /**
             * 通过设置滤镜来改变图片亮度
             */
            isDrawingCacheEnabled = true
            colorFilter = ColorMatrixColorFilter(BG_PRESSED)
            drawable.colorFilter = ColorMatrixColorFilter(BG_PRESSED)
        } else {//未点击
            colorFilter = ColorMatrixColorFilter(BG_NOT_PRESSED)
            drawable.colorFilter = ColorMatrixColorFilter(BG_NOT_PRESSED)
        }
    }
}
