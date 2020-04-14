package com.tb.library.tbExtend

import android.content.Intent
import android.net.Uri
import com.tb.library.base.RegexConfig
import com.tb.library.base.TbApplication
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.experimental.and

/**
 * @CreateDate: 2020/3/7 2:42
 * @Description: 字符扩展
 * @Author: TangBo
 */

/*将文字时间转为毫秒类型*/
fun String?.tbString2Millis(defaultPattern: String = "yyyy-MM-dd HH:mm:ss"): Long {
    if (this.isNullOrEmpty()) return 0
    val l = SimpleDateFormat(defaultPattern, Locale.getDefault()).parse(this) ?: return 0
    return l.time
}

/*小于10转换为例如01,02等结构*/
fun String?.tbStringFormat(): String {
    if (this.isNullOrEmpty()) return ""
    return if (this.toInt() < 10) "0$this" else this
}

/*保留小数后两位*/
fun String?.tbStringSaveTwo(): String {
    if (this.isNullOrEmpty()) return ""
    return String.format("%.2f", this.toDouble())
}

/*保留整数*/
fun String?.tbStringSaveInteger(): String {
    if (this.isNullOrEmpty()) return ""
    return String.format("%.0f", this.toDouble())
}

//去除字符串里面的空格 特殊符
fun String?.tbStringReplaceSpec(): String {
    if (this.isNullOrEmpty()) return ""
    return this.replace("&nbsp;".toRegex(), " ")
}

//使用正则表达式检查邮箱地址格式
fun String?.tbStringCheckEmail(): Boolean {
    if (this.isNullOrEmpty()) return false
    val rule =
        "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?"
    val pattern: Pattern = Pattern.compile(rule)
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}

/*验证电话号码*/
fun String?.tbStringCheckPhone(): Boolean {
    if (this.isNullOrEmpty()) return false
    var myreg =
        Pattern.compile("^0?(13[0-9]|15[0-9]|16[0-9]|18[0-9]|14[0-9]|17[0-9]|19[0-9])[0-9]{8}\$")
    val m = myreg.matcher(this)
    return m.matches()
}

/*字符串反转*/
fun String?.tbStringReverse(): String {
    if (this.isNullOrEmpty()) return ""
    val len = this.length
    if (len <= 1) return this
    val mid = len shr 1
    val chars = this.toCharArray()
    var c: Char
    for (i in 0 until mid) {
        c = chars[i]
        chars[i] = chars[len - i - 1]
        chars[len - i - 1] = c
    }
    return String(chars)
}

//首字母大写(只对字母有效)
fun String?.tbStringFirstLetterUpper(): String {
    if (this.isNullOrEmpty()) return ""
    return (this[0].toInt() - 32).toChar().toString() + this.substring(1)
}

//半角字符与全角字符混乱所致：这种情况一般就是汉字与数字、英文字母混用
fun String?.tbString2DBC(): String {
    if (this.isNullOrEmpty()) return ""
    val chars = this.toCharArray()
    var i = 0
    val len = chars.size
    while (i < len) {
        when {
            chars[i].toInt() == 12288 -> chars[i] = ' '
            chars[i].toInt() in 65281..65374 -> chars[i] = (chars[i].toInt() - 65248).toChar()
            else -> chars[i] = chars[i]
        }
        i++
    }
    return String(chars)
}

/*MD5加密 isUpperCase是否转换为大写*/
fun String?.tbString2Md5(isUpperCase: Boolean = false): String {
    val hexDigits =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    if (this.isNullOrEmpty()) return ""
    var encodeBytes: ByteArray?
    try {
        encodeBytes = MessageDigest.getInstance("MD5").digest(this.toByteArray(charset("UTF-8")))
    } catch (neverHappened: NoSuchAlgorithmException) {
        throw RuntimeException(neverHappened)
    } catch (neverHappened: UnsupportedEncodingException) {
        throw RuntimeException(neverHappened)
    }
    val hex = StringBuilder(encodeBytes.size * 2)
    for (b in encodeBytes) {
        hex.append(hexDigits[(b.toInt() shr 4) and 0x0F])
        hex.append(hexDigits[(b and 0x0F).toInt()])
    }
    return if (isUpperCase) hex.toString().toUpperCase() else hex.toString()
}

/*跳转打电话界面*/
fun String?.tb2PhoneCall() {
    TbApplication.mApplicationContext.startActivity(
        Intent(
            Intent.ACTION_DIAL,
            Uri.parse("tel:${if (this.isNullOrEmpty()) "" else this}")
        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

/*跳转发短信界面*/
fun String?.tb2PhoneMsm(msmConten: String?) {
    val uri = Uri.parse("smsto:" + if (this.isNullOrEmpty()) "" else this)
    val intent = Intent(Intent.ACTION_SENDTO, uri)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.putExtra("sms_body", if (msmConten.isNullOrEmpty()) "" else msmConten)
    TbApplication.mApplicationContext.startActivity(intent)
}

/*验证正则表达式*/
fun String?.tbStringCheckRegex(regexStr:String=RegexConfig.REGEX_PRICE): Boolean {
    if (this.isNullOrEmpty()) return false
    val pattern = Pattern.compile(regexStr) //将给定的正则表达式编译到模式中。
    val isNum = pattern.matcher(this)//创建匹配给定输入与此模式的匹配器。
    return isNum.matches() //如果匹配成功，则可以通过 start、end 和 group 方法获取更多信息.
}
