package com.tb.library.http


import com.tb.library.base.TbConfig
import com.tb.library.util.GsonUtil
import com.tb.library.util.TbLogUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 *@作者：tb
 *@时间：2019/6/28
 *@描述：构建Retrofit类 单例模式
 */
class RetrofitApi {

    companion object {
        fun getInstance() = Holder.instance
    }

    private object Holder {
        val instance = RetrofitApi()
    }

    inline fun <reified T> getInterface(
        converterFactory: Converter.Factory = GsonConverterFactory.create(GsonUtil.getInstance().mGson),
        baseUrl: String = TbConfig.getInstance().baseUrl
    ): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(TbConfig.getInstance().okHttpClient.build())
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit.create(T::class.java)
    }
}