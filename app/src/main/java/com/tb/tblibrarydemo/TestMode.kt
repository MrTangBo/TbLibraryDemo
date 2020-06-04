package com.tb.tblibrarydemo

import com.tb.library.http.RetrofitApi
import com.tb.library.model.TbBaseModel
import com.tb.library.model.TbCoroutineModel
import io.reactivex.disposables.Disposable

class TestMode : TbCoroutineModel() {


    fun getData() {
        val flowable = RetrofitApi.getInstance().getInterface<Api>().getData();
        startRequest(flowable, Api.getData)
    }


    override fun tbSpringViewJoinRefresh() {
        super.tbSpringViewJoinRefresh()
        getData()
    }

}