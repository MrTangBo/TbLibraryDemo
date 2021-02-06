package com.tb.tblibrarydemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import com.tb.library.tbExtend.tbAddWater
import com.tb.library.tbExtend.tbBitmapFromResource
import com.tb.library.uiActivity.TbBaseActivity
import com.tb.tblibrarydemo.databinding.ActivityTestBinding
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by Tb on 2020/4/16.
 * describe:
 */
class TestActivity : TbBaseActivity<TestMode, ActivityTestBinding>() {


    override val mLayoutId: Int
        get() = R.layout.activity_test

    override fun initData() {
        super.initData()

        val bm = tbBitmapFromResource(R.mipmap.bg_upgrade_city)
        val warter = tbBitmapFromResource(R.mipmap.ic_launcher)

        mImg.tbAddWater(bm,warter,rect = Rect(50,0,0,50)).apply {

            tbAddWater( drawable.toBitmap(),"asdsa")
        }

    }

    override fun singleClick(view: View) {
        super.singleClick(view)
//        mMode?.getData()
//        tbStartActivity<MainActivity>()
    }
}


