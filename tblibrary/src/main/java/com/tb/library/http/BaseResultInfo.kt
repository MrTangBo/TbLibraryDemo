package com.tb.library.http

import java.io.Serializable
/**
 * @CreateDate: 2020/3/12 1:12
 * @Description: TODO
 * @Author: TangBo
 */
open class BaseResultInfo<T> : Serializable {

    open var mCode: Any? = null

    open var mData: T? = null

    open var mMessage: String = ""


}