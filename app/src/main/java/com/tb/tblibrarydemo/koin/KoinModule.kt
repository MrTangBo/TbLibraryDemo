package com.tb.tblibrarydemo.koin

import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Created by Tb on 2020/8/17.
 * describe:
 */
@Suppress("CAST_NEVER_SUCCEEDS")
object KoinModule {

    val baseModule = module {

        factory(named("list")) { arrayListOf<Any>() }
    }

}


