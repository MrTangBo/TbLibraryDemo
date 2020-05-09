package com.tb.library.tbExtend

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.graphics.*
import android.net.http.SslError
import android.os.Build
import android.os.Handler
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.SearchView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.forEachIndexed
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.bigkoo.convenientbanner.ConvenientBanner
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator
import com.bigkoo.convenientbanner.listener.OnPageChangeListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.liaoinstan.springview.container.BaseHeader
import com.liaoinstan.springview.container.DefaultFooter
import com.liaoinstan.springview.widget.SpringView
import com.tb.library.R
import com.tb.library.base.TbApplication
import com.tb.library.base.TbConfig
import com.tb.library.tbAdapter.TbRecyclerAdapter
import com.tb.library.tbInterface.WebViewListener
import com.tb.library.util.TbLogUtils
import com.tb.library.view.*
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView
import kotlin.math.abs

/**
 * @CreateDate: 2020/3/7 5:30
 * @Description: 控件扩展
 * @Author: TangBo
 */

/*时间倒计时*/
fun TextView.tbCountDownTime(
    mHander: Handler,
    mTotalTime: Int = 60,
    unableBg: Int = 0,
    enableBg: Int = 0,
    unableTxColor: Int = R.color.tb_text_dark,
    enableTxColor: Int = R.color.tb_text_black
): TextView {
    val mContext = this.context
    var totalTime = mTotalTime
    val view = this
    view.isEnabled = false
    view.background = if (unableBg == 0) null else ContextCompat.getDrawable(mContext, unableBg)
    view.setTextColor(ContextCompat.getColor(mContext, unableTxColor))
    mHander.post(object : Runnable {
        override fun run() {
            view.text = context?.getString(R.string.wait_second, totalTime)
            if (mContext is Activity) {
                if (mContext.isDestroyed) {

                    mHander.removeCallbacksAndMessages(null)
                    return
                }
            }
            if (totalTime == 0) {
                mHander.removeCallbacksAndMessages(null)
                view.isEnabled = true
                view.background =
                    if (enableBg == 0) null else ContextCompat.getDrawable(mContext, enableBg)
                view.setTextColor(ContextCompat.getColor(mContext, enableTxColor))
                view.text = context?.resources?.getString(R.string.reGetCodeDescribe)
                return
            }
            totalTime--
            mHander.postDelayed(this, 1000)
        }
    })
    return this
}

/*初始化图片轮播设置*/
fun ConvenientBanner<*>.initBanner(
    imageUrls: ArrayList<*>,
    autoTime: Long = 5000L,
    isCanLoop: Boolean = true,
    @LayoutRes layoutId: Int = R.layout.tb_item_banner,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP,
    @DrawableRes indicator: Int = R.drawable.tb_back_white,
    @DrawableRes indicatorFocus: Int = R.drawable.icon_search_dark,
    align: ConvenientBanner.PageIndicatorAlign = ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL,
    itemClick: TbItemClick = { _ -> Unit },
    onPageSelected: TbItemClick = { _ -> Unit },
    onScrolled: TbOnScrolled = { _, _, _ -> Unit },
    onScrollStateChanged: TbOnScrollStateChanged = { _, _ -> Unit },
    indicatorMarginRect: Rect? = null,//
    itemMarginRect: Rect = Rect(tbGetDimensValue(R.dimen.x20), 0, tbGetDimensValue(R.dimen.x20), 0),
    circleSizeRect: Rect = Rect(0, 0, 0, 0),
    itemBinding: ((binding: ViewDataBinding, data: Any) -> Unit)? = null,
    placeholder: Int = TbConfig.getInstance().placeholder,
    error: Int = TbConfig.getInstance().errorHolder
): ConvenientBanner<*> {

    var mNewState = -999

    (this as ConvenientBanner<Any>).setPages(object : CBViewHolderCreator {
        override fun createHolder(itemView: View?): TbBannerHolderView<Any> {
            return TbBannerHolderView(
                itemView,
                circleSizeRect,
                itemMarginRect,
                scaleType, placeholder, error
            ) { binding, data ->
                itemBinding?.invoke(binding, data)
            }
        }

        override fun getLayoutId(): Int {
            return layoutId
        }
    }, imageUrls)
        //设置两个点图片作为翻页指示器，不设置则没有指示器
        .setPageIndicator(intArrayOf(indicator, indicatorFocus))
        .setPageIndicatorAlign(align)
        .setOnPageChangeListener(object : OnPageChangeListener {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                onScrolled.invoke(recyclerView, dx, dy)
            }

            override fun onPageSelected(index: Int) {
                if (mNewState == 0) {
                    onPageSelected.invoke(index)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                mNewState = newState
                onScrollStateChanged.invoke(recyclerView, newState)
            }
        })
        .setOnItemClickListener {
            itemClick.invoke(it)
        }.startTurning(autoTime).isCanLoop = isCanLoop

    /*设置指示器的边距*/
    if (indicatorMarginRect != null) {
        try {
            val field = ConvenientBanner::class.java.getDeclaredField("loPageTurningPoint")
            field.isAccessible = true
            val viewGroup: ViewGroup = field.get(this) as ViewGroup
            val layoutParams = viewGroup.layoutParams as RelativeLayout.LayoutParams
            layoutParams.setMargins(
                indicatorMarginRect.left,
                indicatorMarginRect.top,
                indicatorMarginRect.right,
                indicatorMarginRect.bottom
            )
        } catch (e: Exception) {
        }
    }
    return this
}


