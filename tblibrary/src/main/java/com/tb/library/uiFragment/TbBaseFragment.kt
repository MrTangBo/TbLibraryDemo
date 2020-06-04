package com.tb.library.uiFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.tb.library.base.RequestInternetEvent
import com.tb.library.base.TbConfig
import com.tb.library.base.TbEventBusInfo
import com.tb.library.model.TbBaseModel
import com.tb.library.tbDialog.TbLoadingDialog
import com.tb.library.tbExtend.isForeground
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
abstract class TbBaseFragment<T : TbBaseModel, G : ViewDataBinding> : Fragment(),
    OnRefreshLoadMoreListener {

    var mMode: T? = null
    lateinit var mBinding: G
    var mRootView: View? = null

    lateinit var mLoadingDialog: TbLoadingDialog

    lateinit var fActivity: FragmentActivity

    var isLoad = false

    open val mIsOpenEventBus = true//是否开启EventBus

    abstract val mLayoutId: Int

    var mTbLoadLayout: TbLoadLayout? = null
    var mSpringView: SmartRefreshLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mRootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, mLayoutId, container, false)
            mRootView = mBinding.root
        }
        return mRootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!this::fActivity.isInitialized) {
            init()
            initModel()
            initData()
        }
    }

    open fun init() {
        fActivity = activity!!
        initLoadingDialog()
        if (mIsOpenEventBus && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }


    /*获取mode*/
    open fun getModel(): T? {

        return null
    }

    /*配置SpringView*/
    open fun getSpringView(): SmartRefreshLayout? {
        return null
    }

    /*配置TbLoadLayout*/
    open fun getTbLoadLayout(): TbLoadLayout? {
        return null
    }

    /*配置TaskIds*/
    open fun initTaskId(): IntArray {
        return intArrayOf()
    }

    open fun initModel() {
        mMode = getModel()
        mSpringView = getSpringView()
        mTbLoadLayout = getTbLoadLayout()
        mMode?.let { model ->
            model.initLiveData(*initTaskId())
            lifecycle.addObserver(model)
            model.mDialogDismiss = { isInternet, isError, taskId ->
                hideLoadingDialog(isInternet, isError, taskId)
            }
            model.mDialogShow = { isShowLoading, isShowLayoutLoading ->
                showLoadingDialog(isShowLoading, isShowLayoutLoading)
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

    open fun showLoadingDialog(isShowLoading: Boolean, isShowLayoutLoading: Boolean) {
        if (isShowLoading) {
            if (mTbLoadLayout == null) {
                mLoadingDialog.show()
            } else {
                if (mTbLoadLayout?.mCurrentShow != TbLoadLayout.CONTENT && isShowLayoutLoading) {
                    mTbLoadLayout?.showView(TbLoadLayout.LOADING)
                } else {
                    mLoadingDialog.show()
                }
            }
        }
    }

    open fun hideLoadingDialog(isInternet: Boolean, isError: Boolean, taskId: Int) {
        mLoadingDialog.dismiss()
        when {
            !isInternet -> {
                mTbLoadLayout?.showView(TbLoadLayout.NO_INTERNET)
            }
            isError -> {
                mTbLoadLayout?.showView(TbLoadLayout.ERROR)
            }
        }
        mSpringView?.finishRefresh(300)
        mSpringView?.finishLoadMore(300)
    }

    open fun <E> resultData(taskId: Int, info: E) {
        mTbLoadLayout?.showView(TbLoadLayout.CONTENT)
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

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mMode?.apply {
            mPage++
            mIsShowLoading = false
            mIsShowLayoutLoading = false
            tbOnRefresh()
            tbSpringViewJoinRefresh()
        }
    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        mMode?.apply {
            mPage = 1
            mIsShowLoading = false
            mIsShowLayoutLoading = false
            tbLoadMore()
            tbSpringViewJoinRefresh()
        }
    }

    /*eventBus回调*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onUserEvent(event: TbEventBusInfo) {
        if (mTbLoadLayout != null) {
            if (event is RequestInternetEvent && mTbLoadLayout!!.mCurrentShow != TbLoadLayout.CONTENT && mTbLoadLayout!!.mCurrentShow != TbLoadLayout.NO_DATA) {
                mMode?.apply {
                    repeatQuest()
                    repeatCoroutine()
                }
            }
        } else {
            if (event is RequestInternetEvent && fActivity.isForeground()) {
                mMode?.apply {
                    repeatQuest()
                    repeatCoroutine()
                }
            }
        }
    }

    /**
     * 使用kotlin协程请求需要联网重连必须子类实现
     */
    open fun repeatCoroutine(){

    }

    override fun onResume() {
        super.onResume()
        if (TbConfig.getInstance().fontType.isNotEmpty()) {
            FontUtil.replaceFont(mRootView, TbConfig.getInstance().fontType)
        }
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

    open fun showContent() {
        mTbLoadLayout?.showView(TbLoadLayout.CONTENT)
    }

    open fun showEmpty() {
        mTbLoadLayout?.showView(TbLoadLayout.NO_DATA)
    }
}