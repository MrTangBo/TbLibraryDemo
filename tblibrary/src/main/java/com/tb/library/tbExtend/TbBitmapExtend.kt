package com.tb.library.tbExtend

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.appcompat.widget.AppCompatDrawableManager
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.tb.library.base.TbApplication
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 *@作者：tb
 *@时间：2019/7/4
 *@描述：Bitmap扩展
 */
/*Bitmap转化为byte数组*/
fun Bitmap.tbBitmap2Byte(): ByteArray {
    val o = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, o)
    return o.toByteArray()
}

/*byte数组转化为Bitmap*/
fun ByteArray?.tbByte2Bitmap(): Bitmap? {
    if (this == null) return null
    if (this.isEmpty()) return null
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}

/*把bitmap转换成Base64编码String*/
fun Bitmap?.tbBitmap2String(): String {
    if (this == null) return ""
    return Base64.encodeToString(this.tbBitmap2Byte(), Base64.DEFAULT)
}


/*把Bitmap缩放为新的尺寸*/
fun Bitmap?.tbBitmapScale(newWidth: Int, newHeight: Int): Bitmap? {
    if (this == null) return null
    return ThumbnailUtils.extractThumbnail(this, newWidth, newHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
}

/*Bitmap旋转 degress旋转角度*/
fun Bitmap?.tbBitmapRotate(degress: Float): Bitmap? {
    if (this == null) return null
    val m = Matrix()
    m.postRotate(degress)
    val newBitmap = Bitmap.createBitmap(this, 0, 0, this.width, this.height, m, true)
    this.recycle()
    return newBitmap
}

/*保存bitmap quality保存质量*/
fun Bitmap?.tbBitmapSave(fileName: String, quality: Int = 100) :String{
    if (this == null) return ""
    val file = File(TbApplication.mApplicationContext.cacheDir.path, "$fileName.png")
    if (!file.exists()) {
        file.createNewFile()
    }
    return try {
        val out = FileOutputStream(file)
        this.compress(Bitmap.CompressFormat.PNG, quality, out)
        out.flush()
        out.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

/*保存bitmap quality保存质量*/
fun Bitmap?.tbBitmapSaveSdCard(fileName: String, quality: Int = 100) {
    if (this == null) return

    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "$fileName.png")
    if (!file.exists()) {
        file.createNewFile()
    }
    try {
        val out = FileOutputStream(file)
        this.compress(Bitmap.CompressFormat.PNG, quality, out)
        out.flush()
        out.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    val values = ContentValues()
    values.put(MediaStore.Images.Media.DATA, file.absolutePath)
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/PerracoLabs")
    }
    TbApplication.mApplicationContext.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    // 最后通知图库更新
    TbApplication.mApplicationContext.sendBroadcast(
        Intent(
            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
            file.absolutePath.toUri()
        )
    )
}


/*获取网络图片 需要在线程中获取*/
fun Any.tbBitmapFromInternet(url: String): Bitmap? {
    try {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.doInput = true
        conn.connect()
        val `is` = conn.inputStream
        return BitmapFactory.decodeStream(`is`)
    } catch (e: MalformedURLException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: IOException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    }
    return null
}

/*从资源文件获取图片转化为Bitmap*/
@SuppressLint("RestrictedApi")
fun Any.tbBitmapFromResource(resId: Int): Bitmap? {
    val drawable = AppCompatDrawableManager.get().getDrawable(TbApplication.mApplicationContext, resId)
    return drawable.toBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
}

/*获取网络视频和本地视频第一帧*/
fun Any.tbBitmapThumbnail(url: String, newWidth: Int, newHeight: Int): Bitmap? {
    var bitmap: Bitmap? = null
    val retriever = MediaMetadataRetriever()
    try {
        if (url.startsWith("http://")
            || url.startsWith("https://")
            || url.startsWith("widevine://")
        ) {
            retriever.setDataSource(url, Hashtable())
        } else {
            retriever.setDataSource(url)
        }
        bitmap = retriever.getFrameAtTime(
            0,
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
        ) //retriever.getFrameAtTime(-1);
    } catch (ex: IllegalArgumentException) {
        // Assume this is a corrupt video file
        ex.printStackTrace()
    } catch (ex: RuntimeException) {
        // Assume this is a corrupt video file.
        ex.printStackTrace()
    } finally {
        try {
            retriever.release()
        } catch (ex: RuntimeException) {
            // Ignore failures while cleaning up.
            ex.printStackTrace()
        }
    }
    if (bitmap == null) return null
    return ThumbnailUtils.extractThumbnail(bitmap, newWidth, newHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
}

/*创建图片平铺*/
fun Bitmap?.tbBitmapRepeater(width: Int = this!!.width, height: Int = this!!.width, xy: String = "x"): Bitmap? {
    if (this == null) return null
    var bitmap: Bitmap?
    return when (xy) {
        "x" -> {
            val count = (width + this.width - 1) / this.width
            bitmap = Bitmap.createBitmap(width, this.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            for (idx in 0 until count) {
                canvas.drawBitmap(this, (idx * this.width).toFloat(), 0f, null)
            }
            bitmap
        }
        "y" -> {
            val count = (height + this.height - 1) / this.height
            bitmap = Bitmap.createBitmap(this.width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap!!)
            for (idx in 0 until count) {
                canvas.drawBitmap(this, 0f, (idx * this.height).toFloat(), null)
            }
            bitmap
        }
        "xy" -> {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val countX = (width + this.width - 1) / this.width
            val countY = (height + this.height - 1) / this.height
            for (idx in 0 until countX) {
                canvas.drawBitmap(this, (idx * this.width).toFloat(), 0f, null)
                for (idy in 0 until countY) {
                    canvas.drawBitmap(this, (idx * this.width).toFloat(), ((idy + 1) * this.height).toFloat(), null)
                }
            }
            bitmap
        }
        else -> null
    }
}

/*图片压缩  压缩顺序为 等比压缩->质量压缩  maxSize 单位为KB*/
fun Bitmap?.tbBitmapCompress(targetWidth: Int = 0, targeHeight: Int = 0, maxSize: Int = 100): Bitmap? {
    if (this == null) return null
    val baos = ByteArrayOutputStream()
    var bitmap1 = if (targetWidth == 0 || (targetWidth == this.width && targeHeight == this.height)) {
        this
    } else {
        this.tbBitmapScale(targetWidth, targeHeight)
    }
    bitmap1?.compress(Bitmap.CompressFormat.PNG, 100, baos)
    var options = 100
    while (baos.toByteArray().size / 1024 > maxSize) {
        baos.reset()//重置baos即清空baos
        bitmap1?.compress(Bitmap.CompressFormat.PNG, options, baos)
        options -= 10//每次都减少10
    }
    val isBm = ByteArrayInputStream(baos.toByteArray())
    return BitmapFactory.decodeStream(isBm, null, null)

}
