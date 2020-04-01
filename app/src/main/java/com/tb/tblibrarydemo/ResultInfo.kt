package com.tb.tblibrarydemo

import com.google.gson.annotations.SerializedName
import com.tb.library.http.BaseResultInfo
import java.io.Serializable

class ResultInfo<T> : BaseResultInfo<T>(), Serializable {

    @SerializedName("msgCode")
    override var mCode: Any? = 0

    @SerializedName("data")
    override var mData: T? = null

    @SerializedName("msgContent")
    override var mMessage: String = ""

    var result: String = ""

    var otherCode: Int = 0


}