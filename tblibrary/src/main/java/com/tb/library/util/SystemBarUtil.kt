package com.tb.library.util

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager

/**
 * @CreateDate: 2020/3/12 1:12
 * @Description: TODO
 * @Author: TangBo
 */
object SystemBarUtil {

    /*设置状态栏黑色字体图标，*/
    fun statusBarLightMode(activity: Activity, isFitWindowStatusBar: Boolean, isDark: Boolean = true): Int {
        var result = 0
        when {
            MIUISetStatusBarLightMode(activity.window, isDark) -> result = 1
            FlymeSetStatusBarLightMode(activity.window, isDark) -> result = 2
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                if (isFitWindowStatusBar) {
                    if (isDark){
                        activity.window.decorView.systemUiVisibility =
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    }else{
                        activity.window.decorView.systemUiVisibility =
                            View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    }
                } else {
                    if (isDark){
                        activity.window.decorView.systemUiVisibility =
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_VISIBLE
                    }else{
                        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                    }
                }
                result = 3
            }
        }
        return result
    }

    /*设置状态栏图标为深色和魅族特定的文字风格*/
    fun FlymeSetStatusBarLightMode(window: Window?, dark: Boolean): Boolean {
        var result = false
        if (window != null) {
            try {
                val lp = window.attributes
                val darkFlag = WindowManager.LayoutParams::class.java
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java
                    .getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                if (dark) {
                    value = value or bit
                } else {
                    value = value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                window.attributes = lp
                result = true
            } catch (e: Exception) {

            }

        }
        return result
    }

    fun MIUISetStatusBarLightMode(window: Window?, dark: Boolean): Boolean {
        var result = false
        if (window != null) {
            val clazz = window.javaClass
            try {
                var darkModeFlag = 0
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField =
                    clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag)//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag)//清除黑色字体
                }
                result = true

            } catch (e: Exception) {

            }
        }
        return result
    }

}