/*RecyclerView初始化*/
inline fun <T, G : ViewDataBinding> RecyclerView?.init(
    adapter: TbRecyclerAdapter<T, G>?,
    mLayoutManager: Class<*> = LinearLayoutManager::class.java,
    mSpanCount: Int = 2,
    noinline itemClick: TbItemClick = { _ -> Unit },
    crossinline scrollYListener: (scrollY: Int, isTopDirection: Boolean) -> Unit = { _, _ -> Boolean },//isTop代表是否上滑
    reverseLayout: Boolean = false,//是否倒叙
    orientation: Int = RecyclerView.VERTICAL,
    dividerOrientation: Int = TbConfig.HORIZONTAL_LIST,//分割线的样式
    dividerColor: Int = R.color.line_background,//分割线的颜色
    dividerSize: Int = this!!.tbGetDimensValue(R.dimen.x1),//分割线的宽度或者高度
    headerViews: ArrayList<Int> = arrayListOf(),//添加头部
    footerViews: ArrayList<Int> = arrayListOf(),//添加尾部
    floatTitleDecoration: FloatingItemDecoration? = null//是否吸附title
): ArrayList<ViewDataBinding> {
    val b: ArrayList<ViewDataBinding> = arrayListOf()
    if (this == null) {
        return b
    }
    if (adapter == null) {
        return b
    }
    var scrollY = 0
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            scrollY += dy
            scrollYListener.invoke(scrollY, dy > 0)
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val first = IntArray(2)
            if (mLayoutManager.simpleName == "StaggeredGridLayoutManager") {
                val l: StaggeredGridLayoutManager = layoutManager as StaggeredGridLayoutManager
                l.findFirstCompletelyVisibleItemPositions(first)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (first[0] == 1 || first[1] == 1)) {
                    l.invalidateSpanAssignments()//这行主要解决了当加载更多数据时，底部需要重绘，否则布局可能衔接不上。
                }
            }
        }
    })

    when (mLayoutManager.simpleName) {
        /*线性布局 包括垂直和水平*/
        "LinearLayoutManager" -> {
            layoutManager = LinearLayoutManager(this.context, orientation, reverseLayout)
            if (floatTitleDecoration != null) {
                addItemDecoration(floatTitleDecoration)
            } else {
                addItemDecoration(
                    TbDividerItemDecoration(
                        context,
                        dividerOrientation,
                        dividerSize,
                        ContextCompat.getColor(context, dividerColor)
                    )
                )
            }
        }
        "GridLayoutManager" -> {
            layoutManager = GridLayoutManager(this.context, mSpanCount)
            addItemDecoration(
                TbDividerItemDecoration(
                    context,
                    TbConfig.BOTH_SET,
                    dividerSize,
                    ContextCompat.getColor(context, dividerColor)
                )
            )
        }
        "StaggeredGridLayoutManager" -> {
            layoutManager = StaggeredGridLayoutManager(mSpanCount, orientation)
        }

        "TbFlowLayoutManager" -> {
            layoutManager = TbFlowLayoutManager()
        }
    }
    this.adapter = adapter
    adapter.tbItemClick = itemClick

    if (this is TbHeaderRecyclerView) {
        //添加头部
        if (headerViews.isNotEmpty()) {
            headerViews.forEach {
                val bind: ViewDataBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(context), it, this, false)
                b.add(bind)
                addHeaderView(bind.root)
            }
        }
        //添加尾部
        if (footerViews.isNotEmpty()) {
            footerViews.forEach {
                val bind: ViewDataBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(context), it, this, false)
                b.add(bind)
                addFooterView(bind.root)
            }
        }
    }
    return b
}


