package com.tb.library.tbExtend

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Context
import com.tb.library.base.TbApplication
import com.tencent.mmkv.MMKV

/**
 * @CreateDate: 2020/3/7 2:12
 * @Description: 控件扩展
 * @Author: TangBo
 */

//获取系统通知栏管理
val Any.tbNotificationManager: NotificationManager
    get() = TbApplication.mApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

/*获取ActivityManager*/
val Any.tbActivityManager: ActivityManager
    get() = TbApplication.mApplicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

val Any.tbClipboardManager: ClipboardManager
    get() = TbApplication.mApplicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

/*获取不可清除数据的MMKV对象*/
val Any.tBMMKV: MMKV
    get() = MMKV.mmkvWithID("noClean")

/*获取可清除数据的MMKV对象*/
val Any.tBMMKV_C: MMKV
    get() = MMKV.mmkvWithID("canClean")