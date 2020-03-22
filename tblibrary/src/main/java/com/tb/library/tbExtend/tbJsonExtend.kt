package com.tb.library.tbExtend

import com.google.gson.reflect.TypeToken
import com.tb.library.util.GsonUtil

/**
 * @CreateDate: 2020/3/7 3:04
 * @Description: json相关亏站
 * @Author: TangBo
 */

/*json字符串转换位对象*/
inline fun <reified T> String?.tb2Object(): T? {
    if (this.isNullOrEmpty()) return null
    return if (this.startsWith("{")) {
        GsonUtil.getInstance().fromJson(this)
    } else {
        null
    }
}

/*转换object为json字符串*/
fun Any?.tb2Json(): String {
    if (this == null) return ""
    return GsonUtil.getInstance().toJson(this)
}

/*json字符串转换为集合List*/
inline fun <reified T> String?.tb2List(): T? {
    if (this.isNullOrEmpty()) return null
    return GsonUtil.getInstance().fromJsonList(this)
}
