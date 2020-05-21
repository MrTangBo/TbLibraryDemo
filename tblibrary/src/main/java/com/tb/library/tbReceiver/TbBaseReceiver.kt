package com.tb.library.tbReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.tb.library.base.RequestInternetEvent
import com.tb.library.base.TbConfig
import com.tb.library.base.TbEnum
import com.tb.library.base.TbEventBusInfo
import com.tb.library.tbExtend.tbNetWorkIsConnect
import com.tb.library.tbExtend.tbNetWorkIsMobile
import com.tb.library.tbExtend.tbNetWorkIsWifi
import com.tb.library.tbExtend.tbShowToast
import com.tb.library.util.TbLogUtils
import org.greenrobot.eventbus.EventBus

class TbBaseReceiver : BroadcastReceiver() {
    companion object {
        var isFirst: Boolean = true
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (tbNetWorkIsConnect()) {//判断网络是否可用
            if (!isFirst) {
                TbLogUtils.log("onReceive--->")
                if (tbNetWorkIsMobile()) {
                    EventBus.getDefault().post(RequestInternetEvent(TbEnum.MOBILE))
                    tbShowToast("当前正在使用移动网络！")
                } else {
                    EventBus.getDefault().post(RequestInternetEvent(TbEnum.WIFI))
                }
                isFirst = true
            }
        } else {
            isFirst = false
            EventBus.getDefault().post(RequestInternetEvent(TbEnum.NO_INTERNET))
            when {
                tbNetWorkIsWifi() -> {
                    tbShowToast("当前wifi网络不可用！")
                }
                tbNetWorkIsMobile() -> {
                    tbShowToast("当前移动网络不可用！")
                }
                else -> {
                    tbShowToast("当前网络不可用！")
                }
            }
        }
    }
}
