package com.tb.tblibrarydemo

import com.tb.tblibrarydemo.ResultInfo
import com.tb.tblibrarydemo.TestBean
import io.reactivex.Flowable
import okhttp3.RequestBody
import retrofit2.http.*

//http://v.juhe.cn/joke/content/list.php?key=c9784b2d8e15eaae0798d3696de1cbd2&page=2&pagesize=10&sort=asc&time=1418745237
interface Api {
    companion object {
        const val getData = 0
    }


    @GET("/joke/content/list.php")
    suspend fun getDataCoroutine(
        @Query("@Query") sort: String = "asc",
        @Query("time") time: String = "1418745237",
        @Query("key") key: String = "c9784b2d8e15eaae0798d3696de1cbd2"
    ): ResultInfo<TestBean>

    @GET("/joke/content/list.php")
    fun getData(
        @Query("@Query") sort: String = "asc",
        @Query("time") time: String = "1418745237",
        @Query("key") key: String = "c9784b2d8e15eaae0798d3696de1cbd2"
    ): Flowable<ResultInfo<TestBean>>


    @POST("storage/file_01")
    @Multipart
    fun uploadBuyImage(@PartMap map: @JvmSuppressWildcards Map<String, RequestBody>): @JvmSuppressWildcards Flowable<*>


    /*获取验证邮箱*/
//    @POST("user/getUserInfo")
//    fun getUserInfo(): Flowable<ResultInfo<PasswordChangeStepOneInfo>>

}