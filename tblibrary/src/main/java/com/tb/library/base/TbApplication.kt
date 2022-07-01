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

    fun init(context: Application) {
        mApplicationContext = context.applicationContext
        ARouter.openDebug()
        ARouter.init(context);
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