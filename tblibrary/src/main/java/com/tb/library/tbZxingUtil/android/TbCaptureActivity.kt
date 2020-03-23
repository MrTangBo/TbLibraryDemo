package com.tb.library.tbZxingUtil.android


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import com.google.zxing.Result
import com.tb.library.R
import com.tb.library.tbExtend.tbRequestPermission
import com.tb.library.tbZxingUtil.bean.ZxingConfig
import com.tb.library.tbZxingUtil.camera.CameraManager
import com.tb.library.tbZxingUtil.common.Constant
import com.tb.library.tbZxingUtil.decode.DecodeImgCallback
import com.tb.library.tbZxingUtil.decode.DecodeImgThread
import com.tb.library.tbZxingUtil.decode.ImageUtil
import com.tb.library.tbZxingUtil.view.ViewfinderView


/**
 * @author: yzq
 * @date: 2017/10/26 15:22
 * @declare :扫一扫
 */

open class TbCaptureActivity : AppCompatActivity(), SurfaceHolder.Callback, View.OnClickListener {
    var config: ZxingConfig? = null
    private var previewView: SurfaceView? = null
    var viewfinderView: ViewfinderView? = null
        private set
    private var flashLightIv: AppCompatImageView? = null
    private var flashLightTv: TextView? = null
    private var backIv: AppCompatImageView? = null
    private var flashLightLayout: LinearLayout? = null
    private var albumLayout: LinearLayout? = null
    private var bottomLayout: LinearLayout? = null
    private var hasSurface: Boolean = false
    lateinit var inactivityTimer: InactivityTimer
    lateinit var beepManager: BeepManager
    var cameraManager: CameraManager? = null
    private var handler: CaptureActivityHandler? = null
    private var surfaceHolder: SurfaceHolder? = null

    var mLayoutId = R.layout.tb_activity_capture

    fun getHandler(): Handler? {
        return handler
    }

    fun drawViewfinder() {
        viewfinderView!!.drawViewfinder()
    }


    fun setLayoutId(layoutId: Int) {
        this.mLayoutId = layoutId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 保持Activity处于唤醒状态
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK
        }

        /*先获取配置信息*/
        try {
            config = intent.extras!!.get(Constant.INTENT_ZXING_CONFIG) as ZxingConfig?
        } catch (e: Exception) {

            Log.i("config", e.toString())
        }

        if (config == null) {
            config = ZxingConfig()
        }
        setContentView(mLayoutId)
        initView()

        hasSurface = false

        inactivityTimer = InactivityTimer(this)
        beepManager = BeepManager(this)
        beepManager.setPlayBeep(config!!.isPlayBeep)
        beepManager.setVibrate(config!!.isShake)
    }


    open fun initView() {
        previewView = findViewById(R.id.preview_view)
        previewView!!.setOnClickListener(this)

        viewfinderView = findViewById(R.id.viewfinder_view)
        viewfinderView!!.setZxingConfig(config!!)


        backIv = findViewById(R.id.backIv)
        backIv!!.setOnClickListener(this)

        flashLightIv = findViewById(R.id.flashLightIv)
        flashLightTv = findViewById(R.id.flashLightTv)

        flashLightLayout = findViewById(R.id.flashLightLayout)
        flashLightLayout!!.setOnClickListener(this)
        albumLayout = findViewById(R.id.albumLayout)
        albumLayout!!.setOnClickListener(this)
        bottomLayout = findViewById(R.id.bottomLayout)

        switchVisibility(bottomLayout, config!!.isShowbottomLayout)
        switchVisibility(flashLightLayout, config!!.isShowFlashLight)
        switchVisibility(albumLayout, config!!.isShowAlbum)

        /*有闪光灯就显示手电筒按钮  否则不显示*/
        if (isSupportCameraLedFlash(packageManager)) {
            flashLightLayout!!.visibility = View.VISIBLE
        } else {
            flashLightLayout!!.visibility = View.GONE
        }

    }

    /**
     * @param flashState 切换闪光灯图片
     */
    fun switchFlashImg(flashState: Int) {
        if (flashState == Constant.FLASH_OPEN) {
            flashLightIv!!.setImageResource(R.drawable.zx_icon_open)
            flashLightTv!!.setText(R.string.close_flash)
        } else {
            flashLightIv!!.setImageResource(R.drawable.zx_icon_close)
            flashLightTv!!.setText(R.string.open_flash)
        }
    }

    /**
     * @param rawResult 返回的扫描结果
     */
    fun handleDecode(rawResult: Result) {
        inactivityTimer.onActivity()
        beepManager.playBeepSoundAndVibrate()
        val intent = intent
        intent.putExtra(Constant.CODED_CONTENT, rawResult.text)
        setResult(Activity.RESULT_OK, intent)
        this.finish()
    }


    private fun switchVisibility(view: View?, b: Boolean) {
        if (b) {
            view!!.visibility = View.VISIBLE
        } else {
            view!!.visibility = View.GONE
        }
    }


    override fun onResume() {
        super.onResume()
        cameraManager = CameraManager(application, config)
        viewfinderView!!.setCameraManager(cameraManager)
        handler = null
        surfaceHolder = previewView!!.holder
        if (hasSurface) {
            initCamera(surfaceHolder)
        } else {
            // 重置callback，等待surfaceCreated()来初始化camera
            surfaceHolder!!.addCallback(this)
        }
        beepManager.updatePrefs()
        inactivityTimer.onResume()


    }

    private fun initCamera(surfaceHolder: SurfaceHolder?) {
        if (surfaceHolder == null) {
            throw IllegalStateException("No SurfaceHolder provided")
        }
        if (cameraManager!!.isOpen) {
            return
        }
        tbRequestPermission(arrayListOf(Manifest.permission.CAMERA), permissionSuccess = {
                // 打开Camera硬件设备
                cameraManager!!.openDriver(surfaceHolder)
                // 创建一个handler来打开预览，并抛出一个运行时异常
                if (handler == null) {
                    handler = CaptureActivityHandler(this, cameraManager!!)
                }
            }, permissionFault = {
                FinishListener(this)
            })

//        try {
//            // 打开Camera硬件设备
//            cameraManager!!.openDriver(surfaceHolder)
//            // 创建一个handler来打开预览，并抛出一个运行时异常
//            if (handler == null) {
//                handler = CaptureActivityHandler(this, cameraManager!!)
//            }
//        } catch (ioe: IOException) {
//            Log.w(TAG, ioe)
//            displayFrameworkBugMessageAndExit()
//        } catch (e: RuntimeException) {
//            Log.w(TAG, "Unexpected error initializing camera", e)
//            displayFrameworkBugMessageAndExit()
//        }

    }

