package com.tb.library.view


import android.animation.ObjectAnimator
import android.app.Activity
import android.view.*
import android.widget.PopupWindow
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.tb.library.R
import com.tb.library.tbExtend.TbOnClick


/**
 *@作者：tb
 *@时间：2019/7/26
 *@描述：兼容7.0位置显示不正确的PopupWindow
 */
class TbPopupWindow(
    var mActivity: Activity,
    @LayoutRes var popLayoutId: Int,
    windowWith: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    windowHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    var windowTransScale: Float = 1.0f,
    @DrawableRes var drawableId: Int = R.color.tb_transparent,//不给默认的背景，PopupWindow 点击外部和返回键无法消失
    isFocusable: Boolean = true,
    isTouchable: Boolean = true,
    isOutsideTouchable: Boolean = true,
    dismissListener: TbOnClick = {},
    animatorStyle: Int = 0
) : PopupWindow() {
    private var parms: WindowManager.LayoutParams? = null
    private var mWidth = 0
    private var mHeight = 0
    var popBaseBind: ViewDataBinding? = null
    private var direction: String = ""

    init {
        parms = mActivity.window.attributes
        setBackgroundDrawable(ContextCompat.getDrawable(mActivity, drawableId))
        if (animatorStyle!=0){
            animationStyle=animatorStyle
        }
        // 设置PopupWindow的大小（宽度和高度）
        width = windowWith
        height = windowHeight
        popBaseBind = DataBindingUtil.inflate(LayoutInflater.from(mActivity), popLayoutId, null, false)
        val mLayoutView = popBaseBind?.root
        mLayoutView?.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        mWidth = mLayoutView!!.measuredWidth
        mHeight = mLayoutView.measuredHeight
        contentView = mLayoutView
        this.isFocusable = isFocusable // 设置PopupWindow可获得焦点
        this.isTouchable = isTouchable // 设置PopupWindow可触摸
        this.isOutsideTouchable = isOutsideTouchable // 设置非PopupWindow区域可触摸
        setOnDismissListener {
            parms?.alpha = 1.0f
            mActivity.window.attributes = parms
            dismissListener.invoke()
        }
        setTouchInterceptor { _, motionEvent ->
            if (!isOutsideTouchable()) {
                contentView?.dispatchTouchEvent(motionEvent)
            }
            return@setTouchInterceptor isFocusable() && !isOutsideTouchable()
        }
    }

    override fun dismiss() {
        var animator: ObjectAnimator
        when (direction) {
            "bottom" -> {
                animator = ObjectAnimator.ofFloat(popBaseBind?.root, "translationY", 0f, -mHeight * 1.0f)
                animator.duration = 300
                animator.start()
                animator.doOnEnd {
                    super.dismiss()
                }
            }
            "top" -> {
                animator = ObjectAnimator.ofFloat(popBaseBind?.root, "translationY", 0f, mHeight * 1.0f)
                animator.duration = 300
                animator.start()
                animator.doOnEnd {
                    super.dismiss()
                }
            }
            "left" -> {
                animator = ObjectAnimator.ofFloat(popBaseBind?.root, "translationX", 0f, mWidth * 1.0f)
                animator.duration = 300
                animator.start()
                animator.doOnEnd {
                    super.dismiss()
                }
            }
            "right" -> {
                animator = ObjectAnimator.ofFloat(popBaseBind?.root, "translationX", 0f, -mWidth * 1.0f)
                animator.duration = 300
                animator.start()
                animator.doOnEnd {
                    super.dismiss()
                }
            }
            else -> {
                super.dismiss()
            }

        }
    }

    /*显示在控件正上方*/
    fun showAtLocationTop(view: View, marginDp: Int) {
        direction = "top"
        popBaseBind?.root?.translationY = mHeight * 1.0f
        val array = IntArray(2)
        view.getLocationInWindow(array)
        showAtLocation(
            view,
            Gravity.NO_GRAVITY,
            (array[0] + view.width / 2) - mWidth.div(2),
            array[1] - mHeight - marginDp
        )
        ObjectAnimator.ofFloat(popBaseBind?.root, "translationY", mHeight * 1.0f, 0f).setDuration(300).start()

    }

    /*显示在控件正下方*/
    fun showAtLocationBottom(view: View, marginDp: Int) {
        direction = "bottom"
        popBaseBind?.root?.translationY = -mHeight * 1.0f
        val array = IntArray(2)
        view.getLocationInWindow(array)
        showAtLocation(
            view,
            Gravity.NO_GRAVITY,
            (array[0] + view.width / 2) - mWidth.div(2),
            array[1] + view.height + marginDp
        )
        ObjectAnimator.ofFloat(popBaseBind?.root, "translationY", -mHeight * 1.0f, 0f).setDuration(300).start()
    }

    /*显示在控件正左方*/
    fun showAtLocationLeft(view: View, marginDp: Int) {
        direction = "left"
        popBaseBind?.root?.translationX = mWidth * 1.0f
        val array = IntArray(2)
        view.getLocationInWindow(array)
        showAtLocation(
            view,
            Gravity.NO_GRAVITY,
            array[0] - mWidth - marginDp,
            array[1] + view.height / 2 - mHeight / 2
        )
        ObjectAnimator.ofFloat(popBaseBind?.root, "translationX", mWidth * 1.0f, 0f).setDuration(300).start()
    }

    /*显示在控件正右方*/
    fun showAtLocationRight(view: View, marginDp: Int) {
        direction = "right"
        popBaseBind?.root?.translationX = -mWidth * 1.0f
        val array = IntArray(2)
        view.getLocationInWindow(array)
        showAtLocation(
            view,
            Gravity.NO_GRAVITY,
            array[0] + view.width + marginDp,
            array[1] + view.height / 2 - mHeight / 2
        )
        ObjectAnimator.ofFloat(popBaseBind?.root, "translationX", -mWidth * 1.0f, 0f).setDuration(300).start()
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        parms?.alpha = windowTransScale
        mActivity.window.attributes = parms
        super.showAsDropDown(anchor, xoff, yoff, gravity)
    }

        override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        parms?.alpha = windowTransScale
        mActivity.window.attributes = parms
        when (gravity) {
            Gravity.BOTTOM -> {
                direction = "top"
                popBaseBind?.root?.translationY = mHeight * 1.0f
                ObjectAnimator.ofFloat(popBaseBind?.root, "translationY", mHeight * 1.0f, 0f).setDuration(300).start()
                super.showAtLocation(parent, gravity, x, y)
            }
            Gravity.TOP -> {
                direction = "bottom"
                popBaseBind?.root?.translationY = -mHeight * 1.0f
                ObjectAnimator.ofFloat(popBaseBind?.root, "translationY", -mHeight * 1.0f, 0f).setDuration(300).start()
                super.showAtLocation(parent, gravity, x, y)
            }
            Gravity.START -> {
                direction = "right"
                popBaseBind?.root?.translationX = -mWidth * 1.0f
                ObjectAnimator.ofFloat(popBaseBind?.root, "translationX", -mWidth * 1.0f, 0f).setDuration(300).start()
                super.showAtLocation(parent, gravity, x, y)
            }
            Gravity.END -> {
                direction = "left"
                popBaseBind?.root?.translationX = mWidth * 1.0f
                ObjectAnimator.ofFloat(popBaseBind?.root, "translationX", mWidth * 1.0f, 0f).setDuration(300).start()
                super.showAtLocation(parent, gravity, x, y)
            }
            else -> super.showAtLocation(parent, gravity, x, y)
        }


    }

}
