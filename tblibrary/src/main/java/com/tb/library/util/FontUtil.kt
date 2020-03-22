package com.tb.library.util

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tb.library.base.TbConfig



/**
 * 作者：蓝调
 * 时间：2018-04-02
 * 描述：修改字体工具类
 */
object FontUtil {
    var typeface: Typeface? = null
    private fun getInstance(context: Context): Typeface? {
        if (typeface == null) {
            synchronized(Typeface::class.java) {
                if (typeface == null) {
                    typeface = createTypeface(context, TbConfig.getInstance().fontType)
                }
            }
        }
        return typeface
    }

    /**
     *
     * Replace the font of specified view and it's children
     *
     * @param root     The root view.
     * @param fontPath font file path relative to 'assets' directory.
     */
    fun replaceFont(root: View?, fontPath: String) {
        if (root == null) {
            return
        }
        if (root is TextView) { // If view is TextView or it's subclass, replace it's font
            var style = Typeface.NORMAL
            if (root.typeface != null) {
                style = root.typeface.style
            }
            if (TextUtils.isEmpty(fontPath)) {
                val typeface = Typeface.defaultFromStyle(style)
                root.typeface = typeface
            } else {
                root.setTypeface(getInstance(root.getContext()), style)
            }

        } else if (root is ViewGroup) { // If view is ViewGroup, apply this method on it's child views
            for (i in 0 until root.childCount) {
                replaceFont(root.getChildAt(i), fontPath)
            }
        }
    }

    /**
     *
     * Replace the font of specified view and it's children
     *
     * @param context  The view corresponding to the activity.
     * @param fontPath font file path relative to 'assets' directory.
     */
    fun replaceFont(context: Activity, fontPath: String) {
        replaceFont(getRootView(context), fontPath)
    }


    /*
     * Create a Typeface instance with your font file
     */
    fun createTypeface(context: Context, fontPath: String): Typeface? {
        var typeface: Typeface? = null
        try {
            typeface = Typeface.createFromAsset(context.assets, fontPath)
        } catch (e: Exception) {

        } finally {
            Typeface.defaultFromStyle(Typeface.NORMAL)
        }
        return typeface
    }

    /**
     * 从Activity 获取 rootView 根节点
     *
     * @param context
     * @return 当前activity布局的根节点
     */
    fun getRootView(context: Activity): View {
        return (context.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
    }

}
