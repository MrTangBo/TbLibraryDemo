package com.tb.library.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.liaoinstan.springview.container.BaseSimpleHeader
import com.tb.library.R

class TbDefaultHeader(
    var context: Context,
    var rotationSrc: Int = R.drawable.loading,
    var arrowSrc: Int = R.drawable.arrow
) : BaseSimpleHeader() {

    private var freshTime: Long = 0

    private var ROTATE_ANIM_DURATION = 180
    private var mRotateUpAnim: RotateAnimation? = null
    private var mRotateDownAnim: RotateAnimation? = null

    private var headerTitle: TextView? = null
    private var headerTime: TextView? = null
    private var headerArrow: ImageView? = null
    private var headerProgressbar: ProgressBar? = null

    init {
        movePara = 2.0f
        mRotateUpAnim = RotateAnimation(
            0.0f,
            -180.0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        mRotateUpAnim!!.duration = ROTATE_ANIM_DURATION.toLong()
        mRotateUpAnim!!.fillAfter = true
        mRotateDownAnim = RotateAnimation(
            -180.0f,
            0.0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        mRotateDownAnim!!.duration = ROTATE_ANIM_DURATION.toLong()
        mRotateDownAnim!!.fillAfter = true



    }


    override fun onFinishAnim() {
        headerArrow!!.visibility = View.VISIBLE
        headerProgressbar!!.visibility = View.INVISIBLE
    }

    override fun onStartAnim() {
        freshTime = System.currentTimeMillis()
        headerTitle!!.text = context.resources.getString(R.string.springRefresh)
        headerArrow!!.visibility = View.INVISIBLE
        headerArrow!!.clearAnimation()
        headerProgressbar!!.visibility = View.VISIBLE
    }

    override fun onPreDrag(rootView: View?) {
        if (freshTime == 0L) {
            freshTime = System.currentTimeMillis()
        } else {
            val m = ((System.currentTimeMillis() - freshTime) / 1000 / 60).toInt()
            if (m in 1..59) {
                headerTime!!.text = "$m ${context.resources.getString(R.string.minutesago)}"
            } else if (m >= 60 && m < 60 * 24) {
                val h = m / 60
                headerTime!!.text = "$h ${context.resources.getString(R.string.hoursago)}"
            } else if (m >= 60 * 24) {
                val d = m / (60 * 24)
                headerTime!!.text = "$d ${context.resources.getString(R.string.daysago)}"
            } else if (m == 0) {
                headerTime!!.text = context.resources.getString(R.string.just)
            }
        }
    }

    override fun onDropAnim(rootView: View?, dy: Int) {

    }

    override fun getView(inflater: LayoutInflater?, viewGroup: ViewGroup?): View {
        val view = inflater!!.inflate(R.layout.tb_include_refresh_header, viewGroup, false)
        headerTitle = view.findViewById(R.id.default_header_title)
        headerTime = view.findViewById(R.id.default_header_time)
        headerArrow = view.findViewById(R.id.default_header_arrow)
        headerProgressbar =
            view.findViewById(R.id.default_header_progressbar)
        headerProgressbar?.indeterminateDrawable = ContextCompat.getDrawable(context, rotationSrc)
        headerArrow?.setImageResource(arrowSrc)
        return view
    }

    override fun onLimitDes(rootView: View?, upORdown: Boolean) {
        if (!upORdown) {
            headerTitle!!.text = context.resources.getString(R.string.releaseRefresh)
            if (headerArrow!!.visibility == View.VISIBLE) headerArrow!!.startAnimation(
                mRotateUpAnim
            )
        } else {
            headerTitle!!.text = context.resources.getString(R.string.pullRefresh)
            if (headerArrow!!.visibility == View.VISIBLE) headerArrow!!.startAnimation(
                mRotateDownAnim
            )
        }
    }

}
