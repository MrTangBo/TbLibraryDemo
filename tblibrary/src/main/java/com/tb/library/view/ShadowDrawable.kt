package com.tb.library.view

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.ViewCompat
/**
*@作者：tb
*@时间：2019/12/9
*@描述：阴影Drawable
*/
class ShadowDrawable private constructor(
    private val mShape: Int,
    private val mBgColor: IntArray?,
    private val mShapeRadius: Int,
    shadowColor: Int,
    private val mShadowRadius: Int,
    private val mOffsetX: Int,
    private val mOffsetY: Int
) : Drawable() {
    private val mShadowPaint: Paint = Paint()
    private val mBgPaint: Paint
    private var mRect: RectF? = null

    init {
        mShadowPaint.color = Color.TRANSPARENT
        mShadowPaint.isAntiAlias = true
        mShadowPaint.setShadowLayer(mShadowRadius.toFloat(), mOffsetX.toFloat(), mOffsetY.toFloat(), shadowColor)
        mShadowPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_ATOP)

        mBgPaint = Paint()
        mBgPaint.isAntiAlias = true
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        mRect = RectF(
            (left + mShadowRadius - mOffsetX).toFloat(),
            (top + mShadowRadius - mOffsetY).toFloat(),
            (right - mShadowRadius - mOffsetX).toFloat(),
            (bottom - mShadowRadius - mOffsetY).toFloat()
        )
    }

    override fun draw(canvas: Canvas) {
        if (mBgColor != null) {
            if (mBgColor.size == 1) {
                mBgPaint.color = mBgColor[0]
            } else {
                mBgPaint.shader = LinearGradient(
                    mRect!!.left, mRect!!.height() / 2, mRect!!.right,
                    mRect!!.height() / 2, mBgColor, null, Shader.TileMode.CLAMP
                )
            }
        }

        if (mShape == SHAPE_ROUND) {
            canvas.drawRoundRect(mRect!!, mShapeRadius.toFloat(), mShapeRadius.toFloat(), mShadowPaint)
            canvas.drawRoundRect(mRect!!, mShapeRadius.toFloat(), mShapeRadius.toFloat(), mBgPaint)
        } else {
            canvas.drawCircle(
                mRect!!.centerX(),
                mRect!!.centerY(),
                Math.min(mRect!!.width(), mRect!!.height()) / 2,
                mShadowPaint
            )
            canvas.drawCircle(
                mRect!!.centerX(),
                mRect!!.centerY(),
                Math.min(mRect!!.width(), mRect!!.height()) / 2,
                mBgPaint
            )
        }
    }

    override fun setAlpha(alpha: Int) {
        mShadowPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mShadowPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    class Builder {
        private var mShape: Int = 0
        private var mShapeRadius: Int = 0
        private var mShadowColor: Int = 0
        private var mShadowRadius: Int = 0
        private var mOffsetX = 0
        private var mOffsetY = 0
        private var mBgColor: IntArray? = null

        init {
            mShape = SHAPE_ROUND
            mShapeRadius = 12
            mShadowColor = Color.parseColor("#4d000000")
            mShadowRadius = 18
            mOffsetX = 0
            mOffsetY = 0
            mBgColor = IntArray(1)
            mBgColor!![0] = Color.TRANSPARENT
        }

        fun setShape(mShape: Int): Builder {
            this.mShape = mShape
            return this
        }

        fun setShapeRadius(ShapeRadius: Int): Builder {
            this.mShapeRadius = ShapeRadius
            return this
        }

        fun setShadowColor(shadowColor: Int): Builder {
            this.mShadowColor = shadowColor
            return this
        }

        fun setShadowRadius(shadowRadius: Int): Builder {
            this.mShadowRadius = shadowRadius
            return this
        }

        fun setOffsetX(OffsetX: Int): Builder {
            this.mOffsetX = OffsetX
            return this
        }

        fun setOffsetY(OffsetY: Int): Builder {
            this.mOffsetY = OffsetY
            return this
        }

        fun setBgColor(BgColor: Int): Builder {
            this.mBgColor!![0] = BgColor
            return this
        }

        fun setBgColor(BgColor: IntArray): Builder {
            this.mBgColor = BgColor
            return this
        }

        fun builder(): ShadowDrawable {
            return ShadowDrawable(mShape, mBgColor, mShapeRadius, mShadowColor, mShadowRadius, mOffsetX, mOffsetY)
        }
    }

    companion object {

        val SHAPE_ROUND = 1
        val SHAPE_CIRCLE = 2

        fun setShadowDrawable(view: View, drawable: Drawable) {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            ViewCompat.setBackground(view, drawable)
        }

        fun setShadowDrawable(
            view: View,
            shapeRadius: Int,
            shadowColor: Int,
            shadowRadius: Int,
            offsetX: Int,
            offsetY: Int
        ) {
            val drawable = Builder()
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .builder()
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            ViewCompat.setBackground(view, drawable)
        }

        fun setShadowDrawable(
            view: View,
            bgColor: Int,
            shapeRadius: Int,
            shadowColor: Int,
            shadowRadius: Int,
            offsetX: Int,
            offsetY: Int
        ) {
            val drawable = Builder()
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .builder()
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            ViewCompat.setBackground(view, drawable)
        }

        fun setShadowDrawable(
            view: View,
            shape: Int,
            bgColor: Int,
            shapeRadius: Int,
            shadowColor: Int,
            shadowRadius: Int,
            offsetX: Int,
            offsetY: Int
        ) {
            val drawable = Builder()
                .setShape(shape)
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .builder()
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            ViewCompat.setBackground(view, drawable)
        }

        fun setShadowDrawable(
            view: View,
            bgColor: IntArray,
            shapeRadius: Int,
            shadowColor: Int,
            shadowRadius: Int,
            offsetX: Int,
            offsetY: Int
        ) {
            val drawable =Builder()
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .builder()
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            ViewCompat.setBackground(view, drawable)
        }
    }
}