//    private fun displayFrameworkBugMessageAndExit() {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("扫一扫")
//        builder.setMessage(getString(R.string.msg_camera_framework_bug))
//        builder.setPositiveButton(R.string.button_ok, FinishListener(this))
//        builder.setOnCancelListener(FinishListener(this))
//        builder.show()
//    }

    override fun onPause() {

        Log.i("TbCaptureActivity", "onPause")
        if (handler != null) {
            handler!!.quitSynchronously()
            handler = null
        }
        inactivityTimer.onPause()
        beepManager.close()
        cameraManager!!.closeDriver()

        if (!hasSurface) {

            surfaceHolder!!.removeCallback(this)
        }
        super.onPause()
    }

    override fun onDestroy() {
        inactivityTimer.shutdown()
        viewfinderView!!.stopAnimator()
        super.onDestroy()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (!hasSurface) {
            hasSurface = true
            initCamera(holder)
        }
    }


    override fun surfaceDestroyed(holder: SurfaceHolder) {
        hasSurface = false
    }

    override fun surfaceChanged(
        holder: SurfaceHolder, format: Int, width: Int,
        height: Int
    ) {

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.flashLightLayout -> /*切换闪光灯*/
                cameraManager!!.switchFlashLight(handler)
            R.id.albumLayout -> {
                /*打开相册*/
                val intent = Intent()
                intent.action = Intent.ACTION_PICK
                intent.type = "image/*"
                startActivityForResult(intent, Constant.REQUEST_IMAGE)
            }
            R.id.backIv -> finish()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constant.REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            val path = ImageUtil.getImageAbsolutePath(this, data!!.data)
            DecodeImgThread(path, object : DecodeImgCallback {
                override fun onImageDecodeSuccess(result: Result) {
                    handleDecode(result)
                }
                override fun onImageDecodeFailed() {
                    Toast.makeText(this@TbCaptureActivity, R.string.scan_failed_tip, Toast.LENGTH_SHORT).show()
                }
            }).run()
        }
    }

    companion object {

        private val TAG = TbCaptureActivity::class.java.simpleName

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }

        /**
         * @param pm
         * @return 是否有闪光灯
         */
        fun isSupportCameraLedFlash(pm: PackageManager?): Boolean {
            if (pm != null) {
                val features = pm.systemAvailableFeatures
                for (f in features) {
                    if (f != null && PackageManager.FEATURE_CAMERA_FLASH == f.name) {
                        return true
                    }
                }
            }
            return false
        }
    }


}
