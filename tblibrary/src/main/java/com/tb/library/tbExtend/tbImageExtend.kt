package com.tb.library.tbExtend

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.tb.library.R
import com.tb.library.base.TbApplication
import com.tb.library.base.TbConfig
import com.tb.library.databinding.TbPopSaveImageBinding
import com.tb.library.tbZxingUtil.TbRGBLuminanceSource
import com.tb.library.tbZxingUtil.encode.CodeCreator
import com.tb.library.util.GlideUtil
import com.tb.library.util.ImageWaterUtil
import com.tb.library.view.TbPopupWindow
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

/**
 * @CreateDate: 2020/3/7 6:20
 * @Description: TODO
 * @Author: TangBo
 */


//常规的加载
@BindingAdapter("url", "placeholder", "error", "scaleType", "isCache", requireAll = false)
fun ImageView.showImage(
    imageUrl: String? = null,
    placeholder: Int? = null,
    error: Int? = null,
    scaleType: ImageView.ScaleType? = null,
    isCache: Boolean? = null//是否启用缓存
) {
    var mScaleType = ImageView.ScaleType.CENTER_CROP
    var mIsCache = true
    var mImageUrl = ""
    var mPlaceholder = TbConfig.getInstance().placeholder
    var mError = TbConfig.getInstance().errorHolder
    scaleType?.let {
        mScaleType = it
    }
    isCache?.let {
        mIsCache = it
    }
    imageUrl?.let {
        mImageUrl = it
    }
    placeholder?.let {
        mPlaceholder = it
    }
    error?.let {
        mError = it
    }
    GlideUtil.getInstance()
        .showImage(this.context, mImageUrl, this, mPlaceholder, mError, mScaleType, mIsCache)
}


/*上传图片到自己的服务器*/
@SuppressLint("CheckResult")
inline fun List<String>?.tbUpLoadImage(
    name: String = "file",
    crossinline zipListener: ((partMap: HashMap<String, RequestBody>) -> Unit) = { _ -> Unit },
    key: Array<String>? = null,
    value: Array<String>? = null,
    showIndex: Boolean = false  //有的后台需要在多图片上传的时候为file0,file1,file2..
) {
    if (this.isNullOrEmpty()) return
    Flowable.just(this)
        .observeOn(Schedulers.io())
        .map {
            val fileList = arrayListOf<File>()
            it.forEach { path ->
                val file = File(path)
                if (file.exists()) {
                    fileList.add(file)
                }
            }
            fileList
        }.observeOn(AndroidSchedulers.mainThread()).subscribe {
            val partMap = HashMap<String, RequestBody>()
            it.forEachIndexed { index, file ->
                val fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                partMap[name + if (showIndex) index else "" + "\"; filename=\"" + file.name] =
                    fileBody
            }
            if (key == null || value == null) {
                zipListener.invoke(partMap)
                return@subscribe
            }
            for (i in key.indices) {
                val valueBody = RequestBody.create(MediaType.parse("text/plain"), value[i])
                partMap[key[i]] = valueBody
            }
            zipListener.invoke(partMap)
        }
}

//长按，通过zxing读取图片，判断是否有二维码
@SuppressLint("ClickableViewAccessibility")
inline fun ImageView?.tbImageLongPress(
    activity: AppCompatActivity,
    crossinline readQRCode: (readStr: String) -> Unit = { _ -> Unit },
    crossinline clickImg: TbOnClick = {},
    imageUrl: String = ""
) {
    if (this == null) return
    val act: WeakReference<AppCompatActivity> = WeakReference(activity)
    val mGestureDetector =
        GestureDetector(activity, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent?) {
                val pop = TbPopupWindow(activity, R.layout.tb_pop_save_image)
                val bind: TbPopSaveImageBinding = pop.popBaseBind as TbPopSaveImageBinding
                bind.saveImage.setOnClickListener {
                    act.get()?.let { mActivity ->
                        mActivity.tbRequestPermission(
                            arrayListOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ), permissionSuccess = {
                                object : Thread() {
                                    override fun run() {
                                        val bm = tbBitmapFromInternet(imageUrl)
                                        if (mActivity.isDestroyed) {
                                            return
                                        }
                                        mActivity.runOnUiThread {
                                            bm?.tbBitmapSaveSdCard("${System.currentTimeMillis()}")//保存图片
                                        }
                                    }
                                }.start()
                                tbShowToast(TbApplication.mApplicationContext.resources.getString(R.string.saveSuccess))
                                pop.dismiss()
                            }
                        )
                    }

                }
                val re: Result? = tbReadQRCode()
                if (re == null) {
                    bind.readRQCode.visibility = View.GONE
                } else {
                    bind.readRQCode.visibility = View.VISIBLE
                    bind.readRQCode.setOnClickListener {
                        readQRCode.invoke(re.text)
                        pop.dismiss()
                    }
                }
                pop.showAsDropDown(
                    this@tbImageLongPress,
                    e?.x!!.toInt(), -e.y.toInt(), Gravity.TOP
                )
            }

            /*单击*/
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                clickImg.invoke()
                return super.onSingleTapUp(e)
            }
        })
    setOnTouchListener { _, motionEvent ->
        mGestureDetector.onTouchEvent(motionEvent)
        return@setOnTouchListener true
    }
}

