package com.tb.tblibrarydemo

import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.tb.library.R
import com.tb.library.base.TbApplication
import com.tb.library.base.TbConfig
import com.tb.library.http.BaseResultInfo
import com.tb.library.model.TbBaseModel
import com.tb.library.tbExtend.tb2Json
import com.tb.library.tbExtend.tbNetWorkIsConnect
import com.tb.library.tbExtend.tbShowToast
import com.tb.library.tbReceiver.TbBaseReceiver
import com.tb.library.util.TbLogUtils
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Created by Tb on 2020/6/2.
 * describe:
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
                mm[taskId] = api as suspend Any.() -> MutableList<BaseResultInfo<*>>
            }
            if (tbNetWorkIsConnect()) {
                if (mIsShowLoading) {
                    mDialogShow.invoke(mIsShowLoading, mIsShowLayoutLoading)
                }
                val infos = api(CoroutineRetrofitApi.getInstance().getInterface())
                infos.forEach { info ->
                    info.mCode?.let { code ->
                        if (code == TbConfig.getInstance().successCode) {
                            mLiveDataMap[taskId]?.value = info.mData
                        } else {
                            mErrorCodeEvent.invoke(code, info.mMessage, taskId)
                        }
                    }
                    mm.clear()
                    TbLogUtils.log("taskId-$taskId--->${info.mData.tb2Json()}")
                }
                mIsShowLoading = true
                mIsShowLayoutLoading = false
                mDialogDismiss.invoke(true, false, taskId)
            } else {
                mDialogDismiss.invoke(false, false, taskId)
                TbBaseReceiver.isFirst = false
            }
        } catch (e: Exception) {
            if (mPage > 1) {
                mPage--
            }
            mIsShowLoading = true
            mIsShowLayoutLoading = false
            mDialogDismiss.invoke(true, true, taskId)
            when (e) {
                is ConnectException, is UnknownHostException -> {
                    tbShowToast(TbApplication.mApplicationContext.resources.getString(R.string.connect_error))
                }
                is TimeoutException, is SocketTimeoutException -> {
                    tbShowToast(TbApplication.mApplicationContext.resources.getString(R.string.connect_time_out))

                }
                is JsonSyntaxException -> {
                    tbShowToast(TbApplication.mApplicationContext.resources.getString(R.string.json_error))
                }
                else -> tbShowToast(e.tb2Json())
            }
            TbLogUtils.log("error--->${e.tb2Json()}")
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

