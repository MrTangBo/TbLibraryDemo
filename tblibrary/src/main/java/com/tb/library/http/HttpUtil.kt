package com.tb.library.http

import android.util.Log
import com.tb.library.base.TbConfig
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * @CreateDate: 2020/3/14 12:13
 * @Description: TODO
 * @Author: TangBo
 */
class HttpUtil {

    companion object {
        fun getInstance() = Holder.instance
    }

    private object Holder {
        val instance = HttpUtil()
    }

    fun startRequest(
        flowables: MutableList<Flowable<*>>,
        requestListener: RequestListener,
        taskId: Int
    ) {
        var retryCount = 0//当前重连次数
        Flowable.concat(flowables)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retryWhen {
                return@retryWhen it.flatMap { throwable ->
                    if (throwable is IOException) {
                        if (++retryCount <= TbConfig.getInstance().maxRepeatCount) {
                            Log.i("startRequest-->", "重连第$retryCount 次")
                            Flowable.timer(TbConfig.getInstance().repeatDelayTime, TimeUnit.SECONDS)
                        } else {
                            Flowable.error(throwable)
                        }
                    } else {
                        Flowable.error(throwable)
                    }
                }
            }.subscribe(object : Subscriber<Any> {
                override fun onComplete() {
                    requestListener.onComplete(taskId)
                }

                override fun onSubscribe(s: Subscription?) {
                    s?.request(TbConfig.getInstance().maxQuestCount)
                    requestListener.start(s, taskId)
                }

                override fun onNext(t: Any) {
                    requestListener.onNext(t, taskId)
                }

                override fun onError(t: Throwable?) {
                    requestListener.onError(t, taskId)
                }

            })


    }

}