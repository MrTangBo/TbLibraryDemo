package com.tb.library.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.tb.library.R
import com.tb.library.tbExtend.TbItemClickInfo
import com.tb.library.tbExtend.tbGetDimensValue
import com.tb.library.tbExtend.tbImageLongPress
import java.util.*

/**
 *@作者：tb
 *@时间：2019/9/2
 *@描述：防微信9宫格图片
 */
class LGNineGridView : ViewGroup {
    private val SINGLE_IMAGE_MODE_RELATIVE_TO_SELF = 1//单图显示模式:按照原图比例计算高度
    private val SINGLE_IMAGE_MODE_SPECIFIED_RATIO = 2//单图显示模式:指定宽高比例
    private val DEFAULTWIDTHRELATIVETOPARENT = 2 / 3.0f//单张图片相对总可用宽度的比例
    private var widthRatioToParent = DEFAULTWIDTHRELATIVETOPARENT
    private var singleImageMode = SINGLE_IMAGE_MODE_SPECIFIED_RATIO
    private var space = 0
    private var grideWidth = 0
    private var grideHeight = 0
    private val urls = ArrayList<String>()
    private var mContext: Context? = null
    private var rows: Int = 0
    private var colums: Int = 0
    private var singleImageHeightRatio = 1f//单张图片相对自己宽度的比例(默认为1:和宽度相等)

    private var imageCreator: ImageCreator? = null
    var onItemClick: TbItemClickInfo = { _,_ -> Unit }
    var readQRCode: ((readStr: String) -> Unit)? = null

    interface ImageCreator {
        fun createImageView(context: Context?): TbPressImageView

        fun loadImage(context: Context?, url: String, imageView: TbPressImageView)
    }

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }

    fun setImageCreator(imageCreator: ImageCreator) {
        this.imageCreator = imageCreator
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        this.mContext = context
        val defaultSpace = tbGetDimensValue(R.dimen.x2)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LGNineGridView)
        space = typedArray.getDimension(R.styleable.LGNineGridView_space, defaultSpace.toFloat())
            .toInt()
        singleImageHeightRatio =
            typedArray.getFloat(R.styleable.LGNineGridView_singleImageRatio, 1f)
        singleImageMode =
            typedArray.getInteger(
                R.styleable.LGNineGridView_singleImageRatioMode,
                SINGLE_IMAGE_MODE_SPECIFIED_RATIO
            )
        widthRatioToParent =
            typedArray.getFloat(
                R.styleable.LGNineGridView_singleImageWidthRatioToParent,
                DEFAULTWIDTHRELATIVETOPARENT
            )
        typedArray.recycle()
    }

    //646 768
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = urls.size
        val sugMinWidth = suggestedMinimumWidth
        val minWidth = paddingLeft + paddingRight + sugMinWidth
        val totalWidth = View.resolveSizeAndState(minWidth, widthMeasureSpec, 0)
        val availableWidth = totalWidth - paddingLeft - paddingRight
        if (size == 1) {
            grideWidth = (availableWidth * widthRatioToParent).toInt()
            grideHeight = (grideWidth * singleImageHeightRatio).toInt()
            if (singleImageMode == SINGLE_IMAGE_MODE_RELATIVE_TO_SELF) {
                val view = getChildAt(0) as ImageView
                val rect = view.drawable.bounds
                val bitmapWidth = rect.width()
                val bitmapHeight = rect.height()
                grideWidth = bitmapWidth
                grideHeight = bitmapHeight
                if (grideWidth >= totalWidth) {
                    grideWidth = totalWidth
                    grideHeight = grideWidth * bitmapHeight / bitmapWidth
                }
            }
        } else {
            grideWidth = (availableWidth - space * (colums - 1)) / 3

            grideHeight = grideWidth
        }
        val height = rows * grideHeight + (rows - 1) * space + paddingTop + paddingBottom
        setMeasuredDimension(totalWidth, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var ml = l
        var mt = t
        var mr = r
        var mb = b
        val size = urls.size
        if (size == 0)
            return
        val tmpWidth = grideWidth
        val tmpHeight = grideHeight

        val childSize = childCount

        for (i in 0 until size) {
            val url = urls[i]
            var view: View? = getChildAt(i)
            if (view == null) {
                if (imageCreator == null) {
                    imageCreator = DefaultImageCreator.getInstance()
                }
                view = imageCreator!!.createImageView(context)
                addView(view)
                view.tbImageLongPress(context as AppCompatActivity, readQRCode = { readStr ->
                    readQRCode?.invoke(readStr)
                }, clickImg = {
                    onItemClick?.invoke(i, view)
                }, imageUrl = url)
            }
            imageCreator!!.loadImage(context, url, view as TbPressImageView)
            view.visibility = View.VISIBLE
            ml = i % colums * (tmpWidth + space) + paddingLeft
            mt = i / colums * (tmpHeight + space) + paddingTop
            mr = l + tmpWidth
            mb = t + tmpHeight
            view.layout(ml, mt, mr, mb)
        }

        if (size < childSize) {
            for (i in size until childSize) {
                val view = getChildAt(i) as ImageView
                view.visibility = View.GONE
            }
        }
    }

    private fun initRowAndColum(size: Int) {
        rows = (size - 1) / 3 + 1
        colums = (size - 1) % 3 + 1
        if (size == 4) {
            rows = 2
            colums = 2
            return
        } else {
            colums = 3
        }
    }

    fun setUrls(urls: List<String>?) {
        if (urls == null || urls.isEmpty()) {
            visibility = View.GONE
            return
        }
        visibility = View.VISIBLE
        if (this.urls === urls) {
            return
        }
        this.urls.clear()
        this.urls.addAll(urls)
        initRowAndColum(urls.size)
        requestLayout()
    }

    companion object {
        private val DEFAULT_SPACING = 2
        private val MAX_COUNT = 9
    }
}
