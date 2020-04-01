package com.tb.tblibrarydemo

import com.google.gson.annotations.SerializedName
import com.tb.library.http.BaseResultInfo
import java.io.Serializable

class ResultInfo<T> : BaseResultInfo<T>(), Serializable {

    @SerializedName("error_code")
    override var mCode: Any? = 0

    @SerializedName("result")
    override var mData: T? = null

    @SerializedName("reason")
    override var mMessage: String = ""




}