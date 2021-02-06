package com.tb.library.tbExtend

import android.animation.ObjectAnimator
import android.view.View


/**
 * Created by Tb on 2021/2/6.
 * describe: 动画相关扩展
 */
/*动画属性配置*/
enum class TbAnimatorProperty {
    translationx, translationY, rotate, rotateX, rotateY, scaleX, scaleY, alpha

}

/**
 * 单一属性动画
 * @receiver View
 * @param property TbAnimatorProperty  动画属性
 */
/*
fun View.tbAnimatorSingle(property: TbAnimatorProperty) {
    val alphaAnim = ObjectAnimator.ofFloat(this, property.toString(), 1.0f, 0.3f)
    //执行事件
    //执行事件
    alphaAnim.duration = 1000
    //延迟
    //延迟
    alphaAnim.startDelay = 300
    alphaAnim.start()
}*/
