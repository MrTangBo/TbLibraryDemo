package com.tb.tblibrarydemo

data class TestBean(
    val `data`: List<Data>
)

data class Data(
    val content: String,
    val hashId: String,
    val unixtime: Int,
    val updatetime: String
)