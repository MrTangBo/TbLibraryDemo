package com.tb.library.tbInterface

import android.graphics.Bitmap
import android.webkit.WebView

interface WebViewListener {

    fun loadStart(view: WebView?, url: String?, favicon: Bitmap?) {}
    fun loading(view: WebView?, newProgress: Int) {}
    fun loadComplete(view: WebView?, url: String?) {}


}