package com.tb.library.tbExtend

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.fragment.app.Fragment
import com.tb.library.R
import com.tb.library.base.TbApplication
import com.tb.library.base.TbConfig
import com.tb.library.util.SystemBarUtil
import com.tb.library.util.TbLogUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable
import java.io.Serializable

/**
 * @CreateDate: 2020/3/7 3:11
 * @Description: Context扩展
 * @Author: TangBo
 */

/*打开软键盘和关闭软键盘 isOpen=false关闭 为true打开*/
fun Context.tbKeyboard(isOpen: Boolean = false) {
    if (this is Activity) {
        if (isOpen) {
            Handler().postDelayed({
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)

            }, 500)
        } else {
            try {
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(this.window.decorView.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

/*跳转界面*/
inline fun <reified T> Any.tbStartActivity(
    params: MutableMap<String, Serializable>? = null,
    requestCode: Int? = null,
    activityOptions: Bundle? = ActivityOptionsCompat.makeCustomAnimation(
        TbApplication.mApplicationContext, R.anim.slide_right_in, R.anim.slide_left_out
    ).toBundle()
) {
    if (this !is Activity && (this !is Fragment)) {
        return
    }
    var currentActivity: Activity? = null
    if (this is Activity) {
        currentActivity = this
    } else if (this is Fragment) {
        currentActivity = this.activity!!
    }
    if (currentActivity == null) {
        return
    }

    if (!params.isNullOrEmpty()) {
        val b = Bundle()
        params.forEach {
            b.putSerializable(it.key, it.value)
        }
        if (requestCode != null) {
            currentActivity.startActivityForResult(
                Intent(currentActivity, T::class.java).putExtras(b),
                requestCode,
                activityOptions
            )
        } else {
            currentActivity.startActivity(
                Intent(currentActivity, T::class.java).putExtras(b),
                activityOptions
            )
        }
    } else {
        if (requestCode != null) {
            currentActivity.startActivityForResult(
                Intent(currentActivity, T::class.java),
                requestCode,
                activityOptions
            )
        } else {
            currentActivity.startActivity(Intent(currentActivity, T::class.java), activityOptions)
        }
    }
}

/*设置状态栏
* statusColorId状态栏颜色
*NavigationBarColorId 底部导航栏颜色
* isImmersive 是否是沉浸式模式
* isLightMode 状态栏TextColor和图标颜色是否为黑色（用于实现默写设计状态栏为白底黑子）
* isFitWindowStatusBar 是否填充状态栏
* */
fun Context.tbStatusBarInit(
    statusColorId: Int = TbConfig.getInstance().statusColor,
    navigationBarColorId: Int = TbConfig.getInstance().navigationBarColor,
    isImmersive: Boolean = false,
    immersiveBottom: Boolean = true,
    isLightMode: Boolean = false,
    isFitWindowStatusBar: Boolean = false
) {
    if (this !is Fragment && this !is Activity) return
    var currentActivity: Activity? = null
    if (this is Activity) {
        currentActivity = this
    } else if (this is Fragment) {
        currentActivity = this.activity!!
    }
    if (currentActivity == null) return
    val window = currentActivity.window
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    window.statusBarColor = ContextCompat.getColor(this, statusColorId)
    window.navigationBarColor = ContextCompat.getColor(this, navigationBarColorId)
    val decorView = window.decorView
    when {
        isImmersive -> {
            if (immersiveBottom) {
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            } else {
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }

        else -> SystemBarUtil.statusBarLightMode(currentActivity, isFitWindowStatusBar, isLightMode)
    }
}

/*申请权限*/
inline fun Activity.tbRequestPermission(
    permissionList: ArrayList<String>?,
    crossinline permissionSuccess: TbOnClick = {},//获取权限
    crossinline permissionFault: TbOnClick = {}
): Disposable? {
    if (permissionList == null) return null
    if (permissionList.isEmpty()) return null
    return RxPermissions(this).request(*permissionList.toTypedArray()).subscribe {
        if (it) {
            permissionSuccess.invoke()
        } else {
            tbShowToast(resources.getString(R.string.permissionFailed))
            permissionFault.invoke()
        }
    }
}


/*修改桌面图标，只针对启动图标*/
fun Activity.tbChangeDeskIcon(showActivityName: String, hideActivityName: String) {
    val show = ComponentName(this, "${packageName}.$showActivityName")
    val hide = ComponentName(this, "${packageName}.$hideActivityName")
    if (packageManager.getComponentEnabledSetting(show) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
        packageManager.setComponentEnabledSetting(
            show,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
    if (packageManager.getComponentEnabledSetting(hide) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
        packageManager.setComponentEnabledSetting(
            hide,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

}

/*创建Activity桌面快捷图标*/
fun Activity.tbCreateDeskIcon(name: String, icon: Int, activityClass: Class<*>) {
    if (!ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
        tbShowToast(resources.getString(R.string.noSupportDeskIcon))
        return
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        var isCreate = false
        val intent = Intent(this, activityClass)
        intent.action = Intent.ACTION_VIEW
        val shortcutInfo = ShortcutInfoCompat.Builder(this, name)
            .setIcon(IconCompat.createWithResource(this, icon))
            .setIntent(intent)
            .setShortLabel(name)
            .build()
        val list = ShortcutManagerCompat.getDynamicShortcuts(this)
        TbLogUtils.log("shortcutInfos--->${list.size}")
        list.forEach {
            if (it.shortLabel == shortcutInfo.shortLabel) {
                isCreate = true
            }
        }
        if (isCreate) {
            tbShowToast("$name ${resources.getString(R.string.deskIconExist)}")
        } else {
            if (list.size > 4) {
                tbShowToast(resources.getString(R.string.maxIconExist))
                return
            }
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O || Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) {
                ShortcutManagerCompat.addDynamicShortcuts(this, arrayListOf(shortcutInfo))
            } else {
                val peddingIntent =
                    PendingIntent.getBroadcast(
                        this,
                        0,
                        Intent(this, BroadcastReceiver::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                ShortcutManagerCompat.addDynamicShortcuts(this, arrayListOf(shortcutInfo))
                ShortcutManagerCompat.requestPinShortcut(
                    this,
                    shortcutInfo,
                    peddingIntent.intentSender
                )
            }
        }
    } else {
        if (tbCheckShortCutExist(this, name)) {
            tbShowToast("$name ${resources.getString(R.string.deskIconExist)}")
            return
        }
        //创建快捷方式的intent广播
        val intent = Intent("com.android.launcher.action.INSTALL_SHORTCUT")
        //添加快捷名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name)
        //  快捷图标不允许重复
        intent.putExtra("duplicate", false)
        // 快捷图标
        val iconRes = Intent.ShortcutIconResource.fromContext(this, icon)
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes)

        //我们下次启动要用的Intent信息
        val carryIntent = Intent()
        carryIntent.setClass(this, activityClass)
        carryIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        //添加携带的Intent
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, carryIntent)
        //  发送广播
        sendBroadcast(intent)
    }
}

inline fun <reified F : Fragment> Activity.newFragment(vararg args: Pair<String, String>): F {
    val bundle = Bundle()
    args.let {
        for (arg in args) {
            bundle.putString(arg.first, arg.second)
        }
    }
    val fg = F::class.java.newInstance()
    fg.arguments = bundle
    return fg
}

/*判断快捷方式是否存在*/
@SuppressLint("ObsoleteSdkInt")
private fun tbCheckShortCutExist(mContent: Context, name: String): Boolean {
    var isInstallShortcut = false
    val authority: String = when {
        Build.VERSION.SDK_INT < 8 -> "content://com.android.launcher.settings/favorites?notify=true"
        Build.VERSION.SDK_INT <= 19 -> "content://com.android.launcher2.settings/favorites?notify=true"
        else -> "content://com.android.launcher3.settings/favorites?notify=true"
    }
    val cr = mContent.contentResolver
    val uri = Uri.parse(authority)
    try {
        val cursor = cr.query(uri, arrayOf("title", "iconResource"), "title=?", arrayOf(name), null)
        if (cursor != null && cursor.count > 0) {
            isInstallShortcut = true
        }
        if (null != cursor && !cursor.isClosed)
            cursor.close()

    } catch (e: Exception) {
        TbLogUtils.log("e--->$e")
    }
    return isInstallShortcut
}

/*dip2px*/
fun Context.tbDip2px(dpValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

/*dip2px*/
fun Context.tbSp2px(spValue: Float): Int {
    val scale = this.resources.displayMetrics.scaledDensity
    return (spValue * scale + 0.5f).toInt()
}

/*px2Dp*/
fun Context.tbPx2dip(pxValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

/* Get activity from context object*/
fun Context.tbScanForActivity(): Activity? {
    if (this is Activity) {
        return this
    } else if (this is ContextWrapper) {
        return this.baseContext.tbScanForActivity()
    }
    return null
}
