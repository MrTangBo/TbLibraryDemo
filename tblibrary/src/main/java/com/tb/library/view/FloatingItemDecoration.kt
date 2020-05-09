package com.tb.library.view


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tb.library.R
import java.util.*

/**
 *@作者：tb
 *@时间：2019/8/19
 *@描述：RecyclerView可吸附线
 */
class FloatingItemDecoration : RecyclerView.ItemDecoration {
    private var mDivider: Drawable? = null
    private var dividerHeight: Int = 0
    private var dividerWidth: Int = 0
    private val keys = HashMap<Int, String>()
    private var mTitleHeight: Int = 0
    private var mTextPaint: Paint? = null
    private var mBackgroundPaint: Paint? = null
    private var mTextHeight: Float = 0.toFloat()
    private var mTextBaselineOffset: Float = 0.toFloat()
    private var mContext: Context? = null
    /**
     * 滚动列表的时候是否一直显示悬浮头部
     */
    private var showFloatingHeaderOnScrolling = true

    constructor(context: Context) {
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        a.recycle()
        this.dividerHeight = mDivider!!.intrinsicHeight
        this.dividerWidth = mDivider!!.intrinsicWidth
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param drawableId 分割线图片
     */
    constructor(context: Context, @DrawableRes drawableId: Int) {
        mDivider = ContextCompat.getDrawable(context, drawableId)
        this.dividerHeight = mDivider!!.intrinsicHeight
        this.dividerWidth = mDivider!!.intrinsicWidth
    }

    /**
     * 自定义分割线
     * 也可以使用[Canvas.drawRect]或者[Canvas.drawText]等等
     * 结合[Paint]去绘制各式各样的分割线
     *
     * @param context
     * @param color         整型颜色值，非资源id
     * @param dividerWidth  单位为dp
     * @param dividerHeight 单位为dp
     */
    constructor(context: Context, @ColorRes color: Int, @Dimension dividerWidth: Int, @Dimension dividerHeight: Int) {
        mDivider = ColorDrawable(ContextCompat.getColor(context,color))
        this.dividerWidth =dividerWidth
        this.dividerHeight =dividerHeight
    }

    fun init(mContext: Context, @ColorRes titleBg: Int = R.color.tb_green, titleTxColor: Int = R.color.tb_white) {
        this.mContext = mContext
        mTextPaint = Paint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.textSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, mContext.resources.displayMetrics)
        mTextPaint!!.color = ContextCompat.getColor(mContext, titleTxColor)
        val fm = mTextPaint!!.fontMetrics
        mTextHeight = fm.bottom - fm.top//计算文字高度
        mTextBaselineOffset = fm.bottom

        mBackgroundPaint = Paint()
        mBackgroundPaint!!.isAntiAlias = true
        mBackgroundPaint!!.color = ContextCompat.getColor(mContext, titleBg)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        drawVertical(c, parent)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (!showFloatingHeaderOnScrolling) {
            return
        }
        val firstVisiblePos = (parent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (firstVisiblePos == RecyclerView.NO_POSITION) {
            return
        }
        val title = getTitle(firstVisiblePos)
        if (TextUtils.isEmpty(title)) {
            return
        }
        var flag = false
        if (getTitle(firstVisiblePos + 1) != null && title != getTitle(firstVisiblePos + 1)) {
            //说明是当前组最后一个元素，但不一定碰撞了
            //Log.e(TAG, "onDrawOver: "+"==============" +firstVisiblePos);
            val child = parent.findViewHolderForAdapterPosition(firstVisiblePos)!!.itemView
            if (child.top + child.measuredHeight < mTitleHeight) {
                //进一步检测碰撞
                //Log.e(TAG, "onDrawOver: "+child.getTop()+"$"+firstVisiblePos );
                c.save()//保存画布当前的状态
                flag = true
                c.translate(0f, (child.top + child.measuredHeight - mTitleHeight).toFloat())//负的代表向上
            }
        }
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val top = parent.paddingTop
        val bottom = top + mTitleHeight
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mBackgroundPaint!!)
        val x = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, mContext!!.resources.displayMetrics)
        val y = bottom.toFloat() - (mTitleHeight - mTextHeight) / 2 - mTextBaselineOffset//计算文字baseLine

        c.drawText(title!!, x, y, mTextPaint!!)

        if (flag) {
            //还原画布为初始状态
            c.restore()
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.getChildViewHolder(view).adapterPosition
        if (keys.containsKey(pos)) {//留出头部偏移
            outRect.set(0, mTitleHeight, 0, 0)
        } else {
            outRect.set(0, dividerHeight, 0, 0)
        }
    }

    /**
     * *如果该位置没有，则往前循环去查找标题，找到说明该位置属于该分组
     *
     * @param position
     * @return
     */
    private fun getTitle(position: Int): String? {
        var mPosition = position
        while (mPosition >= 0) {
            if (keys.containsKey(mPosition)) {
                return keys[mPosition]
            }
            mPosition--
        }
        return null
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        var top = 0
        var bottom = 0
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            if (!keys.containsKey(params.viewLayoutPosition)) {
                //画普通分割线
                top = child.top - params.topMargin - dividerHeight
                bottom = top + dividerHeight
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(c)
            } else {
                //画头部
                top = child.top - params.topMargin - mTitleHeight
                bottom = top + mTitleHeight
                c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mBackgroundPaint!!)
                //                float x=child.getPaddingLeft()+params.leftMargin;
                val x = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, mContext!!.resources.displayMetrics)
                val y = bottom.toFloat() - (mTitleHeight - mTextHeight) / 2 - mTextBaselineOffset//计算文字baseLine
                //                Log.e(TAG, "drawVertical: "+bottom );
                c.drawText(keys[params.viewLayoutPosition]!!, x, y, mTextPaint!!)
            }
        }
    }

    fun setShowFloatingHeaderOnScrolling(showFloatingHeaderOnScrolling: Boolean) {
        this.showFloatingHeaderOnScrolling = showFloatingHeaderOnScrolling
    }

    fun setKeys(keys: Map<Int, String>): FloatingItemDecoration {
        this.keys.clear()
        this.keys.putAll(keys)
        return this
    }

    fun setTitleHeight(titleHeight: Int): FloatingItemDecoration {
        this.mTitleHeight = titleHeight
        return this
    }


    companion object {
        private val TAG = "FloatingItemDecoration"
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }
}