/*系统TabLayout*/
inline fun TabLayout.init(
    titles: ArrayList<CharSequence>,
    selectColor: Int = R.color.tb_green,
    selectSize: Int = R.dimen.tb_text26,
    unSelectColor: Int = R.color.tb_text_black,
    unSelectSize: Int = R.dimen.tb_text26,
    selectItemBg: Int = R.color.tb_transparent,
    unSelectItemBg: Int = R.color.tb_transparent,
    itemPadding: Rect = Rect(),
    icons: ArrayList<Int> = arrayListOf(),
    crossinline mOnTabSelected: TbItemClick = { _ -> Unit },
    textGravity: Int = Gravity.CENTER,
    textStyleSelect: Int = Typeface.NORMAL,
    textStyleUnSelect: Int = Typeface.NORMAL,
    viewPager: ViewPager? = null,
    viewPager2: ViewPager2? = null
) {
    val lp = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    titles.forEachIndexed { index, charSequence ->
        val tex = TextView(context)
        tex.text = charSequence
        tex.layoutParams = lp
        val tab = if (icons.isEmpty()) {
            newTab().setCustomView(tex)
        } else {
            newTab().setCustomView(tex).setIcon(icons[index])
        }
        tab.view.forEachIndexed { _, view ->
            if (view is TextView) {
                view.gravity = textGravity
                view.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    tbGetDimensValue(unSelectSize).toFloat()
                )
                view.setTextColor(ContextCompat.getColor(context, unSelectColor))
                view.background = ContextCompat.getDrawable(context, unSelectItemBg)
                view.setPadding(
                    itemPadding.left,
                    itemPadding.top,
                    itemPadding.right,
                    itemPadding.bottom
                )
                view.typeface = Typeface.defaultFromStyle(textStyleUnSelect)
                if (index == 0) {
                    view.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        tbGetDimensValue(selectSize).toFloat()
                    )
                    view.setTextColor(ContextCompat.getColor(context, selectColor))
                    view.typeface = Typeface.defaultFromStyle(textStyleSelect)
                    view.background = ContextCompat.getDrawable(context, selectItemBg)
                }
            }
        }
        addTab(tab)
    }

    viewPager?.tbOnPageListener(onPageSelected = {
        this.getTabAt(it)?.select()
    })
    viewPager2?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            this@init.getTabAt(position)?.select()
        }
    })
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) {
        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {
            p0?.view?.forEachIndexed { _, view ->
                if (view is TextView) {
                    view.typeface = Typeface.defaultFromStyle(textStyleUnSelect)
                    view.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        tbGetDimensValue(unSelectSize).toFloat()
                    )
                    view.setTextColor(ContextCompat.getColor(context, unSelectColor))
                    view.background = ContextCompat.getDrawable(context, unSelectItemBg)
                }

            }
        }

        override fun onTabSelected(p0: TabLayout.Tab?) {
            p0?.view?.forEachIndexed { _, view ->
                viewPager?.currentItem = p0.position
                viewPager2?.currentItem = p0.position
                mOnTabSelected.invoke(p0.position)
                if (view is TextView) {
                    view.typeface = Typeface.defaultFromStyle(textStyleSelect)
                    view.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        tbGetDimensValue(selectSize).toFloat()
                    )
                    view.setTextColor(ContextCompat.getColor(context, selectColor))
                    view.background = ContextCompat.getDrawable(context, selectItemBg)
                }
            }
        }
    })

}

