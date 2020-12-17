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

    fun getUser() {

        viewModelScope.launch {
            startRequestCoroutine<Api>(Api.getData, api = {
                mutableListOf(
                    getUserInfo(
                        mutableMapOf(
                            "merchantId" to "303803010381503",
                            "userName" to "admin",
                            "passWord" to "II1flFeJPSrSYfXfIwO5N8mMPTVdBgD+I2SSp1tsC+6g5Pb55rM9FMUNUow5H8a0e2x9BS5TOYtLBxZw6LMA2NWNgGb7oPxnfm2kpI5WlswvA7tk402qyVTCnhib58tfHZrlNbpqaIYIDM30WIGRquzv5Kz2lWNldS0Z4fjAH64="
                        )
                    )
                )
            })
        }
    }


    override fun tbSpringViewJoinRefresh() {
        super.tbSpringViewJoinRefresh()
        getData()
    }

}