package com.tb.library.view

import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bigkoo.convenientbanner.holder.Holder
import com.tb.library.base.TbConfig
import com.tb.library.databinding.TbItemBannerBinding
import com.tb.library.tbExtend.showImage


/**
 *@作者：tb
 *@时间：2019/8/8
 *@描述：banner item设置
 */
open class TbBannerHolderView<T>(
    itemView: View?,
    private var circleSizeRect: Rect,
    private var marginRect: Rect,
    var scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP,
    var placeholder: Int,
    var error: Int,
    var itemBinding: ((binding: ViewDataBinding, data: T) -> Unit)? = null
) : Holder<T>(itemView) {

    private var mBinding: ViewDataBinding? = null

    override fun updateUI(data: T) {

        mBinding?.let {
            if (it is TbItemBannerBinding) {
                val layoutParams: FrameLayout.LayoutParams =
                    it.bannerImage.layoutParams as FrameLayout.LayoutParams
                layoutParams.setMargins(
                    marginRect.left,
                    marginRect.top,
                    marginRect.right,
                    marginRect.bottom
                )
                it.bannerImage.layoutParams = layoutParams
                it.bannerImage.setCornerRadius(
                    circleSizeRect.left.toFloat(),
                    circleSizeRect.top.toFloat(),
                    circleSizeRect.right.toFloat(),
                    circleSizeRect.bottom.toFloat()
                )
                when (data) {
                    is String -> {
                        it.bannerImage.showImage(data, placeholder, error, scaleType = scaleType)
                    }
                    is Int -> {
                        it.bannerImage.scaleType = scaleType
                        it.bannerImage.setImageResource(data)
                    }
                }
            }
            itemBinding?.invoke(it, data)
        }

    }

    override fun initView(itemView: View?) {
        mBinding = DataBindingUtil.bind(itemView!!)
    }

}