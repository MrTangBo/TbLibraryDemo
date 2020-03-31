package com.tb.library.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.core.view.forEachIndexed
import androidx.viewpager.widget.ViewPager
import com.tb.library.R
import com.tb.library.tbExtend.*
import q.rorbin.badgeview.Badge

/**
 *@作者：tb
 *@时间：2019/9/5
 *@描述：底部导航栏控件
 */
class TbBottomNavigation : RadioGroup {

    private var mUnSelectTxColor: Int = R.color.tb_text_black
    private var mUnSelectTxSize: Int = tbGetDimensValue(R.dimen.x28)
    private var mSelectTxColor: Int = R.color.tb_green
    private var mSelectTxSize: Int = tbGetDimensValue(R.dimen.x28)
    private var mIsCenterBulge: Boolean = false
    private var mCenterHeight: Int = tbGetDimensValue(R.dimen.x108)
    private var mBg: Int = R.drawable.bg_item_ripple
    private var mPaddingTop: Int = tbGetDimensValue(R.dimen.x5)
    private var mPaddingBottom: Int = tbGetDimensValue(R.dimen.x5)

    constructor(mContext: Context) : super(mContext, null)
    constructor(mContext: Context, mAttributes: AttributeSet) : super(mContext, mAttributes) {
        init(mAttributes)
    }

    private fun init(mAttributes: AttributeSet) {
        orientation = HORIZONTAL
        val typeArray = context.obtainStyledAttributes(mAttributes, R.styleable.TbBottomNavigation)
        background = ContextCompat.getDrawable(
            context,
            typeArray.getResourceId(R.styleable.TbBottomNavigation_bg, mBg)
        )
        mUnSelectTxColor =
            typeArray.getResourceId(
                R.styleable.TbBottomNavigation_unSelectTxColor,
                R.color.tb_text_black
            )
        mUnSelectTxSize =
            typeArray.getDimensionPixelSize(
                R.styleable.TbBottomNavigation_unSelectTxSize,
                mUnSelectTxSize
            )
        mSelectTxColor =
            typeArray.getResourceId(R.styleable.TbBottomNavigation_selectTxColor, R.color.tb_green)
        mSelectTxSize = typeArray.getDimensionPixelSize(
            R.styleable.TbBottomNavigation_selectTxSize,
            mSelectTxSize
        )
        mIsCenterBulge = typeArray.getBoolean(R.styleable.TbBottomNavigation_isCenterBulge, false)
        mCenterHeight = typeArray.getDimensionPixelSize(
            R.styleable.TbBottomNavigation_centerHeight,
            mCenterHeight
        )
        mPaddingTop =
            typeArray.getDimensionPixelSize(R.styleable.TbBottomNavigation_paddingTop, mPaddingTop)
        mPaddingBottom = typeArray.getDimensionPixelSize(
            R.styleable.TbBottomNavigation_paddingBottom,
            mPaddingBottom
        )
        if (mIsCenterBulge) {
            clipChildren = false
            gravity = Gravity.BOTTOM
        }
        typeArray.recycle()
    }

