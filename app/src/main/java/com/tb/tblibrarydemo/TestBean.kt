package com.tb.tblibrarydemo

import java.io.Serializable


data class TestBean(
    var `data`: List<Data> = listOf()
):Serializable

data class Data(
    var content: String = "",
    var hashId: String = "",
    var unixtime: Int = 0,
    var updatetime: String = ""
):Serializable