/*ViewPager监听*/
inline fun ViewPager.tbOnPageListener(
    crossinline onPageSelected: TbItemClick = { _ -> Unit },
    crossinline onPageScrolled: TbOnPageScrolled = { _, _, _ -> Unit },
    crossinline onPageScrollStateChanged: TbOnPageScrollStateChanged = { _ -> Unit }
) {
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            onPageScrollStateChanged.invoke(state)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            onPageScrolled.invoke(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            onPageSelected.invoke(position)
        }
    })
}

inline fun ViewPager.doPageSelected(crossinline onPageSelected: TbItemClick) {
    tbOnPageListener(onPageSelected = onPageSelected)
}

inline fun ViewPager.doPageScrolled(crossinline PageScrolled: TbOnPageScrolled) {
    tbOnPageListener(onPageScrolled = PageScrolled)
}

inline fun ViewPager.doPageScrollStateChanged(crossinline onPageScrollStateChanged: TbOnPageScrollStateChanged) {
    tbOnPageListener(onPageScrollStateChanged = onPageScrollStateChanged)
}

/*初始化SearchView*/
inline fun SearchView.init(
    textColor: Int = R.color.tb_text_black,
    textHitStr: CharSequence = context.resources.getString(R.string.search),
    textHitColor: Int = R.color.tb_text_dark,
    isExpand: Boolean = true,
    crossinline getViews: (mSearchButton: ImageView, mCloseButton: ImageView, mCollapsedButton: ImageView, mSearchAutoComplete: SearchView.SearchAutoComplete) -> Unit = { _, _, _, _ -> Unit },
    textSize: Int = tbGetDimensValue(R.dimen.tb_text28),
    searchBg: Int = R.drawable.tb_bg_search,
    searchViewHeight: Int = tbGetDimensValue(R.dimen.x70),
    @DrawableRes mSearchIcon: Int = R.drawable.icon_search_dark,
    @DrawableRes mCloseIcon: Int = R.drawable.icon_close,
    @DrawableRes mCollapsedIcon: Int = R.drawable.icon_search_dark,
    isClick: Boolean = false,
    isFocus: Boolean = false,//是否自动
    crossinline onSearchClick: TbOnClick = { },
    crossinline onExpand: TbOnClick = { },
    crossinline closeListener: TbOnClick = { },
    crossinline onQueryChange: (str: String) -> Unit = { _ -> Unit },//内容变化监听
    crossinline onQuerySubmit: (str: String) -> Unit = { _ -> Unit }//提交监听
) {

    val p = this.layoutParams
    p.height = searchViewHeight
    this.layoutParams = p

    visibility = View.VISIBLE
    background = ContextCompat.getDrawable(this.context, searchBg)

    setIconifiedByDefault(false)
    if (isExpand) {
        onActionViewExpanded()
    } else {
        onActionViewCollapsed()
    }
    val mSearchButton: ImageView
    val mCloseButton: ImageView
    val mCollapsedButton: ImageView
    val field1 = this.javaClass.getDeclaredField("mSearchButton")
    val field2 = this.javaClass.getDeclaredField("mCloseButton")
    val field3 = this.javaClass.getDeclaredField("mCollapsedIcon")
    field1.isAccessible = true
    field2.isAccessible = true
    field3.isAccessible = true
    mSearchButton = field1.get(this) as ImageView
    mCloseButton = field2.get(this) as ImageView
    mCollapsedButton = field3.get(this) as ImageView

    mSearchButton.setImageResource(mSearchIcon)
    mCollapsedButton.setImageResource(mCollapsedIcon)
    mCloseButton.setImageResource(mCloseIcon)

    val mSearchAutoComplete: SearchView.SearchAutoComplete = this.findViewById(R.id.search_src_text)
    if (!isFocus) {
        mSearchAutoComplete.clearFocus()
        this.clearFocus()
    }
    mSearchAutoComplete.hint = textHitStr
    mSearchAutoComplete.setHintTextColor(ContextCompat.getColor(this.context, textHitColor))
    mSearchAutoComplete.setTextColor(ContextCompat.getColor(this.context, textColor))
    mSearchAutoComplete.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
    mSearchAutoComplete.background = null
    if (isClick) {
        mSearchAutoComplete.isFocusable = false
        mSearchAutoComplete.isFocusableInTouchMode = false
        mSearchAutoComplete.setOnClickListener {
            onSearchClick.invoke()
        }
    }

    //搜索图标按钮(打开搜索框的按钮)的点击事件
    setOnSearchClickListener {
        onExpand.invoke()
    }

    /*关闭的监听*/
    setOnCloseListener {
        closeListener.invoke()
        return@setOnCloseListener false
    }

    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(p0: String?): Boolean {
            onQuerySubmit.invoke(p0!!)
            return false
        }

        override fun onQueryTextChange(p0: String?): Boolean {
            onQueryChange.invoke(p0!!)
            return false
        }
    })
    getViews.invoke(mSearchButton, mCloseButton, mCollapsedButton, mSearchAutoComplete)
}


