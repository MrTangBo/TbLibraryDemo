package com.tb.tblibrarydemo.koin

import android.content.Context
import com.tb.library.tbExtend.tbShowToast
import com.tb.tblibrarydemo.TestActivity

/**
 * Created by Tb on 2020/8/14.
 * describe:
 */
class Person(var context: Context, var name: String) {
    fun eat() {
        tbShowToast("吃东西了${name}。。。${(context as TestActivity)}")
    }
}
