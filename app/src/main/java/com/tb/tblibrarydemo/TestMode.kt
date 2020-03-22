package com.tb.tblibrarydemo
import com.tb.library.http.RetrofitApi
import com.tb.library.model.TbBaseModel
import io.reactivex.disposables.Disposable

class TestMode : TbBaseModel() {

    var ds: Disposable? = null

    fun getData() {
        val flowable = RetrofitApi.getInstance().getInterface<Api>().getData();
        startRequest(taskId = Api.firstId, flowables = arrayListOf(flowable))
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