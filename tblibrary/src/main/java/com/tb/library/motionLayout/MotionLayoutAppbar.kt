package com.tb.library.motionLayout

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.appbar.AppBarLayout

class MotionLayoutAppbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr), AppBarLayout.OnOffsetChangedListener {
    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        appBarLayout?.apply {
            progress = (-verticalOffset*1.0f / totalScrollRange)
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        parent?.apply {
                this as AppBarLayout
            addOnOffsetChangedListener(this@MotionLayoutAppbar)
        }
    }
}