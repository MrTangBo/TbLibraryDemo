package com.tb.tblibrarydemo
import com.tb.library.http.RetrofitApi
import com.tb.library.model.TbBaseModel
import io.reactivex.disposables.Disposable

class TestMode : TbBaseModel() {

    var ds: Disposable? = null

    fun getData() {
//        val flowable = RetrofitApi.getInstance().getInterface<Api>().getEmail();
//        startRequest(taskId = Api.getData, flowables = arrayListOf(flowable,flowable))

        mRequestMap.add(
            mutableMapOf(
                "account" to "e609968039"
            )
        )
        val mLoginApi = RetrofitApi.getInstance()
            .getInterface<Api>()
            .getEmail(mRequestMap[0])
        startRequest(mLoginApi, 200)
    }

    override fun tbOnRefresh() {
        super.tbOnRefresh()
        getData()
    }

    override fun tbLoadMore() {
        super.tbLoadMore()
        getData()
    }

    override fun dropView() {
        super.dropView()
        ds?.dispose()
    }
}