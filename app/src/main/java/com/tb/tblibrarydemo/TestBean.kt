package com.tb.tblibrarydemo


data class TestBean(
    var `data`: List<Data> = listOf()
)

data class Data(
    var content: String = "",
    var hashId: String = "",
    var unixtime: Int = 0,
    var updatetime: String = ""
)