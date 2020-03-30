package com.tb.library.util

import android.util.Base64
import com.tb.library.tbExtend.tbString2Md5
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

/**
 *@作者：tb
 *@时间：2019/7/2
 *@描述：
 */

object AESSecurity {

    var publicKey = "www.winpower.com"

    /*生成key*/
    // 获得一个key生成器（AES加密模式）
    // 设置密匙长度128位
    // 获得密匙
    // 返回密匙的byte数组供加解密使用
    val key: String
        get() {
            try {
                val keyGen = KeyGenerator.getInstance("AES")
                keyGen.init(256)
                val secretKey = keyGen.generateKey()
                val raw = secretKey.encoded
                return raw.toString().tbString2Md5().substring(8, 24)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            return ""
        }

    /*数据加密*/
    fun encrypt(key: String, data: String): String {  // 加密
        val keySpec = SecretKeySpec(key.toByteArray(), "AES") // 根据上一步生成的密匙指定一个密匙（密匙二次加密？）
        try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")// 获得Cypher实例对象
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)  // 初始化模式为加密模式，并指定密匙
            val encode = cipher.doFinal(data.toByteArray())  // 执行加密操作。 input为需要加密的byte数组
            return Base64.encodeToString(encode, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    /*数据解密*/
    fun decrypt(key: String, data: String): String { // 解密
        val keySpec = SecretKeySpec(key.toByteArray(), "AES") // 根据上一步生成的密匙指定一个密匙（密匙二次加密？）
        try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")// 获得Cypher实例对象
            cipher.init(Cipher.DECRYPT_MODE, keySpec)  // 初始化模式为加密模式，并指定密匙
            val r = Base64.decode(data.toByteArray(), Base64.DEFAULT)
            val encode = cipher.doFinal(r)  // 执行加密操作。 input为需要加密的byte数组
            return String(encode)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }
}
