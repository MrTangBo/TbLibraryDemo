package com.tb.tblibrarydemo

import com.tb.tblibrarydemo.ResultInfo
import com.tb.tblibrarydemo.TestBean
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

//http://v.juhe.cn/joke/content/list.php?key=c9784b2d8e15eaae0798d3696de1cbd2&page=2&pagesize=10&sort=asc&time=1418745237
interface Api {
    companion object {
        const val firstId = 0
    }

    @GET("/joke/content/list.php")
    fun getData(
        @Query("@Query") sort: String = "asc", @Query("time") time: String = "1418745237", @Query("key") key: String = "c9784b2d8e15eaae0798d3696de1cbd2"
    ): Flowable<ResultInfo<TestBean>>



}