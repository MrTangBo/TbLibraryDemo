package com.tb.library.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.ConstructorConstructor
import com.google.gson.internal.ObjectConstructor
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.lang.reflect.ParameterizedType
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

    /*本地使用*/
    val mLocalGson: Gson = GsonBuilder().serializeNulls().disableHtmlEscaping().create()

    /*处理网络请求null字符*/
    var mGson: Gson =
        GsonBuilder().serializeNulls().disableHtmlEscaping().registerTypeAdapterFactory(object :
            TypeAdapterFactory {
            override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
                val rawType = type.rawType
                return when {
                    rawType == String::class.java -> CustomTypeAdapter(String::class.java) as TypeAdapter<T>
                    rawType.simpleName == "Integer" -> CustomTypeAdapter(Int::class.java) as TypeAdapter<T>
                    rawType.simpleName == "Float"-> CustomTypeAdapter(Float::class.java) as TypeAdapter<T>
                    rawType .simpleName== "Long"-> CustomTypeAdapter(Long::class.java) as TypeAdapter<T>
                    rawType.simpleName == "Boolean" -> CustomTypeAdapter(Boolean::class.java) as TypeAdapter<T>
                    Collection::class.java.isAssignableFrom(rawType) -> {
                        //获取底层实例
                        val tokenType: Type = type.type
                        //获取此集合内的元素类型
                        val elementType: Type =
                            `$Gson$Types`.getCollectionElementType(tokenType, type.rawType)
                        //生成该元素类型的TypeAdapter
                        val elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType))
                        CollectionTypeAdapter(elementTypeAdapter, type) as TypeAdapter<T>
                    }
                    else -> null
                }
            }
        }).create()

    //转换为字符串
    inline fun <reified T> toJson(t: T): String = mLocalGson.toJson(t)

    //转换为对象
    inline fun <reified T> fromJson(t: String): T = mLocalGson.fromJson(t, T::class.java)

    //转换为集合
    inline fun <reified T> fromJsonList(t: String): T {
        return mLocalGson.fromJson(t, object : TypeToken<T>() {}.type)
    }
}

class CustomTypeAdapter<T>(var clazz: Class<T>) : TypeAdapter<T>() {
    override fun write(writer: JsonWriter, value: T?) {
        if (value == null || value == "null") {
            when (clazz) {
                String::class.java -> writer.value("")
                Int::class.java -> writer.value(0)
                Float::class.java -> writer.value(0f)
                Long::class.java -> writer.value(0L)
                Boolean::class.java -> writer.value(false)
            }
        } else {
            when (clazz) {
                String::class.java -> writer.value(value.toString())
                Int::class.java -> writer.value(value.toString().toInt())
                Float::class.java -> writer.value(value.toString().toFloat())
                Long::class.java -> writer.value(value.toString().toLong())
                Boolean::class.java -> writer.value(value.toString().toBoolean())
            }
        }
    }

    override fun read(reader: JsonReader): T {
        return when (reader.peek()) {
            JsonToken.NULL -> {
                reader.nextNull()
                when (clazz) {
                    String::class.java -> "" as T
                    Int::class.java -> 0 as T
                    Float::class.java -> 0F as T
                    Long::class.java -> 0L as T
                    Boolean::class.java -> false as T
                    else -> null as T
                }
            }
            else -> {
                when (clazz) {
                    String::class.java -> reader.nextString() as T
                    Int::class.java -> reader.nextInt() as T
                    Float::class.java -> reader.nextDouble() as T
                    Long::class.java -> reader.nextLong() as T
                    Boolean::class.java -> reader.nextBoolean() as T
                    else -> "" as T
                }
            }
        }
    }
}


/*处理List为null*/
class CollectionTypeAdapter<T, E>(
    private var elementTypeAdapter: TypeAdapter<T>, var typeToken: TypeToken<E>
) : TypeAdapter<Collection<T>>() {
    override fun write(out: JsonWriter, value: Collection<T>?) {
        if (value == null) {
            out.nullValue()
            return
        }
        out.beginArray()
        for (element in value) {
            elementTypeAdapter.write(out, element)
        }
        out.endArray()
    }

    override fun read(jsonReader: JsonReader): Collection<T> {
        val constructorConstructor = ConstructorConstructor(HashMap())
        val constructor = constructorConstructor.get(typeToken) as ObjectConstructor<out Collection<T>>
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull()
            return constructor.construct()
        }
        val collection: MutableCollection<T> = constructor.construct() as MutableCollection<T>
        jsonReader.beginArray()
        while (jsonReader.hasNext()) {
            val instance: T = elementTypeAdapter.read(jsonReader)
            collection.add(instance)
        }
        jsonReader.endArray()
        return collection
    }
}


