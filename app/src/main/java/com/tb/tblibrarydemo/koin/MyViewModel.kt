package com.tb.tblibrarydemo.koin

import android.content.Context
import androidx.lifecycle.ViewModel
import com.tb.library.tbExtend.tbShowToast
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

/**
 * Created by Tb on 2020/8/14.
 * describe:
 */

class MyViewModel(var context: Context) : ViewModel() , KoinComponent {

    val person :Person  by inject { parametersOf(context,"蓝调989898") }


    fun testViewModel() :Person{


        return person
    }

}