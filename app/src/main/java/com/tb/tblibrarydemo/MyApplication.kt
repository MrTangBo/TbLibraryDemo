package com.tb.tblibrarydemo

import com.tb.library.base.TbApplication
import com.tb.library.base.TbConfig
import com.tb.library.tbExtend.tbGetShared

/**
 * @CreateDate: 2020/3/14 13:15
 * @Description: TODO
 * @Author: TangBo
 */
class MyApplication : TbApplication() {

    override fun onCreate() {
        super.onCreate()

        TbConfig.getInstance().statusColor = R.color.colorAccent
        TbConfig.getInstance().baseUrl = "http://192.168.5.199:8070/"//测试服
        TbConfig.getInstance().setOkHttpClient(
            headers = mutableMapOf(
                "Content-Type" to "application/json",
                "language" to "cn",
                "token" to tbGetShared<String>("token")
            )
        )
        TbConfig.getInstance().successCode = 200.0
    }
}