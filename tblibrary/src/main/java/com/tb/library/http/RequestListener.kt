package com.tb.library.http

import org.reactivestreams.Subscription

/**
 * @CreateDate: 2020/3/14 12:06
 * @Description: 网络请求结果回调
 * @Author: TangBo
 */
interface RequestListener {

    fun start(s: Subscription?, taskId: Int)
    fun <T> onNext(t: T, taskId: Int)
    fun onComplete(taskId: Int)
    fun onError(t: Throwable?, taskId: Int)
}