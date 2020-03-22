package com.tb.library.tbDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import com.tb.library.R
import com.tb.library.base.TbApplication
import com.tb.library.base.TbConfig
import com.tb.library.util.FontUtil
import kotlinx.android.synthetic.main.tb_loading.view.*

/**
 * @CreateDate: 2020/3/12 1:12
 * @Description: TODO
 * @Author: TangBo
 */
open class TbLoadingDialog(
    var mConext: Context,
    var remark: String = TbApplication.mApplicationContext.resources.getString(R.string.loading),
    @LayoutRes var layoutId: Int = TbConfig.getInstance().loadingDialogLayoutId,
    @StyleRes styleId: Int = TbConfig.getInstance().loadingStyleId
) : Dialog(mConext, styleId) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewDialog = LayoutInflater.from(mConext).inflate(layoutId, null)
        FontUtil.replaceFont(viewDialog, TbConfig.getInstance().fontType)
        viewDialog.mProgressBarDescribe.text = remark
        setContentView(viewDialog)
        val windowDia = window
        val lp = windowDia!!.attributes
        lp.gravity = Gravity.CENTER
        windowDia.attributes = lp
        setCanceledOnTouchOutside(false)
    }

}