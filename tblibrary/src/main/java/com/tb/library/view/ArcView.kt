package com.tb.library.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*

import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.tb.library.R

/**
 * @author: MrTangBo
 * @decribe:
 */
class ArcView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(mContext, attrs, defStyleAttr) {
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    /**
     * 弧形高度
     */
    private val mArcHeight: Int
    /**
     * 背景颜色
     */
    private val mBgColor: Int

    private var arcOrientation = "top"

    private val mPaint: Paint

    init {
        val typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ArcView)
        mArcHeight = typedArray.getDimensionPixelSize(R.styleable.ArcView_arcHeight, 0)
        mBgColor = typedArray.getColor(R.styleable.ArcView_bgColor, Color.parseColor("#303F9F"))
        arcOrientation = typedArray.getString(R.styleable.ArcView_arcOrientation).toString()
        typedArray.recycle()
        mPaint = Paint()
        mPaint.isDither = true
        mPaint.isAntiAlias = true
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mPaint.style = Paint.Style.FILL
        mPaint.color = mBgColor
        if (arcOrientation.endsWith("top")) {
            val rect = Rect(0, 0, mWidth, mHeight)
            canvas.drawRect(rect, mPaint)
            mPaint.color = ContextCompat.getColor(mContext, R.color.tb_white)
            val path = Path()
            path.moveTo(0f, mHeight.toFloat())
            path.quadTo((mWidth / 2).toFloat(), (mHeight - mArcHeight).toFloat(), mWidth.toFloat(), mHeight.toFloat())
            canvas.drawPath(path, mPaint)
        } else {
            //矩形部分
            val rect = Rect(0, 0, mWidth, mHeight - mArcHeight)
            canvas.drawRect(rect, mPaint)
            //弧形部分
            val path = Path()
            path.moveTo(0f, (mHeight - mArcHeight).toFloat())
            path.quadTo((mWidth / 2).toFloat(), mHeight.toFloat(), mWidth.toFloat(), (mHeight - mArcHeight).toFloat())
            canvas.drawPath(path, mPaint)
        }


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        if (widthMode == View.MeasureSpec.EXACTLY) {
            mWidth = widthSize
        }
        if (heightMode == View.MeasureSpec.EXACTLY) {
            mHeight = heightSize
        }
        setMeasuredDimension(mWidth, mHeight)
    }
}
