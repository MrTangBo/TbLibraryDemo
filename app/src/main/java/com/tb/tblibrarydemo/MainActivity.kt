package com.tb.tblibrarydemo

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.AppBarLayout
import com.tb.library.base.TbConfig
import com.tb.library.base.TbEventBusInfo
import com.tb.library.tbExtend.*
import com.tb.library.tbZxingUtil.android.TbCaptureActivity
import com.tb.library.uiActivity.TbTitleBaseActivity
import com.tb.library.util.TbLogUtils
import com.tb.tblibrarydemo.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus

class MainActivity : TbTitleBaseActivity<TestMode, ActivityMainBinding>() {


    override val mLayoutId: Int
        get() = R.layout.activity_main


    override fun getModel() {
        super.getModel()
        mMode = ViewModelProvider(this).get(TestMode::class.java)
        mMode?.initLiveData(Api.getData)
//        mTbLoadLayout = mLoadLayout
        mSpringView = springView


        TbLogUtils.log("222--->$mIsOpenARouter")
    }

    override fun initData() {
        super.initData()
        tbStatusBarInit(isImmersive = true, isFitWindowStatusBar = true)
        initToolBar()
//        initMenu(arrayListOf("搜索"))
        mSearchView.init(isExpand = false)

        mBinding.url = "sadsadsa"

        val imagList = arrayListOf<String>()
        imagList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584776339191&di=8ddbdc51e8ebd48d253f890a2144d4e0&imgtype=0&src=http%3A%2F%2Fa3.att.hudong.com%2F14%2F75%2F01300000164186121366756803686.jpg")
        imagList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584776339191&di=2f2b0da66fb2f98a2d9a2dcae6a9ab80&imgtype=0&src=http%3A%2F%2Fa4.att.hudong.com%2F21%2F09%2F01200000026352136359091694357.jpg")
        imagList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584776339191&di=8ddbdc51e8ebd48d253f890a2144d4e0&imgtype=0&src=http%3A%2F%2Fa3.att.hudong.com%2F14%2F75%2F01300000164186121366756803686.jpg")
        imagList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584776339191&di=2f2b0da66fb2f98a2d9a2dcae6a9ab80&imgtype=0&src=http%3A%2F%2Fa4.att.hudong.com%2F21%2F09%2F01200000026352136359091694357.jpg")

        mBinding.url = imagList[0]


        0.tbSetShared("userId")

        TbLogUtils.log("userId-->${tbGetShared<Int>("userId")}")

        mBanner.initBanner(imagList, isCanLoop = true)


        mtBN.setTitle(
            arrayListOf("首页", "论坛", "订单", "消息", "我的"),
            arrayListOf(
                R.drawable.icon_close,
                R.drawable.icon_close,
                R.drawable.icon_close,
                R.drawable.icon_close,
                R.drawable.icon_close
            ),
            arrayListOf(
                R.drawable.ic_delete_photo,
                R.drawable.def_qq,
                R.drawable.ic_delete_photo,
                R.drawable.ic_delete_photo,
                R.drawable.ic_delete_photo
            ), iconSize = tbGetDimensValue(R.dimen.x15), clickPosition = {
                if (it == 2)
                    mMode?.getData()
//                tbStartActivity<TbCaptureActivity>(requestCode = 3000)
            }
        )
            .setBadgeNumSingle(
                3,
                20,
                bgColor = R.color.colorAccent,
                moveUpListener = { badge, targetView ->
                })

    }

    override fun <E> resultData(taskId: Int, info: E) {
        super.resultData(taskId, info)
        val mInfo = info as TestBean

//        mTx.text = "${mInfo.data[0].content}--->t${tbGetShared<String>("name")}"

    }

    override fun <M> errorCodeEvent(code: M, msg: String, taskId: Int) {
        super.errorCodeEvent(code, msg, taskId)
        tbShowToast(code.toString() + msg)
    }

    override fun onClick(view: View?) {
        super.onClick(view)

    }

    override fun singleClick(view: View) {
        super.singleClick(view)
    }
}

class myE : TbEventBusInfo() {

}
