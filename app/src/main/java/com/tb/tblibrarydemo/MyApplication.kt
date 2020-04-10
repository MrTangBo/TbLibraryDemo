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
        TbConfig.getInstance().baseUrl = "http://v.juhe.cn/"//测试服
        TbConfig.getInstance().successCode ="Success"
    }
}