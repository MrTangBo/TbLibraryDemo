package com.tb.library.tbService

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.tb.library.base.TbApplication
import kotlinx.coroutines.*

class FloatWindowService : Service() {

    private lateinit var myBinder: MyBinder

    private var mJob: Job? = null

    companion object {
        const val REQUEST_CODE = 300
        val mIntent = Intent(TbApplication.mApplicationContext, FloatWindowService::class.java)
    }

    private var mParams = WindowManager.LayoutParams()
    private lateinit var mManager: WindowManager

    override fun onBind(intent: Intent): IBinder? {
        return myBinder
    }

    override fun onCreate() {
        super.onCreate()
        myBinder = MyBinder()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        mParams.flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
        //悬浮窗的宽高
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        mParams.gravity = Gravity.CENTER_VERTICAL or Gravity.START
        //背景设置成透明
        mParams.format = PixelFormat.TRANSPARENT
        mManager = baseContext.getSystemService(Service.WINDOW_SERVICE) as WindowManager
    }


    inner class MyBinder : Binder() {
        lateinit var mBinding: ViewDataBinding
        private lateinit var mContext: Activity
        lateinit var mParamsBinder: WindowManager.LayoutParams
         lateinit var mManagerBinder: WindowManager


        var isAddView: Boolean = false

        var refreshTime: Long = 2000

        @SuppressLint("ClickableViewAccessibility")
        fun init(
            context: Activity,
            idList: ArrayList<Int>,
            layoutId: Int,
            refreshData: () -> Unit = {},
            viewClick: (View) -> Unit = { _ -> Unit }
        ) {
            mParamsBinder=mParams
            mManagerBinder=mManager
            mContext = context
            mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), layoutId, null, false)
            val mMyGestureDetectorListener1 = MyGestureDetectorListener(mBinding.root, mBinding.root, viewClick)
            val mGestureDetector1 = GestureDetector(mContext, mMyGestureDetectorListener1)
            mBinding.root.setOnTouchListener { _, event ->
                return@setOnTouchListener mGestureDetector1.onTouchEvent(event)
            }
            idList.forEach {
                val view = mBinding.root.findViewById<View>(it)
                val mMyGestureDetectorListener = MyGestureDetectorListener(mBinding.root, view, viewClick)
                val mGestureDetector = GestureDetector(mContext, mMyGestureDetectorListener)
                view.setOnTouchListener { _, event ->
                    return@setOnTouchListener mGestureDetector.onTouchEvent(event)
                }
            }

            if (mJob == null) {
                mJob = GlobalScope.launch(Dispatchers.Main) {
                    while (true) {
                        delay(refreshTime)
                        refreshData.invoke()
                    }
                }
            }
        }

        fun addView() {
            if (!isAddView) {
                mManager.addView(mBinding.root, mParams)
                isAddView = true
            }
        }

        fun removeView() {
            if (isAddView) {
                mManager.removeView(mBinding.root)
                isAddView = false
            }
        }
    }

    inner class MyGestureDetectorListener(
        var containerView: View,
        var view: View?,
        var click: (View) -> Unit
    ) : GestureDetector.SimpleOnGestureListener() {
        private var lastX = 0f //上一次位置的X.Y坐标
        private var lastY = 0f
        private var nowX = 0f//当前移动位置的X.Y坐标
        private var nowY = 0f
        private var tranX = 0f//悬浮窗移动位置的相对值
        private var tranY = 0f

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            view?.let {
                click.invoke(it)
            }
            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            // 获取按下时的X，Y坐标
            lastX = e?.rawX ?: 0f
            lastY = e?.rawY ?: 0f
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            event: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            // 获取移动时的X，Y坐标
            nowX = event?.rawX ?: 0f
            nowY = event?.rawY ?: 0f
            // 计算XY坐标偏移量
            tranX = nowX - lastX
            tranY = nowY - lastY
            // 移动悬浮窗
            mParams.x += tranX.toInt()
            mParams.y += tranY.toInt()
            //更新悬浮窗位置
            mManager.updateViewLayout(
                containerView,
                mParams
            )
            //记录当前坐标作为下一次计算的上一次移动的位置坐标
            lastX = nowX
            lastY = nowY

            return true
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mJob?.cancel()
    }
}