package com.tb.library.util

import android.util.Log
import com.tb.library.base.TbConfig

/**
 * @CreateDate: 2020/3/5 22:41
 * @Description: TODO
 * @Author: TangBo
 */
object TbLogUtils {
    fun log(str: String?) {
        if (TbConfig.getInstance().isDebug) {
            if (str == null) {
                Log.i(TbConfig.getInstance().logTag, "打印输入为null")
            } else {
                Log.i(TbConfig.getInstance().logTag, str)
            }
        }
    }
}

