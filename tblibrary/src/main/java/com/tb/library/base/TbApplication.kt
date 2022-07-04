package com.tb.library.base

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import com.alibaba.android.arouter.launcher.ARouter
import com.tb.library.tbReceiver.TbBaseReceiver
import com.tencent.mmkv.MMKV

/**
 * @CreateDate: 2020/3/7 2:00
 * @Description: TODO
 * @Author: TangBo
 */

object TbApplication {
    lateinit var mApplicationContext: Context

    /**
     *
     * @param context Application
     * @param isOpenARouterDebug Boolean 是否开启ARouter的Debug模式
     * @param isEnableARouter Boolean 是否开启ARouter
     */

    fun init(
        context: Application,
        isOpenARouterDebug: Boolean = true,
        isEnableARouter: Boolean = false
    ) {
        mApplicationContext = context.applicationContext
        if (isEnableARouter) {
            ARouter.init(context)
            if (isOpenARouterDebug) {
                ARouter.openDebug()
            }
        }
        MMKV.initialize(context)
        initInternetReceiver()
    }

    private fun initInternetReceiver() {
        val receiver = TbBaseReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")//<!--网络状态改变广播-->
        mApplicationContext.registerReceiver(receiver, intentFilter)
    }

}