/*发送通知*/
fun NotificationManager.tbNotify(
    title: String,
    content: String,
    @DrawableRes largeIcon: Int,
    @DrawableRes smallIcon: Int,
    tartActivityClass: Class<*>,
    channelId: String = "default",
    channelName: String = "Default Channel",
    notifyId: Int = 1,
    flags: Int = NotificationCompat.FLAG_AUTO_CANCEL

): NotificationCompat.Builder {
    val mBuilder: NotificationCompat.Builder
    val chan: NotificationChannel
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (getNotificationChannel(channelId) == null) {
            chan =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            chan.setShowBadge(false)//禁止该渠道使用角标
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            chan.enableLights(true)
            // 设置通知出现时的震动（如果 android 设备支持的话）
            chan.enableVibration(false)
            //如上设置使手机：静止1秒，震动2秒，静止1秒，震动3秒
            chan.vibrationPattern = longArrayOf(1000, 400)
            chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC//设置锁屏是否显示通知
            chan.lightColor = Color.BLUE
            chan.setBypassDnd(true)//设置是否可以绕过请勿打扰模式
            createNotificationChannel(chan)
        }
        mBuilder = NotificationCompat.Builder(TbApplication.mApplicationContext, channelId)
    } else {
        mBuilder = NotificationCompat.Builder(TbApplication.mApplicationContext)
        mBuilder.priority = NotificationCompat.PRIORITY_MAX
        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
    }
    val peddingIntent = PendingIntent.getActivity(
        TbApplication.mApplicationContext,
        0,
        Intent(
            TbApplication.mApplicationContext,
            tartActivityClass
        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    mBuilder.setContentTitle(title)
        .setContentText(content)
        .setLargeIcon(
            BitmapFactory.decodeResource(
                TbApplication.mApplicationContext.resources,
                largeIcon
            )
        ).setSmallIcon(smallIcon)
        .setWhen(System.currentTimeMillis())
        .setContentIntent(peddingIntent)
        .setProgress(100, 0, false)
    val n = mBuilder.build()
    n.flags = flags
    notify(notifyId, n)
    return mBuilder
}

/*设置数字标记*/
inline fun View.tbShowBadgeNum(
    num: Int = 0,
    bgColor: Int = R.color.tb_green,
    txColor: Int = R.color.tb_white,
    crossinline moveUpListener: (badge: Badge, targetView: View) -> Unit = { _, _ -> Unit },
    padding: Int = tbGetDimensValue(R.dimen.x10),
    txSize: Int = tbGetDimensValue(R.dimen.x22),
    gravity: Int = Gravity.END or Gravity.TOP
): Badge {
    val bb = QBadgeView(context).bindTarget(this)
        .setBadgeGravity(gravity)
        .setBadgeBackgroundColor(ContextCompat.getColor(context, bgColor))
        .setBadgeTextColor(ContextCompat.getColor(context, txColor))
        .setBadgeTextSize(txSize.toFloat(), false)
        .setBadgePadding(padding.toFloat(), false)
    bb.badgeNumber = num

    bb.setOnDragStateChangedListener { dragState, badge, targetView ->
        if (dragState == Badge.OnDragStateChangedListener.STATE_SUCCEED) {
            moveUpListener.invoke(badge, targetView)
        }
    }
    return bb
}

/*AppBarLayout 根据制定的滑动高度得到比例因子,达到透明度的变化*/
inline fun AppBarLayout.scrollScale(
    targetHeight: Float,
    crossinline scaleValue: (scaleValue: Float, scrollY: Int) -> Unit = { _, _ -> Unit }
) {
    var scale = 0f
    this.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, p1 ->
        scale = abs(p1) / targetHeight
        if (scale > 1) {
            scale = 1f
        } else if (scale < 0) {
            scale = 0f
        }
        scaleValue.invoke(scale, p1)
        TbLogUtils.log("tb--->$scale")
    })
}

