package com.tb.tblibrarydemo

import com.tb.library.base.TbApplication
import com.tb.library.base.TbConfig

/**
 * @CreateDate: 2020/3/14 13:15
 * @Description: TODO
 * @Author: TangBo
 */
class MyApplication : TbApplication() {


    override fun onCreate() {
        super.onCreate()
        TbConfig.getInstance().baseUrl = "http://v.juhe.cn/"
        TbConfig.getInstance().successCode = 0.0
        TbConfig.getInstance().statusColor=R.color.colorAccent
    }
}