package com.tb.tblibrarydemo

import com.tb.library.model.TbBaseModel
import com.tb.library.uiActivity.TbBaseActivity
import com.tb.tblibrarydemo.databinding.ActivityTestBinding

/**
 * Created by Tb on 2020/4/16.
 * describe:
 */
class TestActivity : TbBaseActivity<TbBaseModel, ActivityTestBinding>() {

    override val mLayoutId: Int
        get() = R.layout.activity_test


}