/*初始化WebView*/
@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
inline fun WebView.init(
    url: String,
    js2Android: Any? = null,//js调用android类对象
    js2AndroidNames: ArrayList<String> = arrayListOf(),//js调用android的方法名集合
    android2Js: String = "",//android调用Js传参
    crossinline android2JsCallBack: TbOnClickInfo = { _ -> Unit },//android调用Js回调
    loadListener: WebViewListener? = null//android调用Js回调
) {
    val settings = settings
    //缩放操作
    settings.setSupportZoom(false) //支持缩放，默认为true。是下面那个的前提。
    settings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
    settings.displayZoomControls = true //隐藏原生的缩放控件
    //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
    settings.javaScriptEnabled = true
    //设置自适应屏幕，两者合用
    settings.useWideViewPort = true //将图片调整到适合webview的大小
    settings.loadWithOverviewMode = true // 缩放至屏幕的大小
    //其他细节操作
    settings.cacheMode = WebSettings.LOAD_DEFAULT //关闭webview中缓存
    settings.allowFileAccess = true //设置可以访问文件
    settings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
    settings.domStorageEnabled = true//开启DOM storage API功能
    settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
    settings.loadsImagesAutomatically = true //支持自动加载图片
    settings.blockNetworkImage = false//解决图片不显示
    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW


    /*js调用Android*/
    js2AndroidNames.forEach {
        addJavascriptInterface(js2Android, it)
    }
    /*Android调用js*/
    if (android2Js.isNotEmpty()) {
        evaluateJavascript(android2Js) {
            android2JsCallBack.invoke(it)
        }
    }
    webViewClient = object : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            loadListener?.loadStart(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            loadListener?.loadComplete(view, url)
            val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            //重新测量
            this@init.measure(w, h)
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            // 默认是handle.cancel()的，即遇到错误即中断
            handler?.proceed()
        }
    }
    webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            loadListener?.loading(view, newProgress)
        }
    }
    /*禁止长按事件*/
    /*WebView.HitTestResult.UNKNOWN_TYPE //未知类型
      WebView.HitTestResult.PHONE_TYPE //电话类型
      WebView.HitTestResult.EMAIL_TYPE //电子邮件类型
      WebView.HitTestResult.GEO_TYPE //地图类型
      WebView.HitTestResult.SRC_ANCHOR_TYPE //超链接类型
      WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE //带有链接的图片类型
      WebView.HitTestResult.IMAGE_TYPE //单纯的图片类型
      WebView.HitTestResult.EDIT_TEXT_TYPE //选中的文字类型*/
    setOnLongClickListener {
        true
    }
    loadUrl(url)
}

/*SpringView初始化*/
fun SpringView.init(
    listener: SpringView.OnFreshListener,
    header: BaseHeader? = TbDefaultHeader(this.context),
    footer: BaseHeader? = DefaultFooter(this.context),
    springType: SpringView.Type = SpringView.Type.OVERLAP,
    springGive: SpringView.Give = SpringView.Give.BOTH
): SpringView {
    if (header != null) {
        this.header = header
    }
    if (footer != null) {
        this.footer = footer
    }
    this.setListener(listener)
    this.setGive(springGive)
    this.type = springType
    this.setMoveTime(500)
    return this
}

