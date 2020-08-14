package com.tb.tblibrarydemo.koin

import android.content.Context
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Created by Tb on 2020/8/14.
 * describe:
 */

val testModule = module {
    single { (context: Context, name: String) -> Person(context, name) }
    viewModel { (context: Context)->MyViewModel(context) }
}

