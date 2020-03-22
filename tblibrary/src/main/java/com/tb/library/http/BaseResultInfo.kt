package com.tb.library.http

import java.io.Serializable
/**
 * @CreateDate: 2020/3/12 1:12
 * @Description: TODO
 * @Author: TangBo
 */
open class BaseResultInfo<T> : Serializable {

    open var code: Any? = null

    open var data: T? = null

    open var message: String = ""


}