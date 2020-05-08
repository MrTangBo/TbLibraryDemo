package com.tb.tblibrarydemo

import com.tb.library.model.TbBaseModel

class TestMode : TbBaseModel() {


    fun getData() {
//        val flowable = RetrofitApi.getInstance().getInterface<Api>().getData();
//        startRequest(flowable, Api.getData)
    }


    override fun tbSpringViewJoinRefresh() {
        super.tbSpringViewJoinRefresh()
        getData()
    }

}
