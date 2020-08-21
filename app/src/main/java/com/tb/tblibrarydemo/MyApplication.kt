package com.tb.tblibrarydemo

import com.tb.library.base.TbApplication
import com.tb.library.base.TbConfig
import com.tb.tblibrarydemo.koin.*
import com.tb.tblibrarydemo.koin.KoinModule.baseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


/**
 * @CreateDate: 2020/3/14 13:15
 * @Description: TODO
 * @Author: TangBo
 */
class MyApplication : TbApplication() {

    override fun onCreate() {
        super.onCreate()

        TbConfig.getInstance().statusColor = R.color.colorAccent
        TbConfig.getInstance().baseUrl = "http://v.juhe.cn/"//测试服
        TbConfig.getInstance().successCode = "Success"

        TbConfig.getInstance().isDebug = true
        TbConfig.getInstance().setOkHttpClient()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            androidFileProperties()
            modules(baseModule)
        }
    }
}