package com.tb.library.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.makeramen.roundedimageview.RoundedImageView;

/**
 * 作者：蓝调
 * 时间：2018-06-20
 * 描述：android图片宽度充满高度自适应
 */
public class ResizableImageView extends RoundedImageView {

    public ResizableImageView(Context context) {
        super(context);
    }

    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
            // ceil not round - avoid thin vertical gaps along the left/right edges
            int width = MeasureSpec.getSize(widthMeasureSpec);
            //高度根据使得图片的宽度充满屏幕计算而得
            int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
