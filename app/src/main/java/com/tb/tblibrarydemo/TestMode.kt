package com.tb.tblibrarydemo

import androidx.lifecycle.viewModelScope
import com.tb.library.http.RetrofitApi
import com.tb.library.model.TbBaseModel
import com.tb.library.model.TbCoroutineModel
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch

class TestMode : TbCoroutineModel() {


    fun getData() {
//        val flowable = RetrofitApi.getInstance().getInterface<Api>().getData();
//        startRequest(flowable, Api.getData)

        viewModelScope.launch {
            startRequestCoroutine<Api>(Api.getData, api = {
                mutableListOf(
                    getDataCoroutine()
                )
            })
        }
    }


    override fun tbSpringViewJoinRefresh() {
        super.tbSpringViewJoinRefresh()
        getData()
    }

}