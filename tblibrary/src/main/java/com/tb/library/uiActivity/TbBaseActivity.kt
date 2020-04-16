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
import androidx.lifecycle.ViewModelProvider
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
import com.tb.library.util.TbLogUtils
import com.tb.library.view.TbLoadLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.system.exitProcess

abstract class TbBaseActivity<T : TbBaseModel, G : ViewDataBinding> : AppCompatActivity() {

    var mMode: T? = null
    lateinit var mBinding: G

    var mTbLoadLayout: TbLoadLayout? = null
    var mSpringView: SpringView? = null

    lateinit var mLoadingDialog: TbLoadingDialog

    lateinit var mContext: Context

    open val mIsOpenARouter = false//是否开启ARouter
    open val mIsOpenEventBus = false//是否开启EventBus

    abstract val mLayoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = TbConfig.getInstance().screenOrientation
        window.decorView.background = ContextCompat.getDrawable(this, R.color.tb_white)
        tbStatusBarInit()
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
        if (mIsOpenARouter) {
            ARouter.getInstance().inject(this)
        }
        if (mIsOpenEventBus) {
            EventBus.getDefault().register(this)
        }
    }

    open fun initLoadingDialog() {
        mLoadingDialog = TbLoadingDialog(this)
    }

    open fun initSpringView() {
        mSpringView?.let { springView ->
            mMode?.let {
                mSpringView = springView.init(it)
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

            model.mErrorCodeEvent = { code, msg, taskId ->
                errorCodeEvent(code, msg, taskId)
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
        mTbLoadLayout?.showView(TbLoadLayout.CONTENT)
    }

    open fun <M> errorCodeEvent(code: M, msg: String, taskId: Int) {

    }

    open lateinit var mEventInfo: TbEventBusInfo
    /*eventBus回调*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onUserEvent(event: TbEventBusInfo) {
        mEventInfo = event

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
        if (mIsOpenEventBus) {
            EventBus.getDefault().unregister(this)
        }
        ActivityManagerUtil.getInstance().removeActivity(this)

    }

    open fun showContent() {
        mTbLoadLayout?.showView(TbLoadLayout.CONTENT)
    }

    open fun showEmpty() {
        mTbLoadLayout?.showView(TbLoadLayout.NO_DATA)
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
            ActivityManagerUtil.getInstance().clearAll()
            exitProcess(0)
        }
    }

}