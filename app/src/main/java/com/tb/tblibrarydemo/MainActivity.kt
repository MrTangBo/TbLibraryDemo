package com.tb.tblibrarydemo

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.tb.library.tbExtend.*
import com.tb.library.uiActivity.TbTitleBaseActivity
import com.tb.tblibrarydemo.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : TbTitleBaseActivity<TestMode>() {

    lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        mLayoutId = R.layout.activity_main
        super.onCreate(savedInstanceState)
    }

    override fun getModel() {
        super.getModel()
        mMode = ViewModelProvider(this).get(TestMode::class.java)
        mMode?.initLiveData(Api.firstId)
        mTbLoadLayout = mLoadLayout
        mSpringView = springView
    }

    override fun initData() {
        super.initData()

        tbStatusBarInit(isImmersive = true, immersiveBottom = false)
        initToolBar(paddingTop = tbStatusBarHeight()[0])
//        initMenu(arrayListOf("搜索"))
        mSearchView.init(isExpand = false)

        mBinding = mBaseBinding as ActivityMainBinding
        mBinding.url = "sadsadsa"

        val imagList = arrayListOf<String>()
        imagList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584776339191&di=8ddbdc51e8ebd48d253f890a2144d4e0&imgtype=0&src=http%3A%2F%2Fa3.att.hudong.com%2F14%2F75%2F01300000164186121366756803686.jpg")
        imagList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584776339191&di=2f2b0da66fb2f98a2d9a2dcae6a9ab80&imgtype=0&src=http%3A%2F%2Fa4.att.hudong.com%2F21%2F09%2F01200000026352136359091694357.jpg")
        imagList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584776339191&di=8ddbdc51e8ebd48d253f890a2144d4e0&imgtype=0&src=http%3A%2F%2Fa3.att.hudong.com%2F14%2F75%2F01300000164186121366756803686.jpg")
        imagList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584776339191&di=2f2b0da66fb2f98a2d9a2dcae6a9ab80&imgtype=0&src=http%3A%2F%2Fa4.att.hudong.com%2F21%2F09%2F01200000026352136359091694357.jpg")

        mBinding.url=imagList[0]

        mBanner.initBanner(imagList, isCanLoop = true)
    }

    override fun <E> resultData(taskId: Int, info: E) {
        super.resultData(taskId, info)
        val mInfo = info as TestBean
        mTx.text = "${mInfo.data[0].content}--->t${tbGetShared<String>("name")}"
    }

    override fun <M> errorCodeEvent(code: M, msg: String) {
        tbShowToast(code.toString() + msg)
    }
}
