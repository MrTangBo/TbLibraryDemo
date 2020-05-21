package com.tb.library.base

/**
 *@作者：tb
 *@时间：2019/7/4
 *@描述：
 */
enum class TbEnum {
    WIFI, MOBILE,NO_INTERNET
}

open class TbEventBusInfo()

/*网络重新连接*/
class RequestInternetEvent(var internetType: TbEnum) : TbEventBusInfo()
