package com.tb.library.tbDialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.tb.library.R
import com.tb.library.databinding.TbSelectPictureDialogBinding
import com.tb.library.tbExtend.TbOnClick

/**
 * @CreateDate: 2020/3/12 1:12
 * @Description: TODO
 * @Author: TangBo
 */
open class TbSelectPictureDialog : TbBaseDialog() {

    var binding: TbSelectPictureDialogBinding? = null
    var pictureClick: TbOnClick = {}
    var takePhotoClick: TbOnClick = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        setMyStyle(R.style.tbSelectPictureStyle)
        super.onCreate(savedInstanceState)
        setLayoutId(R.layout.tb_select_picture_dialog)
        setGravity(Gravity.BOTTOM)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = dialogBing as TbSelectPictureDialogBinding
        binding?.openGallery?.setOnClickListener {
            pictureClick.invoke()
            dismiss()
        }
        binding?.openTakePhoto?.setOnClickListener {
            takePhotoClick.invoke()
            dismiss()
        }
        binding?.cancel?.setOnClickListener {
            dismiss()
        }
    }
}