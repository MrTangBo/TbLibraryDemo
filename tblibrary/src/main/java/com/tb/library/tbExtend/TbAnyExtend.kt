package com.tb.library.tbExtend

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tb.library.R
import com.tb.library.base.TbApplication
import com.tb.library.base.TbConfig
import com.tb.library.tbDialog.TbSureDialog
import com.tb.library.util.ActivityManagerUtil
import com.tb.library.util.TbLogUtils
import com.tb.library.tbEntity.TbApkInfo
import java.io.Serializable


/**
 *@作者：tb
 *@时间：2019/6/27
 *@描述：自定义扩展方法
 */

/*保存缓存MMKV*/
fun Any?.tbSetShared(key: String, isClean: Boolean = true) {
    if (this == null) return
    val share = if (isClean) tBMMKV_C else tBMMKV
    when (this) {
        is String -> share.encode(key, this)
        is Int -> share.encode(key, this)
        is Float -> share.encode(key, this)
        is Long -> share.encode(key, this)
        is Boolean -> share.encode(key, this)
    }
}

/*获取String缓存MMKV*/
@Suppress("UNCHECKED_CAST")
inline fun <reified T> Any.tbGetShared(
    key: String,
    isClean: Boolean = true
): T {
    val share = if (isClean) tBMMKV_C else tBMMKV
    return when (T::class) {
        String::class -> share.decodeString(key, "") as T
        Int::class -> share.decodeInt(key, 0) as T
        Float::class -> share.decodeFloat(key, 0f) as T
        Long::class -> share.decodeLong(key, 0L) as T
        Boolean::class -> share.decodeBool(key, false) as T
        else -> (share.decodeString(key, "") as String).tb2Object<T>()!!
    }
}

/*清除MMKV*/
fun Any.tbCleanShared() {
    tBMMKV_C.clear()
}

/*扩展Toast土司*/
fun Any.tbShowToast(
    msg: CharSequence,
    gravity: Int = Gravity.BOTTOM,
    @DrawableRes background: Int = TbConfig.getInstance().toastBg,
    @LayoutRes layoutId: Int = TbConfig.getInstance().toastLayoutId
) {
    val mToast = Toast(TbApplication.mApplicationContext)
    val view = LayoutInflater.from(TbApplication.mApplicationContext).inflate(layoutId, null)
    val toastBackground = view.findViewById<LinearLayout>(R.id.toastLinear)
    toastBackground.background =
        ContextCompat.getDrawable(TbApplication.mApplicationContext, background)
    val textView = view.findViewById<TextView>(R.id.toast_text)
    textView.text = msg
    mToast.duration = Toast.LENGTH_SHORT
    mToast.setGravity(gravity, 0, tbGetDimensValue(R.dimen.x80))
    mToast.view = view
    mToast.show()
}

/* 添加activity*/
fun Activity.tbAddActivity() {
    ActivityManagerUtil.getInstance().addActivity(this)
}

/*关闭指定的Activity*/
fun Any.tbRemoveActivity(activityName: String) {
    ActivityManagerUtil.getInstance().clearOther(activityName)
}

/*将activity全部关闭掉*/
fun Any.tbCleanAllActivity() {
    ActivityManagerUtil.getInstance().clearAll()
}

/*获取手机唯一标识MEID*/
@SuppressLint("HardwareIds")
fun Any.tbGetPhoneOnlyNum(): String {
    val androidID =
        Settings.System.getString(
            TbApplication.mApplicationContext.contentResolver,
            Settings.System.ANDROID_ID
        )
    val serialNumber = Build.SERIAL
    val id = androidID + serialNumber
    return id.tbString2Md5()
}


/*网络是否可用*/
fun Any.tbNetWorkIsConnect(): Boolean {
    val mConnectManager: ConnectivityManager =
        TbApplication.mApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netWorkInfo = mConnectManager.activeNetworkInfo
    if (netWorkInfo != null && netWorkInfo.isConnected) {
        return (netWorkInfo.state == NetworkInfo.State.CONNECTED)
    }
    return false
}

/* 判断当前网络类型*/
fun Any.tbNetWorkIsWifi(): Boolean {
    val mConnectManager: ConnectivityManager =
        TbApplication.mApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netWorkInfo = mConnectManager.activeNetworkInfo
    if (netWorkInfo != null && netWorkInfo.isConnected) {
        return netWorkInfo.type == ConnectivityManager.TYPE_WIFI
    }
    return false
}