/*解析二维码*/
fun ImageView?.tbReadQRCode(): Result? {
    if (this == null) return null
    val bitmap = drawable.toBitmap()
    val source = TbRGBLuminanceSource(bitmap)
    val bitmap1 = BinaryBitmap(HybridBinarizer(source))
    val hints = LinkedHashMap<DecodeHintType, Any>()
    hints[DecodeHintType.CHARACTER_SET] = "utf-8"
    val reader = MultiFormatReader()
    var re: Result? = null
    try {
        re = reader.decode(bitmap1, hints)
    } catch (e: Exception) {
    }

    return re
}

/*生成二维码*/
fun Any.tbCreateQRCode(
    contents: String,
    width: Int = tbGetDimensValue(R.dimen.x300),
    height: Int = tbGetDimensValue(R.dimen.x300),
    @IdRes logo: Int = 0
): Bitmap {
    var logoImage: Bitmap? = null
    if (logo != 0) {
        logoImage = BitmapFactory.decodeResource(TbApplication.mApplicationContext.resources, logo)
    }
    return CodeCreator.createQRCode(contents, width, height, logoImage)
}

/**
 * 添加水印(图片或者文字)
 * @receiver ImageView
 * @param currentBm Bitmap
 * @param water T 水印
 * @param targetWidth Int
 * @param targeHeight Int
 * @param gravity Int
 * @param rect Rect
 */

fun <T> ImageView.tbAddWater(
    currentBm: Bitmap?,
    water: T,
    targetWidth: Int = tbGetDimensValue(R.dimen.x150),
    targeHeight: Int = tbGetDimensValue(R.dimen.x150),
    gravity: Int = Gravity.BOTTOM or Gravity.START,
    rect: Rect = Rect(),
    textSize: Int = 14,
    textColor: Int = tbGetResColor(R.color.tb_text_black)
): ImageView {
    if (currentBm == null) return this
    var targetBm: Bitmap? = null
    when (gravity) {
        Gravity.BOTTOM or Gravity.END -> {//右下角
            when (water) {
                is Bitmap -> {
                    targetBm = ImageWaterUtil.createWaterMaskRightBottom(
                        this.context,
                        currentBm,
                        water.tbBitmapCompress(targetWidth, targeHeight),
                        rect.right,
                        rect.bottom
                    )
                }
                is String -> {
                    targetBm = ImageWaterUtil.drawTextToLeftBottom(
                        context,
                        currentBm,
                        water,
                        textSize,
                        textColor,
                        rect.left,
                        rect.bottom
                    )
                }
            }
        }
        Gravity.TOP or Gravity.END -> {//右上角
            when (water) {
                is Bitmap -> {
                    targetBm = ImageWaterUtil.createWaterMaskRightTop(
                        this.context,
                        currentBm,
                        water.tbBitmapCompress(targetWidth, targeHeight),
                        rect.right,
                        rect.top
                    )
                }
                is String -> {
                    targetBm = ImageWaterUtil.drawTextToRightTop(
                        context,
                        currentBm,
                        water,
                        textSize,
                        textColor,
                        rect.right,
                        rect.top
                    )
                }
            }
        }

        Gravity.TOP or Gravity.START -> {//左上角
            when (water) {
                is Bitmap -> {
                    targetBm = ImageWaterUtil.createWaterMaskLeftTop(
                        this.context,
                        currentBm,
                        water.tbBitmapCompress(targetWidth, targeHeight),
                        rect.left,
                        rect.top
                    )
                }
                is String -> {
                    targetBm = ImageWaterUtil.drawTextToLeftTop(
                        context,
                        currentBm,
                        water,
                        textSize,
                        textColor,
                        rect.left,
                        rect.top
                    )
                }
            }
        }

        Gravity.BOTTOM or Gravity.START -> {//左下角
            when (water) {
                is Bitmap -> {
                    targetBm = ImageWaterUtil.createWaterMaskLeftBottom(
                        this.context,
                        currentBm,
                        water.tbBitmapCompress(targetWidth, targeHeight),
                        rect.left,
                        rect.bottom
                    )
                }
                is String -> {
                    targetBm = ImageWaterUtil.drawTextToLeftBottom(
                        context,
                        currentBm,
                        water,
                        textSize,
                        textColor,
                        rect.left,
                        rect.bottom
                    )
                }
            }
        }
        Gravity.CENTER -> {//中间
            when (water) {
                is Bitmap -> {
                    targetBm = ImageWaterUtil.createWaterMaskCenter(
                        currentBm,
                        water.tbBitmapCompress(targetWidth, targeHeight)
                    )
                }
                is String -> {
                    targetBm = ImageWaterUtil.drawTextToCenter(
                        context,
                        currentBm,
                        water,
                        textSize,
                        textColor
                    )
                }
            }
        }
    }
    setImageBitmap(targetBm)

    return this
}
