package com.tb.library.uiActivity

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.liaoinstan.springview.widget.SpringView
import com.tb.library.R
import com.tb.library.base.TbConfig
import com.tb.library.base.TbEventBusInfo
import com.tb.library.model.TbBaseModel
import com.tb.library.tbDialog.TbLoadingDialog
import com.tb.library.tbExtend.*
import com.tb.library.util.ActivityManagerUtil
import com.tb.library.util.FontUtil
import com.tb.library.view.TbLoadLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.system.exitProcess

abstract class TbBaseActivity<T : TbBaseModel,G:ViewDataBinding> : AppCompatActivity() {

    var mMode: T? = null
    lateinit var mBinding:G

    var mTbLoadLayout: TbLoadLayout? = null
    var mSpringView: SpringView? = null

    lateinit var mLoadingDialog: TbLoadingDialog

    lateinit var mContext: Context

    @LayoutRes
    var mLayoutId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tbStatusBarInit()
        requestedOrientation = TbConfig.getInstance().screenOrientation
        window.decorView.background = ContextCompat.getDrawable(this, R.color.tb_white)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        mBinding = DataBindingUtil.setContentView(this, mLayoutId)
        init()
        initLoadingDialog()
        initModel()
        initData()
    }

    open fun init() {
        mContext = this
        tbAddActivity()
        ARouter.getInstance().inject(this)
        EventBus.getDefault().register(this)
    }

    open fun initLoadingDialog() {
        mLoadingDialog = TbLoadingDialog(this)
    }

    open fun initSpringView() {
        mSpringView?.let { springView ->
            mMode?.let {
                springView.init(it)
            }
        }
    }

    open fun getModel() {

    }

    open fun initModel() {
        getModel()
        mMode?.let { model ->
            initSpringView()
            model.mTbLoadLayout = mTbLoadLayout
            model.mSpringView = mSpringView
            lifecycle.addObserver(model)
            model.mActivity = this
            model.mBinding = mBinding

            model.mDialogDismiss = {
                hideLoadingDialog()
            }
            model.mDialogShow = {
                showLoadingDialog()
            }

            model.mErrorCodeEvent = { code, msg ->
                errorCodeEvent(code, msg)
            }
            model.mLiveDataMap.forEach { map ->
                map.value.observe(this, Observer {
                    resultData(map.key, it)
                })
            }
        }
    }

    open fun initData() {

    }

    open fun showLoadingDialog() {
        mLoadingDialog.show()
    }

    open fun hideLoadingDialog() {
        mLoadingDialog.dismiss()
    }

    open fun <E> resultData(taskId: Int, info: E) {

    }

    open fun <M> errorCodeEvent(code: M, msg: String) {

    }

    open lateinit var eventBundle: Bundle
    open var mEventFlag: String = ""
    open var mEventType: String = ""
    /*eventBus回调*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onUserEvent(event: TbEventBusInfo?) {
        event?.let {
            eventBundle = it.bundle
            eventBundle.getString(TbConfig.EVENT_FLAG)?.let { flag ->
                mEventFlag = flag
            }
            eventBundle.getString(TbConfig.EVENT_TYPE)?.let { type ->
                mEventFlag = type
            }
        }
    }

    open fun onClick(view: View?) {
        if (tbIsMultiClick()) return
        view?.let {
            singleClick(it)
        }
    }

    /*单击事件，防止连点就调用这个方法*/
    open fun singleClick(view: View) {

    }

    override fun onResume() {
        super.onResume()
        if (TbConfig.getInstance().fontType.isNotEmpty()) {
            FontUtil.replaceFont(this, TbConfig.getInstance().fontType)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMode?.dropView()
        tbKeyboard(false)
        ActivityManagerUtil.getInstance().removeActivity(this)
        EventBus.getDefault().unregister(this)
    }

    /**
     * 退出app处理
     */
    private var exitTime: Long = 0

    open fun exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
        {
            tbShowToast(resources.getString(R.string.double_click_exit))
            exitTime = System.currentTimeMillis()

        } else {
            tbCleanAllActivity()
            exitProcess(0)
        }
    }

}