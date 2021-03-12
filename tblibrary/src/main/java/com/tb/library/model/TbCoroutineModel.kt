package com.tb.library.model

import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.tb.library.R
import com.tb.library.base.TbConfig
import com.tb.library.http.BaseResultInfo
import com.tb.library.http.RetrofitApi
import com.tb.library.tbExtend.tb2Json
import com.tb.library.tbExtend.tbGetResString
import com.tb.library.tbExtend.tbNetWorkIsConnect
import com.tb.library.tbExtend.tbShowToast
import com.tb.library.tbReceiver.TbBaseReceiver
import com.tb.library.util.TbLogUtils
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Created by Tb on 2020/6/2.
 * describe:协程网络请求
 */
open class TbCoroutineModel : TbBaseModel() {

    var mm = mutableMapOf<Int, suspend Any.() -> MutableList<BaseResultInfo<*>>>()



    @Suppress("UNCHECKED_CAST")
    suspend inline fun <reified T> startRequestCoroutine(
        taskId: Int,
        noinline api: suspend T.() -> MutableList<BaseResultInfo<*>>
    ) {
        try {
            run {
                mm.clear()
                mm[taskId] = api as suspend Any.() -> MutableList<BaseResultInfo<*>>
            }
            if (tbNetWorkIsConnect()) {
                if (mIsShowLoading) {
                    mDialogShow.invoke(mIsShowLoading, mIsShowLayoutLoading)
                }
                val infos = api(RetrofitApi.getInstance().getInterface())
                infos.forEach { info ->
                    info.mCode?.let { code ->
                        if (code == TbConfig.getInstance().successCode) {
                            mLiveDataMap[taskId]?.value = info.mData
                            mSuccessCodeEvent.invoke(info.mMessage, taskId)
                            resultData.invoke(taskId, info.mData)
                        } else {
                            mErrorCodeEvent.invoke(code, info.mMessage, taskId)
                        }
                    }
                    TbLogUtils.log("taskId-$taskId--->${info.mData.tb2Json()}")
                }
                mm.clear()
                mIsShowLoading = true
                mIsShowLayoutLoading = false
                mDialogDismiss.invoke(true, false, taskId)
            } else {
                mDialogDismiss.invoke(false, false, taskId)
                mErrorCodeEvent.invoke(TbConfig.ERROR_NO_INTERNET, tbGetResString(R.string.internet_no_use), taskId)
                TbBaseReceiver.isFirst = false
            }
        } catch (e: Exception) {
            if (!viewModelScope.isActive) return
            if (mPage > 1) {
                mPage--
            }
            mIsShowLoading = true
            mIsShowLayoutLoading = false
            mDialogDismiss.invoke(true, true, taskId)
            when (e) {
                is ConnectException, is UnknownHostException -> {
                    mErrorCodeEvent.invoke(TbConfig.ERROR_CONNECT, tbGetResString(R.string.connect_error), taskId)
                }
                is TimeoutException, is SocketTimeoutException -> {
                    mErrorCodeEvent.invoke(TbConfig.ERROR_TIME_OUT, tbGetResString(R.string.connect_time_out), taskId)
                }
                is JsonSyntaxException -> {
                    mErrorCodeEvent.invoke(TbConfig.ERROR_JSON, tbGetResString(R.string.json_error), taskId)
                }
                else -> {
                    mErrorCodeEvent.invoke(TbConfig.ERROR_UNKNOWN, tbGetResString(R.string.other_error), taskId)
                }
            }
            TbLogUtils.log("error---->${e.tb2Json()}")
        }

    }

    override fun dropView() {
        super.dropView()
        viewModelScope.cancel()
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> repeatCoroutine() {
        viewModelScope.launch {
            mm.forEach {
                startRequestCoroutine(
                    it.key,
                    it.value as suspend T.() -> MutableList<BaseResultInfo<*>>
                )
            }
        }
    }
}

