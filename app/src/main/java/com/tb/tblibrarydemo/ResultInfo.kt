package com.tb.tblibrarydemo

import com.google.gson.annotations.SerializedName
import com.tb.library.http.BaseResultInfo
import java.io.Serializable

class ResultInfo<T> : BaseResultInfo<T>(), Serializable {

    @SerializedName("error_code")
    override var code: Any? = null

    @SerializedName("result")
    override var data: T? = null

    @SerializedName("reason")
    override var message: String = ""


}