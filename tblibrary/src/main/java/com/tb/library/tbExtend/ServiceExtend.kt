package com.tb.library.tbExtend

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentActivity
import com.tb.library.R
import com.tb.library.base.TbApplication
import com.tb.library.tbDialog.TbSureDialog
import com.tb.library.tbService.FloatWindowService

/**
 * Created by Tb on 2021/3/2.
 * describe:
 */
/*未开启浮窗权限提示弹窗*/
@RequiresApi(Build.VERSION_CODES.M)
fun tbShowFloatWindowTips(
    mContext: FragmentActivity, mSureDialog: TbSureDialog = TbSureDialog(
        titleTx = TbApplication.mApplicationContext.resources.getString(R.string.float_window_permission_title),
        messageTx = TbApplication.mApplicationContext.resources.getString(R.string.float_window_permission_tips)
    )
): Boolean {
    if (!Settings.canDrawOverlays(mContext)) {

        mSureDialog.show(mContext.supportFragmentManager, "pop")
        mSureDialog.sureClick = {
            mContext.startActivityForResult(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION),
                FloatWindowService.REQUEST_CODE, ActivityOptionsCompat.makeCustomAnimation(
                    TbApplication.mApplicationContext,
                    com.tb.library.R.anim.slide_right_in,
                    com.tb.library.R.anim.slide_left_out
                ).toBundle()
            )

        }
    }
    return Settings.canDrawOverlays(mContext)
}
