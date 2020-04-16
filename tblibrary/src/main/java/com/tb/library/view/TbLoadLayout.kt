package com.tb.library.view

import android.content.Context
import android.opengl.Visibility
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.view.contains
import androidx.core.view.forEachIndexed
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.flyco.roundview.RoundFrameLayout
import com.tb.library.R
import com.tb.library.base.TbConfig

/**
 * @CreateDate: 2020/3/11 23:47
 * @Description: TODO
 * @Author: TangBo
 */
class TbLoadLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RoundFrameLayout(context, attrs) {
    companion object {
        const val LOADING: Int = 0
        const val NO_DATA: Int = 1
        const val CONTENT: Int = 2
        const val ERROR: Int = 3
        const val NO_INTERNET: Int = 4
    }

    var mBindEmpty: ViewDataBinding = setLayoutId(TbConfig.getInstance().emptyLayoutId)

    var mBindError: ViewDataBinding = setLayoutId(TbConfig.getInstance().errorLayoutId)

    var mBindLoading: ViewDataBinding = setLayoutId(TbConfig.getInstance().loadingLayoutId)

    var mBindNoInternet: ViewDataBinding = setLayoutId(TbConfig.getInstance().noInternetLayoutId)

    var mCurrentShow = LOADING

    fun setEmptyLayoutId(@LayoutRes layoutId: Int): TbLoadLayout {
        if (mBindEmpty.root in this) {
            removeView(mBindEmpty.root)
        }
        mBindEmpty = setLayoutId(layoutId)
        return this
    }

    fun setErrorLayoutId(@LayoutRes layoutId: Int): TbLoadLayout {
        if (mBindError.root in this) {
            removeView(mBindError.root)
        }
        mBindError = setLayoutId(layoutId)
        return this
    }

    fun setLoadingLayoutId(@LayoutRes layoutId: Int): TbLoadLayout {
        if (mBindLoading.root in this) {
            removeView(mBindLoading.root)
        }
        mBindLoading = setLayoutId(layoutId)
        return this
    }

    fun setNoInternetLayoutId(@LayoutRes layoutId: Int): TbLoadLayout {
        if (mBindNoInternet.root in this) {
            removeView(mBindNoInternet.root)
        }
        mBindNoInternet = setLayoutId(layoutId)
        return this
    }

    private fun setLayoutId(@LayoutRes layoutId: Int): ViewDataBinding {
        val b: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            layoutId,
            this,
            false
        )
        return b
    }

    fun showView(type: Int) {
        mCurrentShow = type
        getChildAt(0)?.visibility = View.GONE
        forEachIndexed { index, view ->
            if (index != 0) {
                removeView(view)
            }
        }
        when (type) {
            LOADING -> {
                if (mBindLoading.root in this) return
                addView(mBindLoading.root)
            }
            NO_DATA -> {
                if (mBindEmpty.root in this) return
                addView(mBindEmpty.root)
            }
            ERROR -> {
                if (mBindError.root in this) return
                addView(mBindError.root)
            }
            NO_INTERNET -> {
                if (mBindNoInternet.root in this) return
                addView(mBindNoInternet.root)
            }
            else -> {
                getChildAt(0)?.visibility = View.VISIBLE
            }

        }
    }
}