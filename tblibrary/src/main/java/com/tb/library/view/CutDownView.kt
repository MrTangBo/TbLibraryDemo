package com.tb.library.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.tb.library.R
import java.util.*

/**
 *@作者：tb
 *@时间：2019/9/24
 *@描述：环形倒计时
 */
class CutDownView : AppCompatTextView {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {
        init()
    }

    private lateinit var mPaint: Paint
    private lateinit var mBounds: Rect
    private lateinit var mArcRectF: RectF
    private lateinit var mTimer: Timer
    private val mOutLineColor = -0x777778
    private val mOutLineWidth = 4

    private var mCircleColor = -0x66777778
    private var mCircleRadius: Int = 0

    private var mTextColor = Color.WHITE
    private var mTextSize = 36f

    private var mProgressLineColor = Color.RED
    private val mProgressLineWidth = 4
    private var mProgress = 0

    private var mCenterX: Int = 0
    private var mCenterY: Int = 0

    private var mTime = 2000   //默认计时
    private var mDrawTimes = 4  //总的绘制次数
    private var mCurrentDrawTimes: Int = 0  //已经绘制的次数
    private var mEachDrawAngle = 90 //默认每次绘制90度
    private var cutDownListener: CutDownListener? = null
    private var mText = context.resources.getString(R.string.skip)

    private fun init() {
        mPaint = Paint()
        mBounds = Rect()
        mArcRectF = RectF()
        mTimer = Timer()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = measuredWidth
        var height = measuredHeight
        if (width > height) {
            height = width
        } else {
            width = height
        }
        mCircleRadius = width / 2
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        getDrawingRect(mBounds) //找到view的边界

        mCenterX = mBounds.centerX()
        mCenterY = mBounds.centerY()

        //画大圆
        mPaint.isAntiAlias = true  //防锯齿
        mPaint.style = Paint.Style.FILL
        mPaint.color = mCircleColor
        canvas.drawCircle(mBounds.centerX().toFloat(), mBounds.centerY().toFloat(), mCircleRadius.toFloat(), mPaint)

        //画外边框
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mOutLineWidth.toFloat()
        mPaint.color = mOutLineColor
        canvas.drawCircle(
            mBounds.centerX().toFloat(),
            mBounds.centerY().toFloat(),
            (mCircleRadius - mOutLineWidth).toFloat(),
            mPaint
        )

        //画字
        val paint = paint
        paint.color = mTextColor
        paint.isAntiAlias = true  //防锯齿
        paint.textSize = mTextSize
        paint.textAlign = Paint.Align.CENTER
        val textY = mCenterY - (paint.descent() + paint.ascent()) / 2
        canvas.drawText(mText, mCenterX.toFloat(), textY, paint)

        //画进度条
        mPaint.strokeWidth = mProgressLineWidth.toFloat()
        mPaint.color = mProgressLineColor
        mPaint.strokeCap = Paint.Cap.ROUND
        mArcRectF.set(
            (mBounds.left + mProgressLineWidth).toFloat(), (mBounds.top + mProgressLineWidth).toFloat(),
            (mBounds.right - mProgressLineWidth).toFloat(), (mBounds.bottom - mProgressLineWidth).toFloat()
        )
        canvas.drawArc(
            mArcRectF, -90f,
            ((mCurrentDrawTimes + 1) * mEachDrawAngle).toFloat(), false, mPaint
        )
    }


    fun star() {
        val step = 100 / mDrawTimes
        mTimer.schedule(object : TimerTask() {
            override fun run() {
                postInvalidate()
                mCurrentDrawTimes++
                mProgress += step
                mTime -= 100
                cutDownListener?.percentChange(mProgress)
                if (mProgress >= 100) {
                    cutDownListener?.onFinish()
                    mTimer.cancel()
                }
            }
        }, 100, 100)
    }

    fun stop() {
        mTimer.cancel()
    }

    fun setText(text: String) {
        mText = text
    }

    override fun setTextSize(textSize: Float) {
        mTextSize = textSize
    }

    /**
     * 设置倒计时时间,每100ms更新一次
     */
    fun setTime(time: Int) {
        mTime = time
        mDrawTimes = time / 100
        mEachDrawAngle = 360 / mDrawTimes
    }

    fun getTime(): Int {
        return mTime
    }

    fun setProgressColor(color: Int) {
        mProgressLineColor = color
    }

    fun setCircleBackgroundColor(color: Int) {
        mCircleColor = color
    }

    override fun setTextColor(color: Int) {
        mTextColor = color
    }

    fun setOnFinishListener(cutDownListener: CutDownListener) {
        this.cutDownListener = cutDownListener
    }

    interface CutDownListener {
        fun percentChange(percent: Int)
        fun onFinish()
    }

}