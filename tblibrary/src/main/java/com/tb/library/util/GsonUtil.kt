package com.tb.library.util

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 *@作者：tb
 *@时间：2019/6/28
 *@描述：Gson工具类
 */
class GsonUtil {

    companion object {
        fun getInstance() = Holder.instance
    }

    private object Holder {
        val instance = GsonUtil()
    }

    /**/
    var mGson = GsonBuilder().serializeNulls().disableHtmlEscaping().create()

    //转换为字符串
    inline fun <reified T> toJson(t: T): String = mGson.toJson(t)

    //转换为对象
    inline fun <reified T> fromJson(t: String): T = mGson.fromJson(t, T::class.java)

    //转换为集合
    inline fun <reified T> fromJsonList(t: String): T {
        return mGson.fromJson(t, object : TypeToken<T>() {}.type)
    }
}