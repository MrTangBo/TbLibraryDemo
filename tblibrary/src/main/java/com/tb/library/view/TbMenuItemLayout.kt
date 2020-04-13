package com.tb.library.view

import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.flyco.roundview.RoundFrameLayout
import com.tb.library.R
import com.tb.library.base.TbApplication
import com.tb.library.databinding.TbMenuItemBinding
import com.tb.library.tbExtend.TbOnClick
import com.tb.library.tbExtend.tbDip2px
import com.tb.library.tbExtend.tbGetDimensValue
import com.tb.library.tbExtend.tbSp2px


/**
 *@作者：tb
 *@时间：2019/9/3
 *@描述：常用菜单Item
 */

class TbMenuItemLayout : RoundFrameLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    private var defaultItemBg: Int = R.drawable.bg_item_ripple
    private var defaultItemLineBg: Int = R.color.line_background

    private var defaultLeftTextColor = R.color.tb_text_black
    private var defaultLeftTextSize = context.tbSp2px(14f)

    private var defaultRightTextColor = R.color.tb_text_dark
    private var defaultRightTextSize = context.tbSp2px(12f)

    private var defaultLeftIconSrc = R.drawable.icon_close


    private var defaultRightIconSrc = R.drawable.icon_next_dark


    var itemClick: TbOnClick = {}

    var bind: TbMenuItemBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.tb_menu_item, this, true)

    private fun init(attrs: AttributeSet?) {
        bind.root.setOnClickListener {
            itemClick.invoke()
        }
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.TbMenuItemLayout)

        when (typeArray.getInt(R.styleable.TbMenuItemLayout_rightTypeFace, 0)) {
            0 -> {
                bind.rightTx.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            }
            1 -> {
                bind.rightTx.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }
        }
        when (typeArray.getInt(R.styleable.TbMenuItemLayout_leftTypeFace, 0)) {
            0 -> {
                bind.leftTx.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            }
            1 -> {
                bind.leftTx.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }
        }

        bind.root.setBackgroundResource(
            typeArray.getResourceId(
                R.styleable.TbMenuItemLayout_itemBg,
                defaultItemBg
            )
        )
        bind.bottomLine.setBackgroundColor(
            typeArray.getColor(
                R.styleable.TbMenuItemLayout_itemLineBg,
                ContextCompat.getColor(context, defaultItemLineBg)
            )
        )

        bind.leftTx.text = typeArray.getString(R.styleable.TbMenuItemLayout_leftText)
        bind.leftTx.setTextColor(
            typeArray.getColor(
                R.styleable.TbMenuItemLayout_leftTextColor,
                ContextCompat.getColor(context, defaultLeftTextColor)
            )
        )
        bind.leftTx.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            typeArray.getDimension(
                R.styleable.TbMenuItemLayout_leftTextSize,
                defaultLeftTextSize.toFloat()
            )
        )

        when (typeArray.getInt(R.styleable.TbMenuItemLayout_leftIconVisible, 0)) {
            0 -> {
                bind.leftIcon.visibility = View.VISIBLE
            }
            1 -> {
                bind.leftIcon.visibility = View.INVISIBLE
            }
            2 -> {
                bind.leftIcon.visibility = View.GONE
            }
        }

        bind.leftIcon.setImageResource(
            typeArray.getResourceId(
                R.styleable.TbMenuItemLayout_leftIconSrc,
                defaultLeftIconSrc
            )
        )

        bind.rightTx.text = typeArray.getString(R.styleable.TbMenuItemLayout_rightText)
        bind.rightTx.setPadding(
            0,
            0,
            (typeArray.getDimension(
                R.styleable.TbMenuItemLayout_rightTxPaddingEnd,
                context.tbDip2px(30f).toFloat()
            )).toInt(),
            0
        )
        bind.rightTx.setTextColor(
            typeArray.getColor(
                R.styleable.TbMenuItemLayout_rightTextColor,
                ContextCompat.getColor(context, defaultRightTextColor)
            )
        )
        bind.rightTx.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            typeArray.getDimension(
                R.styleable.TbMenuItemLayout_rightTextSize,
                defaultRightTextSize.toFloat()
            )
        )

        when (typeArray.getInt(R.styleable.TbMenuItemLayout_rightIconVisible, 0)) {
            0 -> {
                bind.rightIcon.visibility = View.VISIBLE
            }
            1 -> {
                bind.rightIcon.visibility = View.INVISIBLE
            }
            2 -> {
                bind.rightIcon.visibility = View.GONE
            }
        }


        bind.rightIcon.setImageResource(
            typeArray.getResourceId(
                R.styleable.TbMenuItemLayout_rightIconSrc,
                defaultRightIconSrc
            )
        )
        typeArray.recycle()
    }


    fun setLeftTx(
        str: CharSequence
    ): TbMenuItemLayout {
        bind.leftTx.text = str
        return this
    }

    fun setLeftTxColor(
        textColor: Int = defaultLeftTextColor
    ): TbMenuItemLayout {
        bind.leftTx.setTextColor(ContextCompat.getColor(context, textColor))
        return this
    }

    fun setLeftTxSize(
        textSize: Int = defaultLeftTextSize
    ): TbMenuItemLayout {
        bind.leftTx.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        return this
    }


    fun setRightTx(
        str: CharSequence,
        textSize: Int = defaultRightTextSize,
        textColor: Int = defaultRightTextColor
    ): TbMenuItemLayout {
        bind.rightTx.text = str
        bind.rightTx.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        bind.rightTx.setTextColor(ContextCompat.getColor(context, textColor))
        return this
    }

    fun setRightTxColor(
        textColor: Int = defaultRightTextColor
    ): TbMenuItemLayout {
        bind.rightTx.setTextColor(ContextCompat.getColor(context, textColor))
        return this
    }

    fun setRightTxSize(
        textSize: Int = defaultRightTextSize
    ): TbMenuItemLayout {
        bind.rightTx.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        return this
    }

    fun setIconLeft(@DrawableRes leftIcon: Int): TbMenuItemLayout {
        bind.leftIcon.setImageResource(leftIcon)
        return this
    }

    fun setIconRight(@DrawableRes rightIcon: Int = defaultRightIconSrc): TbMenuItemLayout {
        bind.rightIcon.setImageResource(rightIcon)
        return this
    }

    fun setLineBottomMargin(margin: Rect) {
        val lp: ConstraintLayout.LayoutParams =
            bind.bottomLine.layoutParams as ConstraintLayout.LayoutParams
        lp.setMargins(margin.left, margin.top, margin.right, margin.bottom)
        bind.bottomLine.layoutParams = lp
    }


    fun setRightTxStyle(typeFace: Int = Typeface.NORMAL) {
        bind.rightTx.typeface = Typeface.defaultFromStyle(typeFace)
    }

    fun setLeftTxStyle(typeFace: Int = Typeface.NORMAL) {
        bind.leftTx.typeface = Typeface.defaultFromStyle(typeFace)
    }

}