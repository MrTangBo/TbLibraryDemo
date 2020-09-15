package com.tb.library.model

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonSyntaxException
import com.tb.library.R
import com.tb.library.base.TbApplication
import com.tb.library.base.TbConfig
import com.tb.library.http.BaseResultInfo
import com.tb.library.http.HttpUtil
import com.tb.library.http.RequestListener
import com.tb.library.tbExtend.tb2Json
import com.tb.library.tbExtend.tbGetResString
import com.tb.library.tbExtend.tbNetWorkIsConnect
import com.tb.library.tbExtend.tbShowToast
import com.tb.library.tbReceiver.TbBaseReceiver
import com.tb.library.util.TbLogUtils
import io.reactivex.Flowable
import org.greenrobot.eventbus.EventBus
import org.reactivestreams.Subscription
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import kotlin.collections.ArrayList
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.arrayListOf
import kotlin.collections.forEach
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set

/**
 * @CreateDate: 2020/3/12 1:12
 * @Description: TODO
 * @Author: TangBo
 */
open class TbBaseModel : ViewModel(), LifecycleObserver, RequestListener {

    var mLiveDataMap: MutableMap<Int, MutableLiveData<Any>> = mutableMapOf()

    var mRequestMap: ArrayList<MutableMap<String, String>> = arrayListOf()
    var mSubscriptions: MutableList<Subscription> = mutableListOf()
    var mFlowableMap: MutableMap<Int, MutableList<Flowable<*>>> = mutableMapOf()

    var mPage = 1
    var mIsShowLoading = true
    var mIsShowLayoutLoading = true
    var mDialogDismiss: (isInternet: Boolean, isError: Boolean, taskId: Int) -> Unit =
        { _, _, _ -> Unit }
    var mDialogShow: (isShowLoading: Boolean, isShowLayoutLoading: Boolean) -> Unit =
        { _, _ -> Unit }
    var mErrorCodeEvent: (code: Any, msg: String, taskId: Int) -> Unit = { _, _, _ -> Unit }

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
            if (mIsShowLoading) {
                mDialogShow.invoke(mIsShowLoading, mIsShowLayoutLoading)
            }
            HttpUtil.getInstance().startRequest(flowables, this, taskId)
        } else {
            mDialogDismiss.invoke(false, false, taskId)
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
                mErrorCodeEvent.invoke(code, info.mMessage, taskId)
            }
        }
        TbLogUtils.log("taskId-$taskId--->${info.mData.tb2Json()}")
    }

    override fun onComplete(taskId: Int) {
        mFlowableMap.clear()
        mIsShowLoading = true
        mIsShowLayoutLoading = false
        mDialogDismiss.invoke(true, false, taskId)
    }

    override fun onError(t: Throwable?, taskId: Int) {
        mIsShowLoading = true
        mIsShowLayoutLoading = false
        mDialogDismiss.invoke(true, true, taskId)
        if (mPage > 1) {
            mPage--
        }
        when (t) {
            is ConnectException, is UnknownHostException -> {
                tbShowToast(tbGetResString(R.string.connect_error))
            }
            is TimeoutException, is SocketTimeoutException -> {
                tbShowToast(tbGetResString(R.string.connect_time_out))
            }
            is JsonSyntaxException -> {
                tbShowToast(tbGetResString(R.string.json_error))
            }
            else -> {
                tbShowToast(tbGetResString(R.string.other_error))
            }
        }
        TbLogUtils.log("error---->${t.tb2Json()}")
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