package com.tb.tblibrarydemo.koin

import com.tb.library.tbExtend.tbShowToast

/**
 * Created by Tb on 2020/8/17.
 * describe:
 */
open class KoinChildModel : KoinModel() {


    override fun test() {
        tbShowToast("这是儿子0")
    }


}