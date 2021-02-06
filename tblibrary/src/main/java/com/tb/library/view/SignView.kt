package com.tb.library.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Tb on 2020/11/27.
 * describe: 自定义签名View
 */

class SignView : View {

    /**
     * 画笔
     */
    private lateinit var paint: Paint
    private lateinit var path: Path
    private lateinit var cacheCanvas: Canvas

    /**
     * 签名画布
     */
    lateinit var signBitmap: Bitmap

    //画笔颜色
    private var paintColor: Int = Color.BLACK

    //画笔宽度
    private var paintWidth = 5f
    private var xAlixs: Float = 0.0f
    private var yAlixs: Float = 0.0f

    /**
     * 背景色（指最终签名结果文件的背景颜色,这里我设置为白色）
     *  你也可以设置为透明的
     */
//    private var mBackColor = Color.WHITE

    //是否已经签名
    private var isSigned: Boolean = false

    val paintFlagsDrawFilter = PaintFlagsDrawFilter(
        0,
        Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG
    )


    constructor(context: Context?) : super(context) {
        init(context)
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init(context)
    }

    constructor(context: Context?, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(context)
    }


    private fun init(context: Context?) {
        paint = Paint()
        path = Path()
        //setBackgroundColor(Color.WHITE)
        paint.color = paintColor//设置签名颜色
        paint.style = Paint.Style.STROKE  //设置填充样式
        paint.isAntiAlias = true  //抗锯齿功能
        paint.isDither = true
        //连接的外边缘以圆弧的方式相交
        paint.strokeJoin = Paint.Join.ROUND;
        //线条结束处绘制一个半圆
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.strokeWidth = paintWidth//设置画笔宽度
        paint.pathEffect = CornerPathEffect(200f)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //创建跟view一样大的bitmap，用来保存签名
        signBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        cacheCanvas = Canvas(signBitmap)
//        cacheCanvas.drawColor(mBackColor)

        isSigned = false
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //画此次笔画之前的签名
        canvas.drawBitmap(signBitmap, 0f, 0f, paint)
        // 通过画布绘制多点形成的图形
        canvas.drawPath(path, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //记录每次 X ， Y轴的坐标
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                xAlixs = event.x
                yAlixs = event.y

                path.reset()
                path.moveTo(xAlixs, yAlixs)
            }

            MotionEvent.ACTION_MOVE -> {
                xAlixs = event.x
                yAlixs = event.y
                path.lineTo(xAlixs, yAlixs)
                isSigned = true
            }

            MotionEvent.ACTION_UP -> {
                //将路径画到bitmap中，即一次笔画完成才去更新bitmap，而手势轨迹是实时显示在画板上的。
                cacheCanvas.drawPath(path, paint)
                path.reset()
            }

            else -> ""
        }

        // 更新绘制
        invalidate()

        return true
    }

    /**
     * 清除画板
     */
    public fun clear() {

        isSigned = false
        path.reset()
        paint.color = paintColor
//        cacheCanvas.drawColor(mBackColor, PorterDuff.Mode.CLEAR)

        invalidate()
    }

    /**
     * 保存画板
     *
     * @param path       保存到路径
     */
    @Throws(IOException::class)
    fun save(path: String) {
        val bitmap = signBitmap
        //  如果图片过大的话，需要压缩图片，不过在我测试手机上最大才50多kb

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
        val buffer = bos.toByteArray()

        val file = File(path)
        if (file.exists()) {
            file.delete()
        }

        val outputStream = FileOutputStream(file)
        outputStream.write(buffer)
        outputStream.close()
    }


    //TODO 这里可以扩展一些setter方法


    /**
     * 是否有签名
     *
     * @return isSigned
     */
    public fun getHasSigned(): Boolean {
        return isSigned
    }

}