/* 判断当前网络类型*/
fun Any.tbNetWorkIsMobile(): Boolean {
    val mConnectManager: ConnectivityManager =
        TbApplication.mApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netWorkInfo = mConnectManager.activeNetworkInfo
    if (netWorkInfo != null && netWorkInfo.isConnected) {
        return netWorkInfo.type == ConnectivityManager.TYPE_MOBILE
    }
    return false
}

/*获取状态栏和导航栏的高度*/
@SuppressLint("PrivateApi")
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
fun Any.tbStatusBarHeight(): IntArray {
    val array: IntArray = IntArray(2)
    var statusBarHeight = -1
    var navigationbarHeight = -1
    val resources: Resources = TbApplication.mApplicationContext.resources
    try {
        val clazz = Class.forName("com.android.internal.R\$dimen")
        val any = clazz.newInstance()
        val height = Integer.parseInt(clazz.getField("status_bar_height").get(any).toString())
        statusBarHeight = resources.getDimensionPixelSize(height)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    val resIdShow = resources.getIdentifier("config_showNavigationBar", "bool", "android")
    var hasNavigationBar = false
    if (resIdShow > 0) {
        hasNavigationBar = resources.getBoolean(resIdShow)//是否显示底部navigationBar
    }
    if (hasNavigationBar) {
        val resIdNavigationBar =
            resources.getIdentifier("navigation_bar_height", "dimen", "android")
        navigationbarHeight = 0
        if (resIdNavigationBar > 0) {
            navigationbarHeight =
                resources.getDimensionPixelSize(resIdNavigationBar)//navigationBar高度
        }
    }
    array[0] = statusBarHeight
    array[1] = navigationbarHeight
    return array
}

/*获取通知栏权限并设置*/
fun Any.tbNotifyEnabled(
    activity: AppCompatActivity? = null,
    messageTx: String = TbApplication.mApplicationContext.resources.getString(R.string.notifyMark)
): Boolean {
    val appInfo = TbApplication.mApplicationContext.applicationInfo
    val pkg = TbApplication.mApplicationContext.applicationContext.packageName
    val uid = appInfo.uid
    if (!NotificationManagerCompat.from(TbApplication.mApplicationContext).areNotificationsEnabled()) {
        if (activity != null) {
            val sureDialog = TbSureDialog(messageTx = messageTx)
            sureDialog.sureClick = {
                val localIntent = Intent()
                localIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//>=8.0
                    localIntent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    localIntent.putExtra(Settings.EXTRA_APP_PACKAGE, pkg)
                } else {
                    localIntent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    localIntent.data = Uri.fromParts("package", pkg, null)
                    localIntent.putExtra("app_uid", uid)
                }
                activity.startActivity(localIntent)
            }
            sureDialog.show(activity.supportFragmentManager, "notification")
        } else {
            tbShowToast(TbApplication.mApplicationContext.resources.getString(R.string.notifyMark2))
        }
    } else {
        return true
    }
    return false
}

/*获取手机分辨率*/
fun Any.tbGetPhoneSize(): IntArray {
    val intArray = IntArray(2)
    val ds = TbApplication.mApplicationContext.resources.displayMetrics
    intArray[0] = ds.widthPixels
    intArray[1] = ds.heightPixels
    TbLogUtils.log("width--->${ds.widthPixels},height--->${ds.heightPixels}")
    return intArray
}

/*是否连点*/
var lastClickTime = 0L

fun Any.tbIsMultiClick(spanTime: Long = 1000): Boolean {
    val currentTime = System.currentTimeMillis()
    return if (currentTime - lastClickTime > spanTime) {
        lastClickTime = currentTime
        false
    } else {
        true
    }
}

/*获取dimension资源文件*/
fun Any.tbGetDimensValue(dimensionId: Int): Int {
    return TbApplication.mApplicationContext.resources.getDimension(dimensionId).toInt()
}

/*获取屏幕尺寸IntArray[0]为宽度 IntArray[1]为高度*/
fun Any.tbGetScreenSize(): IntArray {
    val wm =
        TbApplication.mApplicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(outMetrics)
    return intArrayOf(outMetrics.widthPixels, outMetrics.heightPixels)
}

/*app详细信息*/
fun Any.tbGetApkInfo(): TbApkInfo {
    val mActivity = TbApplication.mApplicationContext
    val apkInfo = TbApkInfo()
    val packageManager = mActivity.packageManager
    val packageInfo = packageManager.getPackageInfo(mActivity.packageName, 0)
    apkInfo.apkName = mActivity.resources.getString(packageInfo.applicationInfo.labelRes)
    apkInfo.packgeName = mActivity.packageName
    apkInfo.versionCode = packageInfo.versionCode
    apkInfo.versionName = packageInfo.versionName
    return apkInfo
}



















