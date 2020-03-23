package com.tb.library.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import com.flyco.roundview.RoundRelativeLayout
import com.makeramen.roundedimageview.RoundedImageView
import com.tb.library.R
import com.tb.library.tbExtend.TbOnClick
import com.tb.library.tbExtend.showImage
import com.tb.library.tbExtend.tbGetDimensValue


/**
 *@作者：tb
 *@时间：2019/10/17
 *@描述：带删除功能的ImageView
 */
class DeleteImageView : RoundRelativeLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private var deleteClick: TbOnClick = {}
    private var imgClick: TbOnClick = {}

    private fun init() {
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        layoutParams.addRule(CENTER_IN_PARENT)
        val image = RoundedImageView(context)
        image.scaleType = ImageView.ScaleType.CENTER_CROP
        addView(image, layoutParams)
        image.setOnClickListener {
            imgClick.invoke()
        }
        val layoutParamsDelete = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParamsDelete.addRule(RelativeLayout.ALIGN_PARENT_END)
        layoutParamsDelete.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        val imageDelete = AppCompatImageView(context)
        imageDelete.setImageResource(R.drawable.icon_close)
        imageDelete.setOnClickListener {
            deleteClick.invoke()
        }
        addView(imageDelete, layoutParamsDelete)
    }


    fun setClick(deleteClick: TbOnClick = {}, imgClick: TbOnClick = {}) {
        this.deleteClick = deleteClick
        this.imgClick = imgClick
    }

    /*设置删除图标*/
    fun setDeleteImg(resId: Int): DeleteImageView {
        val image: AppCompatImageView = getChildAt(1) as AppCompatImageView
        image.setImageResource(resId)
        return this
    }

    /*加载图片*/
    fun setImgUrl(
        url: String,
        scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP,
        radus: Int = tbGetDimensValue(R.dimen.x5),
        padding: Rect = Rect()
    ): DeleteImageView {
        val image: RoundedImageView = getChildAt(0) as RoundedImageView
        image.setPadding(
            padding.left,
            padding.top,
            padding.right,
            padding.bottom
        )
        image.cornerRadius = radus.toFloat()
        image.showImage(url, scaleType)
        return this
    }

    fun setImgResId(
        resId: Int,
        scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP
    ): DeleteImageView {
        val image: RoundedImageView = getChildAt(0) as RoundedImageView
        image.scaleType = scaleType
        image.setImageResource(resId)
        return this
    }
}