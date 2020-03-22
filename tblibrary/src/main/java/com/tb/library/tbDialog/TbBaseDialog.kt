package com.tb.library.tbDialog

import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.tb.library.R


/**
 * @CreateDate: 2020/3/12 1:12
 * @Description: TODO
 * @Author: TangBo
 */
open class TbBaseDialog : DialogFragment() {

    private var mWidth: Int = WindowManager.LayoutParams.MATCH_PARENT
    private var mHeight: Int = WindowManager.LayoutParams.WRAP_CONTENT
    @LayoutRes
    private var layoutId: Int = 0
    private var gravity: Int = Gravity.CENTER
    private var touchOutside: Boolean = false
    @StyleRes
    private var style: Int = R.style.tbDialogFullStyle

    private var rootView: View? = null

    open fun setTouchOutside(touchOutside: Boolean): TbBaseDialog {
        this.touchOutside = touchOutside
        return this
    }

    open fun setGravity(gravity: Int): TbBaseDialog {
        this.gravity = gravity
        return this
    }

    open fun setLayoutId(layoutId: Int): TbBaseDialog {
        this.layoutId = layoutId
        return this
    }

    open fun setWidth(mWidth: Int): TbBaseDialog {
        this.mWidth = mWidth
        return this
    }

    open fun setHeight(mHeight: Int): TbBaseDialog {
        this.mHeight = mHeight
        return this
    }

    open fun setMyStyle(@StyleRes style: Int): TbBaseDialog {
        this.style = style
        return this
    }

    var dialogBing: ViewDataBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, style)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = super.onCreateView(inflater, container, savedInstanceState)
        if (rootView == null) {
            dialogBing = DataBindingUtil.inflate(inflater, layoutId, container, false)
            rootView = dialogBing?.root
        }
        return rootView
    }

    override fun onResume() {
        super.onResume()
        dialog?.setCanceledOnTouchOutside(touchOutside)
        val mWindow = dialog?.window
        val paramas = mWindow?.attributes
        paramas?.width = mWidth
        paramas?.height = mHeight
        paramas?.gravity = gravity
        mWindow?.attributes = paramas
    }
}