package com.tb.tblibrarydemo

import android.os.Bundle
import android.view.KeyEvent
import com.tb.library.model.TbBaseModel
import com.tb.library.tbExtend.tb2Json
import com.tb.library.tbExtend.tbShowToast
import com.tb.library.uiActivity.TbBaseActivity
import com.tb.library.util.TbLogUtils
import com.tb.tblibrarydemo.databinding.ActivityTestBinding
import com.tb.tblibrarydemo.koin.KoinBaseActivity
import com.tb.tblibrarydemo.koin.KoinChildModel
import com.tb.tblibrarydemo.koin.KoinModel
import com.tb.tblibrarydemo.koin.ViewManager
import kotlinx.android.synthetic.main.activity_test.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor


/**
 * Created by Tb on 2020/4/16.
 * describe:
 */
class TestActivity : KoinBaseActivity<KoinChildModel, ActivityTestBinding>() {

    val mList: ArrayList<Any> by inject(named("list"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tex.setOnClickListener {
            mList.add("s")
            mList.add("3")
            mList.add("4")
            mModel.test()
            TbLogUtils.log("------>${mList.tb2Json()}")
        }

        ViewManager::class.constructors.forEach {
            if (it.parameters.size==1){
              val s=  it.call("蓝调")
                tbShowToast(s.name)
            }
        }

        ViewManager::class.memberProperties.forEach {

            TbLogUtils.log("property--->${it.name}")
        }



    }

    override val mLayoutId: Int
        get() = R.layout.activity_test

}

