package com.tb.library.model

import android.app.Activity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.google.gson.JsonSyntaxException
import com.liaoinstan.springview.widget.SpringView
import com.tb.library.R
import com.tb.library.base.RequestInternetEvent
import com.tb.library.base.TbApplication
import com.tb.library.base.TbConfig
import com.tb.library.base.TbEventBusInfo
import com.tb.library.http.BaseResultInfo
import com.tb.library.http.HttpUtil
import com.tb.library.http.RequestListener
import com.tb.library.tbExtend.isForeground
import com.tb.library.tbExtend.tb2Json
import com.tb.library.tbExtend.tbNetWorkIsConnect
import com.tb.library.tbExtend.tbShowToast
import com.tb.library.tbReceiver.TbBaseReceiver
import com.tb.library.util.TbLogUtils
import com.tb.library.view.TbLoadLayout
import io.reactivex.Flowable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.reactivestreams.Subscription
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * @CreateDate: 2020/3/12 1:12
 * @Description: TODO
 * @Author: TangBo
 */
open class TbBaseModel : ViewModel(), LifecycleObserver, RequestListener,
    SpringView.OnFreshListener {

    var mLiveDataMap: MutableMap<Int, MutableLiveData<Any>> = mutableMapOf()

    var mRequestMap: ArrayList<MutableMap<String, String>> = arrayListOf()
    var mSubscriptions: MutableList<Subscription> = mutableListOf()
    var mFlowableMap: MutableMap<Int, MutableList<Flowable<*>>> = mutableMapOf()

    var mBinding: ViewDataBinding? = null
    var mActivity: Activity? = null
    var mFragment: Fragment? = null
    var mTbLoadLayout: TbLoadLayout? = null
    var mSpringView: SpringView? = null

    var mPage = 1
    var mIsShowLoading = true
    var mDialogDismiss: (() -> Unit)? = null
    var mDialogShow: (() -> Unit)? = null
    var mErrorCodeEvent: ((code: Any, msg: String, taskId: Int) -> Unit)? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreate() {
        EventBus.getDefault().register(this)
    }

    /**
     * 通过taskId数量来创建数据管理MutableLiveData的个数
     */
    open fun initLiveData(vararg taskIds: Int) {
        taskIds.forEach {
            mLiveDataMap[it] = MutableLiveData()
        }
    }

    open fun startRequest(
        flowable: Flowable<*>? = null,
        taskId: Int,
        flowables: MutableList<Flowable<*>> = mutableListOf()
    ) {
        flowable?.let {
            flowables.add(it)
        }
        mFlowableMap.clear()
        mFlowableMap[taskId] = flowables
        if (tbNetWorkIsConnect()) {
            mTbLoadLayout?.let {
                if (it.mCurrentShow != TbLoadLayout.CONTENT) {
                    mIsShowLoading = false
                    it.showView(TbLoadLayout.LOADING)
                }
            }
            if (mIsShowLoading) {
                mDialogShow?.invoke()
            }
            HttpUtil.getInstance().startRequest(flowables, this, taskId)
        } else {
            mTbLoadLayout?.showView(TbLoadLayout.NO_INTERNET)
            mDialogDismiss?.invoke()
            mSpringView?.onFinishFreshAndLoadDelay()
            TbBaseReceiver.isFirst = false
        }
        TbLogUtils.log("commitData--->${mRequestMap.tb2Json()}")
        mRequestMap.clear()
    }

    open fun repeatQuest() {
        mSubscriptions.forEach {
            it.cancel()
        }
        mFlowableMap.forEach {
            HttpUtil.getInstance().startRequest(it.value, this, it.key)
        }
    }

    /*取消所有的操作*/
    open fun dropView() {
        mSubscriptions.forEach {
            it.cancel()
        }
        mLiveDataMap.clear()
        mFlowableMap.clear()
        mRequestMap.clear()
        mSubscriptions.clear()
        EventBus.getDefault().unregister(this)
    }

    override fun start(s: Subscription?, taskId: Int) {
        s?.let {
            mSubscriptions.add(it)
        }
    }

    override fun <T> onNext(t: T, taskId: Int) {
        val info = t as BaseResultInfo<*>
        //子类实现
        info.mCode?.let { code ->
            if (code == TbConfig.getInstance().successCode) {
                mLiveDataMap[taskId]?.value = info.mData
            } else {
                mErrorCodeEvent?.invoke(code, info.mMessage, taskId)
            }
        }
        TbLogUtils.log("taskId-$taskId--->${info.mData.tb2Json()}")
    }

    override fun onComplete(taskId: Int) {
        mFlowableMap.clear()
        mIsShowLoading = true
        mDialogDismiss?.invoke()
        mSpringView?.onFinishFreshAndLoadDelay()
    }

    override fun onError(t: Throwable?, taskId: Int) {
        mIsShowLoading = true
        mDialogDismiss?.invoke()
        mTbLoadLayout?.showView(TbLoadLayout.ERROR)
        mSpringView?.onFinishFreshAndLoadDelay()
        if (mPage > 1) {
            mPage--
        }
        when (t) {
            is ConnectException, is UnknownHostException -> {
                tbShowToast(TbApplication.mApplicationContext.resources.getString(R.string.connect_error))
            }
            is TimeoutException, is SocketTimeoutException -> {
                tbShowToast(TbApplication.mApplicationContext.resources.getString(R.string.connect_time_out))

            }
            is JsonSyntaxException -> {
                tbShowToast(TbApplication.mApplicationContext.resources.getString(R.string.json_error))

            }
            else -> tbShowToast(t.tb2Json())
        }
        TbLogUtils.log("error--->${t.tb2Json()}")
    }

    open lateinit var mEventInfo: TbEventBusInfo

    /*eventBus回调*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onUserEvent(event: TbEventBusInfo) {
        mEventInfo = event
        if (mTbLoadLayout != null) {
            if (event is RequestInternetEvent && mTbLoadLayout!!.mCurrentShow != TbLoadLayout.CONTENT) {
                repeatQuest()
            }
        } else {
            if (event is RequestInternetEvent && mActivity?.isForeground()!!) {
                repeatQuest()
            }
        }
    }

    override fun onLoadmore() {
        mPage++
        mIsShowLoading = false
        tbSpringViewJoinRefresh()
        tbLoadMore()
    }

    override fun onRefresh() {
        mPage = 1
        mIsShowLoading = false
        tbSpringViewJoinRefresh()
        tbOnRefresh()
    }


    @Deprecated("用tbSpringViewJoinRefresh代替")
    open fun tbOnRefresh() {

    }

    @Deprecated("用tbSpringViewJoinRefresh代替")
    open fun tbLoadMore() {

    }

    open fun tbSpringViewJoinRefresh() {

    }

}