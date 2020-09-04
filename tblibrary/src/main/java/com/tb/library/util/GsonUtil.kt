package com.tb.library.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
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

    /*处理null字符*/
    var mGson: Gson = GsonBuilder().serializeNulls().disableHtmlEscaping().registerTypeAdapterFactory(object :
        TypeAdapterFactory {
        override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
            val clz = type.rawType as Class<*>
            return when (clz) {
                String::class.java -> {
                    object : TypeAdapter<String>() {
                        override fun write(writer: JsonWriter, value: String?) {
                            if (value == null) {
                                writer.value("")
                            } else
                                writer.value(value)
                        }
                        override fun read(reader: JsonReader): String {
                            return when (reader.peek()) {
                                JsonToken.NULL -> {
                                    reader.nextNull()
                                    ""
                                }
                                else -> {
                                    val str = reader.nextString()
                                    if (str.isEmpty()) "" else str
                                }
                            }

                        }
                    } as TypeAdapter<T>
                }
                else -> null
            }
        }
    }).create()

    //转换为字符串
    inline fun <reified T> toJson(t: T): String = mGson.toJson(t)

    //转换为对象
    inline fun <reified T> fromJson(t: String): T = mGson.fromJson(t, T::class.java)

    //转换为集合
    inline fun <reified T> fromJsonList(t: String): T {
        return mGson.fromJson(t, object : TypeToken<T>() {}.type)
    }
}