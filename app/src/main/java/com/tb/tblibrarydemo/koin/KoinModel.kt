package com.tb.tblibrarydemo.koin

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.tb.library.tbExtend.tbShowToast

/**
 * Created by Tb on 2020/8/17.
 * describe:
 */
open class KoinModel : ViewModel(),LifecycleObserver {


  open  fun test(){
        tbShowToast("sadada")
    }


}