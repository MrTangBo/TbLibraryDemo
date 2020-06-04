package com.tb.library.tbExtend

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 *@作者：tb
 *@时间：2019/6/27
 *@描述：自定义扩展方法
 */

/*********extra类的扩展*********/

/*将毫秒时间戳转为时间字符串*/
fun Long?.tbMillis2String(defaultPattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    if (this == null) return ""
    return SimpleDateFormat(defaultPattern, Locale.getDefault()).format(Date(this))
}

/*将时间戳转为Date类型*/
fun Long?.tbMillis2Date(): Date? {
    if (this == null) return null
    return Date(this)
}

/**
 * 获取两个时间的时间差
 */
fun Long?.tbGetTimeSpan(
    millis: Long,
    unitLen: MutableList<Long> = mutableListOf(86400000, 3600000, 60000, 1000, 1),
    units: MutableList<String> = mutableListOf("天", "小时", "分钟", "秒", "毫秒")
): String {
    if (this == null) {
        return "0"
    }
    var span = abs(this - millis)
    if (span == 0L) return "0"
    val sb = StringBuilder()
    for (i in 0 until unitLen.size) {
        val mode = span / unitLen[i]
        span -= mode * unitLen[i]
        sb.append(mode).append(units[i])
    }
    return sb.toString()
}

/*将时间戳转为 月 天 时 分 秒*/
@Suppress("UNREACHABLE_CODE")
fun Long?.tbMillis2Time(unit: TimeUnit): String {
    if (this == null) return ""
    return return when (unit) {
        TimeUnit.MILLISECONDS -> {
            this.toString().plus("毫秒")
        }
        TimeUnit.SECONDS -> {
            (this / 1000).toString().plus("秒")
        }
        TimeUnit.MINUTES -> {
            (this / 60000).toString().plus("分钟")
        }
        TimeUnit.HOURS -> {
            (this / 3600000).toString().plus("小时")
        }
        TimeUnit.DAYS -> {
            (this / 86400000).toString().plus("天")
        }
        else -> ""
    }
}

/*将Date转为时间戳类型*/
fun Date.tbDate2Millis(): Long {
    return this.time
}

/**
 *@作者：tb
 *@时间：2019/12/11
 *@描述：转换时间为00：00：00
 */
fun tbStringForTime(timeMs: Long): String {
    if (timeMs <= 0) {
        return "00:00"
    }
    val totalSeconds = timeMs / 1000
    val seconds = (totalSeconds % 60).toInt()
    val minutes = (totalSeconds / 60 % 60).toInt()
    val hours = (totalSeconds / 3600).toInt()
    val stringBuilder = StringBuilder()
    val mFormatter = Formatter(stringBuilder, Locale.getDefault())
    return if (hours > 0) {
        mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
    } else {
        mFormatter.format("%02d:%02d", minutes, seconds).toString()
    }
}

























