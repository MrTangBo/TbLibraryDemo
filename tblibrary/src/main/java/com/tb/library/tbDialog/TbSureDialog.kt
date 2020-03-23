package com.tb.library.tbDialog

import android.os.Bundle
import android.view.View
import com.tb.library.R
import com.tb.library.base.TbApplication
import com.tb.library.databinding.TbSureDialogBinding
import com.tb.library.tbExtend.TbOnClick
import com.tb.library.tbExtend.tbGetDimensValue

/**
 * @CreateDate: 2020/3/12 1:12
 * @Description: 确认弹框
 * @Author: TangBo
 */
open class TbSureDialog(
    var titleTx: CharSequence = "",
    var messageTx: CharSequence = "",
    var sureTx: CharSequence = TbApplication.mApplicationContext.resources.getString(R.string.tb_sure),
    var cancelTx: CharSequence = TbApplication.mApplicationContext.resources.getString(R.string.tb_cancel)
) : TbBaseDialog() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutId(R.layout.tb_sure_dialog)
        setWidth(tbGetDimensValue(R.dimen.x600))
    }

    var sureClick: TbOnClick = {}
    var cancelClick: TbOnClick = {}

    var binding: TbSureDialogBinding? = null

    var setView: ((binding: TbSureDialogBinding) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = dialogBing as TbSureDialogBinding
        binding?.sure?.setOnClickListener {
            sureClick.invoke()
            dismiss()
        }
        binding?.cancel?.setOnClickListener {
            cancelClick.invoke()
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        if (titleTx.isEmpty()) {
            binding?.title?.visibility = View.GONE
        } else {
            binding?.title?.visibility = View.VISIBLE
        }
        binding?.title?.text = titleTx
        binding?.message?.text = messageTx
        binding?.sure?.text = sureTx
        binding?.cancel?.text = cancelTx

        binding?.let {
            setView?.invoke(it)
        }
    }

}