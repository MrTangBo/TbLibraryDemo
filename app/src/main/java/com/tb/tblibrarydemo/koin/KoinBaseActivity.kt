package com.tb.tblibrarydemo.koin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.tb.library.tbExtend.getType
import com.tb.library.tbExtend.tbShowToast

/**
 * Created by Tb on 2020/8/17.
 * describe:
 */
abstract class KoinBaseActivity<M : KoinModel, V : ViewDataBinding> : AppCompatActivity() {

     val mModel: M get() = ViewModelProvider(this)[this::class.java.genericSuperclass!!.getType() as Class<M>]

    open val mLayoutId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mLayoutId == 0) {
            tbShowToast("缺少layoutId")
        } else {
            setContentView(mLayoutId)
        }
    }

}

