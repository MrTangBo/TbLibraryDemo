package com.tb.library.uiFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.liaoinstan.springview.widget.SpringView
import com.tb.library.base.TbConfig
import com.tb.library.base.TbEventBusInfo
import com.tb.library.model.TbBaseModel
import com.tb.library.tbDialog.TbLoadingDialog
import com.tb.library.tbExtend.init
import com.tb.library.tbExtend.tbIsMultiClick
import com.tb.library.util.FontUtil
import com.tb.library.view.TbLoadLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * @CreateDate: 2020/3/18 22:46
 * @Description: TODO
 * @Author: TangBo
 */
abstract class TbBaseFragment<T : TbBaseModel, G : ViewDataBinding> : Fragment() {

    var mMode: T? = null
    lateinit var mBinding: G
    var mRootView: View? = null

    var mTbLoadLayout: TbLoadLayout? = null
    var mSpringView: SpringView? = null

    lateinit var mLoadingDialog: TbLoadingDialog

    lateinit var fActivity: FragmentActivity

    var isLoad = false

    open val mIsOpenEventBus = false//是否开启EventBus


    abstract val mLayoutId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mRootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, mLayoutId, container, false)
            mRootView = mBinding.root
            init()
            initData()
        }
        return mRootView
    }

    open fun init() {
        fActivity = activity!!
        initLoadingDialog()
        if (mIsOpenEventBus) {
            EventBus.getDefault().register(this)
        }
    }

    open fun initSpringView() {
        mSpringView?.let { springView ->
            mMode?.let {
                mSpringView = springView.init(it)
            }
        }
    }

    open fun getModel() {

    }

    open fun initModel() {
        getModel()
        mMode?.let { model ->
            initSpringView()
            model.mTbLoadLayout = mTbLoadLayout
            model.mSpringView = mSpringView
            lifecycle.addObserver(model)
            model.mFragment = this
            model.mBinding = mBinding

            model.mDialogDismiss = {
                hideLoadingDialog()
            }
            model.mDialogShow = {
                showLoadingDialog()
            }

            model.mErrorCodeEvent = { code, msg, taskId ->
                errorCodeEvent(code, msg, taskId)
            }
            model.mLiveDataMap.forEach { map ->
                map.value.observe(this, Observer {
                    resultData(map.key, it)
                })
            }
        }
    }

    open fun initData() {

    }

    open fun showLoadingDialog() {
        mLoadingDialog.show()
    }

    open fun hideLoadingDialog() {
        mLoadingDialog.dismiss()
    }

    open fun <E> resultData(taskId: Int, info: E) {


    }

    open fun <M> errorCodeEvent(code: M, msg: String, taskId: Int) {

    }

    open fun initLoadingDialog() {
        mLoadingDialog = TbLoadingDialog(fActivity)
    }

    open fun onClick(view: View?) {
        if (tbIsMultiClick()) return
        view?.let {
            singleClick(it)
        }
    }

    /*单击事件，防止连点就调用这个方法*/
    open fun singleClick(view: View) {

    }

    open lateinit var mEventInfo: TbEventBusInfo
    /*eventBus回调*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onUserEvent(event: TbEventBusInfo) {
        mEventInfo = event

    }

    override fun onResume() {
        super.onResume()
        if (TbConfig.getInstance().fontType.isNotEmpty()) {
            FontUtil.replaceFont(mRootView, TbConfig.getInstance().fontType)
        }
        initModel()
        if (!isLoad) {
            loadData()
            isLoad = true
        }
    }

    /*懒加载数据*/
    abstract fun loadData()

    override fun onDestroy() {
        super.onDestroy()
        mMode?.dropView()
        if (mIsOpenEventBus) {
            EventBus.getDefault().unregister(this)
        }
    }
}