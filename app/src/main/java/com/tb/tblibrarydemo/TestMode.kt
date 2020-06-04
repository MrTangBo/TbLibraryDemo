package com.tb.tblibrarydemo

import androidx.lifecycle.viewModelScope
import com.tb.library.model.TbCoroutineModel
import kotlinx.coroutines.launch

class TestMode : TbCoroutineModel() {

    fun getData() {
        viewModelScope.launch {
            startRequestCoroutine<Api>(Api.getData) {
                mutableListOf(getData())
            }
        }

//    fun getData() {
//        val flowable = RetrofitApi.getInstance().getInterface<Api>().getData();
//        startRequest(flowable, Api.getData)
//    }


//    override fun tbSpringViewJoinRefresh() {
//        super.tbSpringViewJoinRefresh()
//        getData()
//    }


    }
}