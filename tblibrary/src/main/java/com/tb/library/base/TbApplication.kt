package com.tb.library.base

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.tb.library.BuildConfig
import com.tb.library.tbReceiver.TbBaseReceiver
import com.tencent.mmkv.MMKV
import okhttp3.Interceptor

/**
 * @CreateDate: 2020/3/7 2:00
 * @Description: TODO
 * @Author: TangBo
 */
open class TbApplication : Application() {

    companion object {
        lateinit var mApplicationContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        mApplicationContext = applicationContext
        ARouter.openDebug()
        ARouter.init(this);
        MMKV.initialize(this)
        initInternetReceiver()
    }


    open fun initInternetReceiver() {
        val receiver = TbBaseReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")//<!--网络状态改变广播-->
        registerReceiver(receiver, intentFilter)
    }

}