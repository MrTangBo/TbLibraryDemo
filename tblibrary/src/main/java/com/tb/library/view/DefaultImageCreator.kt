package com.tb.library.view

import android.content.Context
import com.tb.library.tbExtend.showImage


class DefaultImageCreator : LGNineGridView.ImageCreator {
    override fun createImageView(context: Context?): TbPressImageView {
        return TbPressImageView(context!!)
    }

    override fun loadImage(context: Context?, url: String, imageView: TbPressImageView) {
        imageView.showImage(url)
    }
    companion object {
        fun getInstance() = Holder.instance
    }
    private object Holder {
        val instance: DefaultImageCreator = DefaultImageCreator()
    }
}
