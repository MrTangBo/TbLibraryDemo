package com.tb.tblibrarydemo

import android.view.KeyEvent
import com.tb.library.model.TbBaseModel
import com.tb.library.tbExtend.tbStartActivity
import com.tb.library.uiActivity.TbBaseActivity
import com.tb.tblibrarydemo.databinding.ActivityTestBinding
import kotlinx.android.synthetic.main.activity_test.*

/**
 * Created by Tb on 2020/4/16.
 * describe:
 */
class TestActivity : TbBaseActivity<TbBaseModel, ActivityTestBinding>() {

    override val mLayoutId: Int
        get() = R.layout.activity_test

    override fun initData() {
        super.initData()
        tex.setOnClickListener {


            tbStartActivity<MainActivity>()

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