    fun setTitle(
        titles: ArrayList<String>,
        unSelectDrawables: ArrayList<Int>? = null,
        selectDrawables: ArrayList<Int>? = null,
        iconSize: Int = 0,
        mViewPager: ViewPager? = null,
        mDefaultCheckPosition: Int = 0,//默认选中
        pageSelect: TbItemClick = { _ -> Unit },
        clickPosition: TbItemClick = { _ -> Unit }
    ): TbBottomNavigation {

        val unSelectList = arrayListOf<Drawable>()
        val selectList = arrayListOf<Drawable>()

        unSelectDrawables?.forEachIndexed { index, i ->
            val drawable = ContextCompat.getDrawable(context, i)
            val drawable_s = ContextCompat.getDrawable(context, selectDrawables!![index])
            drawable?.setBounds(0, 0, if(iconSize!=0) iconSize else drawable.minimumWidth,if(iconSize!=0) iconSize else drawable.minimumHeight)
            drawable_s?.setBounds(0, 0,if(iconSize!=0) iconSize else drawable_s.minimumWidth,if(iconSize!=0) iconSize else drawable_s.minimumHeight)
            selectList.add(drawable_s!!)
            unSelectList.add(drawable!!)
        }

        titles.forEachIndexed { index, s ->
            val radioButton: RadioButton =
                LayoutInflater.from(context).inflate(
                    R.layout.item_radio_button,
                    this,
                    false
                ) as RadioButton
            radioButton.tag = index
            radioButton.setPadding(0, mPaddingTop, 0, mPaddingBottom)
            val params = radioButton.layoutParams
            if (titles.size >= 3 && titles.size % 2 != 0 && mIsCenterBulge && index == (titles.size - 1) / 2) {
                params.height = mCenterHeight
            }
            radioButton.text = s
            radioButton.setTextColor(ContextCompat.getColor(context, mUnSelectTxColor))
            if (unSelectList.isEmpty() || selectList.isEmpty()) {
                radioButton.gravity = Gravity.CENTER
            } else {
                if (unSelectList.isNotEmpty()) {
                    radioButton.setCompoundDrawables(null, unSelectList[index], null, null)
                }
                radioButton.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            }
            radioButton.setOnClickListener { _ ->
                clickPosition.invoke(index)
                forEachIndexed { i, it ->
                    if (it is RadioButton) {
                        it.isChecked = false
                        it.setTextColor(ContextCompat.getColor(context, mUnSelectTxColor))
                        it.setTextSize(TypedValue.COMPLEX_UNIT_PX, mUnSelectTxSize.toFloat())
                        if (unSelectList.isNotEmpty()) {
                            it.setCompoundDrawables(null, unSelectList[i], null, null)
                        }
                    } else if (it is ViewGroup) {
                        if (it.getChildAt(0) != null) {
                            val view = it.getChildAt(0)
                            if (view is RadioButton) {
                                view.isChecked = false
                                view.setTextColor(ContextCompat.getColor(context, mUnSelectTxColor))
                                view.setTextSize(
                                    TypedValue.COMPLEX_UNIT_PX,
                                    mUnSelectTxSize.toFloat()
                                )
                                if (unSelectList.isNotEmpty()) {
                                    view.setCompoundDrawables(null, unSelectList[i], null, null)
                                }
                            }
                        }
                    }
                }
                radioButton.isChecked = true
                radioButton.setTextColor(ContextCompat.getColor(context, mSelectTxColor))
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSelectTxSize.toFloat())
                if (selectList.isNotEmpty()) {
                    radioButton.setCompoundDrawables(null, selectList[index], null, null)
                }
                if (mViewPager != null) {
                    mViewPager.currentItem = index
                }
            }
            addView(radioButton, params)
        }
        /*初始化第一个选中*/
        val firstView: View = getChildAt(mDefaultCheckPosition)
        firstView.performClick()
        if (mViewPager == null) return this
        mViewPager.doPageSelected {
            val view = getChildAt(it)
            if (view is RadioButton) {
                view.performClick()
            } else if (view is ViewGroup) {
                if (view.getChildAt(0) != null) {
                    val view1 = view.getChildAt(0)
                    view1.performClick()
                }
            }
            pageSelect.invoke(it)
        }
        return this
    }

    /*同时设置多个*/
    inline fun setBadgeNumList(
        positions: ArrayList<Int>,//哪些位置需要显示数字标记
        nums: ArrayList<Int>,//对应位置需要显示的数字
        bgColor: Int = R.color.tb_green,
        txColor: Int = R.color.white,
        crossinline moveUpListener: (badge: Badge, targetView: View) -> Unit = { _, _ -> Unit },
        padding: Int = tbGetDimensValue(R.dimen.x10),
        txSize: Int = tbGetDimensValue(R.dimen.x22),
        gravity: Int = Gravity.END or Gravity.TOP
    ): ArrayList<Badge> {
        val badges = arrayListOf<Badge>()
        positions.forEachIndexed { index, i ->
            if (i >= childCount) {
                tbShowToast(context.resources.getString(R.string.arrayOut))
            } else {
                val view = getChildAt(i)
                badges.add(
                    view.tbShowBadgeNum(
                        nums[index],
                        bgColor,
                        txColor,
                        moveUpListener,
                        padding,
                        txSize,
                        gravity
                    )
                )
            }
        }
        return badges
    }

    /*单个设置*/
    inline fun setBadgeNumSingle(
        position: Int,//哪些位置需要显示数字标记
        num: Int,//对应位置需要显示的数字
        bgColor: Int = R.color.tb_green,
        txColor: Int = R.color.white,
        crossinline moveUpListener: (badge: Badge, targetView: View) -> Unit = { _, _ -> Unit },
        padding: Int = tbGetDimensValue(R.dimen.x10),
        txSize: Int = tbGetDimensValue(R.dimen.x22),
        gravity: Int = Gravity.END or Gravity.TOP
    ): Badge {
        val view = getChildAt(position)
        return view.tbShowBadgeNum(num, bgColor, txColor, moveUpListener, padding, txSize, gravity)
    }
}
