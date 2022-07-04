package com.tb.tblibrarydemo

import android.app.Application
import com.tb.library.base.TbApplication
import com.tb.library.base.TbConfig
import okhttp3.Interceptor


/**
 * @CreateDate: 2020/3/14 13:15
 * @Description: TODO
 * @Author: TangBo
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        TbApplication.init(this)

        TbConfig.getInstance().statusColor = R.color.colorAccent
        TbConfig.getInstance().baseUrl = "http://v.juhe.cn/"//测试服
        TbConfig.getInstance().baseMultiUrl["url_name"] = "https://epay.10010.com/"
        TbConfig.getInstance().successCode = "200"
        TbConfig.getInstance().isDebug = true
        TbConfig.getInstance().setOkHttpClient(
            interceptorList = arrayListOf(Interceptor { chain ->
                val requestHeader = chain.request().newBuilder()
                /*设置具体的header*/
                requestHeader.addHeader("Content-Type", "application/json")
//                requestHeader.addHeader("token", tbGetShared<String>("token"))
                return@Interceptor chain.proceed(requestHeader.build())
            }),
            isHostnameVerifier = true
        )

    }
}