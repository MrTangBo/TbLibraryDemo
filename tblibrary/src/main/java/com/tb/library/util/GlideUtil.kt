package com.tb.library.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.tb.library.base.TbApplication
import com.tb.library.base.TbConfig


class GlideUtil {

    /*可以单独设置默认加载和错误图*/
    private var mPlaceholder: Int = TbConfig.getInstance().placeholder
    private var mError: Int = TbConfig.getInstance().errorHolder

    companion object {
        fun getInstance() = Holder.instance
    }

    private object Holder {
        val instance = GlideUtil()
    }

    /*设置加载中的图片*/
    fun setPlaceholder(resId: Int): GlideUtil {
        getInstance().mPlaceholder = resId
        return this
    }

    /*加载错误图片*/
    fun setError(resId: Int): GlideUtil {
        getInstance().mError = resId
        return this
    }


    //常规的加载
    fun showImage(
        mContext: Context,
        url: String,
        imageView: ImageView,
        placeholder: Int = mPlaceholder,
        error: Int = mError,
        scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP,
        isCacheImage: Boolean = true //是否缓存图片
    ) {
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        Glide.with(mContext)
            .load(url)
            .apply(getOptions(isCacheImage, placeholder, error))
            .transition(DrawableTransitionOptions.withCrossFade())
            .thumbnail(0.1f)
            .into(object : DrawableImageViewTarget(imageView) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    super.onResourceReady(resource, transition)
                    imageView.scaleType = scaleType
                    imageView.setImageDrawable(resource)
                }
            })
    }

    private fun getOptions(
        isCacheImage: Boolean, placeholder: Int,
        error: Int
    ): RequestOptions {
        return RequestOptions()
            .placeholder(placeholder)//加载默认图
            .error(error)//加载失败图
            .diskCacheStrategy(if (isCacheImage) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE)//缓存所有
            .priority(Priority.HIGH)//设置图片加载的优先级
            .skipMemoryCache(!isCacheImage)//是否跳过内存缓存
    }

    /*清理缓存*/
    fun glideClean() {
        Glide.get(TbApplication.mApplicationContext).clearDiskCache()
        Glide.get(TbApplication.mApplicationContext).clearMemory()
    }
}