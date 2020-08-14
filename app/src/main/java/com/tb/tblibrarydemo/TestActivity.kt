package com.tb.tblibrarydemo

import android.os.Bundle
import android.view.KeyEvent
import com.tb.library.model.TbBaseModel
import com.tb.library.tbExtend.tbShowToast
import com.tb.library.uiActivity.TbBaseActivity
import com.tb.library.util.TbLogUtils
import com.tb.tblibrarydemo.databinding.ActivityTestBinding
import com.tb.tblibrarydemo.koin.MyViewModel
import com.tb.tblibrarydemo.koin.Person
import kotlinx.android.synthetic.main.activity_test.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named


/**
 * Created by Tb on 2020/4/16.
 * describe:
 */
class TestActivity : TbBaseActivity<TbBaseModel, ActivityTestBinding>() {

    val model: MyViewModel by viewModel { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override val mLayoutId: Int
        get() = R.layout.activity_test

    override fun initData() {
        super.initData()
        tex.setOnClickListener {
            val p = model.testViewModel()

            tbShowToast("${p.name}")
//            TbLogUtils.log("------>${p.name}")
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitApp()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}