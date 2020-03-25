package com.tb.library.base

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.tb.library.R
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

/**
 * @CreateDate: 2020/3/5 22:35
 * @Description: 全局参数配置
 * @Author: TangBo
 */
class TbConfig {

    /*创建单例模式*/
    companion object {
        fun getInstance() = Holder.instance
        //RecyclerView分割线样式
        //水平
        const val HORIZONTAL_LIST = RecyclerView.HORIZONTAL
        //垂直
        const val VERTICAL_LIST = RecyclerView.VERTICAL
        //水平+垂直
        const val BOTH_SET = 2

        //eventBus 事假类型和标记
        const val EVENT_FLAG = "event_flag"
        const val EVENT_TYPE = "event_type"
    }

    private object Holder {
        val instance = TbConfig()
    }


    var isDebug: Boolean = true//配置调试模式
    var logTag: String = "tb_log--->"//打印日志头
    lateinit var successCode: Any
    @LayoutRes
    var toastLayoutId: Int = R.layout.tb_toast_style//toast 布局
    @DrawableRes
    var toastBg: Int = R.drawable.tb_bg_toast//toast 背景
    var fontType: String = ""//字体样式设置（文件位于assets/fonts/my_font.ttf）
    var statusColor: Int = R.color.tb_green//初始化顶部状态栏颜色
    var navigationBarColor: Int = R.color.tb_black//初始化底部状态栏
    var titleBackIcon: Int = R.drawable.tb_back_white//初始化title返回键
    var placeholder: Int = 0
    var errorHolder: Int = 0
    @LayoutRes
    var emptyLayoutId: Int = R.layout.tb_load_empty//空 布局
    @LayoutRes
    var errorLayoutId: Int = R.layout.tb_load_error//加载错误 布局
    @LayoutRes
    var loadingLayoutId: Int = R.layout.tb_loading//加载中 布局
    @LayoutRes
    var noInternetLayoutId: Int = R.layout.tb_no_internet//无网络 布局

    var loadingDialogLayoutId: Int = R.layout.tb_loading_dialog//加载弹框 布局
    var loadingStyleId: Int = R.style.dialogProgress//网络加载进度样式

    var screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//屏幕方向

    var maxQuestCount: Long = 6 //同时最大请求数量
    var maxRepeatCount: Int = 3 //最大重连数默认1次
    var repeatDelayTime: Long = 5 //重连间隔默认8秒

    var baseUrl: String = ""

    var okHttpClient: OkHttpClient = OkHttpClient.Builder().build()


    /**
     * 设置OkHttpClient拦截器
     * loggingLevel:打印内容
     * timeOut:链接超时时间
     * headers:添加公共头部信息
     * certificatePinner:添加证书Pinning
     * sslList:本地证书放在Raw文件下面
     * */
    fun setOkHttpClient(
        loggingLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY,
        timeOut: Long = 15,
        isHostnameVerifier: Boolean = false,//是否信任所有服务器地址
        headers: MutableMap<String, String> = mutableMapOf(),
        certificate: MutableMap<String, String> = mutableMapOf(),
        sslList: ArrayList<Int> = arrayListOf()
    ): TbConfig {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        /*设置链接超时*/
        okHttpClientBuilder.connectTimeout(timeOut, TimeUnit.SECONDS)
        /*设置打印等级*/
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = loggingLevel
        okHttpClientBuilder.addNetworkInterceptor(loggingInterceptor)
        //信任所有服务器地址
        okHttpClientBuilder.hostnameVerifier { _, _ ->
            return@hostnameVerifier isHostnameVerifier
        }
        /*添加头部*/
        if (headers.isNotEmpty()) {
            val headerInterceptor = Interceptor { chain ->
                val requestHeader = chain.request().newBuilder()
                /*设置具体的header*/
                headers.forEach {
                    requestHeader.addHeader(it.key, it.value)
                }
                return@Interceptor chain.proceed(requestHeader.build())
            }
            okHttpClientBuilder.addInterceptor(headerInterceptor)
        }
        /*添加证书Pinning*/
        if (certificate.isNotEmpty()) {
            val certificatePinner = CertificatePinner.Builder()
            certificate.forEach {
                certificatePinner.add(it.key, it.value)
            }
            okHttpClientBuilder.certificatePinner(certificatePinner.build())
        }

        /*https绑定证书*/
        if (sslList.isNotEmpty()) {
            okHttpClientBuilder.sslSocketFactory(
                getInstance().getSSLSocketFactory(
                    TbApplication.mApplicationContext,
                    sslList
                )
            )
        }
        this.okHttpClient = okHttpClientBuilder.build()

        return this
    }

    private fun getSSLSocketFactory(context: Context, sslList: ArrayList<Int>): SSLSocketFactory {
        //CertificateFactory用来证书生成
        val certificateFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
        //Create a KeyStore containing our trusted CAs
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        //读取本地证书
        sslList.forEach {
            val input = context.resources.openRawResource(it)
            keyStore.setCertificateEntry(
                it.toString(),
                certificateFactory.generateCertificate(input)
            )
            input.close()
        }
        //Create a TrustManager that trusts the CAs in our keyStore
        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)
        //Create an SSLContext that uses our TrustManager
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagerFactory.trustManagers, SecureRandom())

        return sslContext.socketFactory
    }

}