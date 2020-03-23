package com.tb.library.tbAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter

/**
 * @CreateDate: 2020/3/7 5:44
 * @Description: TODO
 * @Author: TangBo
 */
class TbPageFragmentAdapter : FragmentPagerAdapter {

    private var fragmentList: List<Fragment>? = null
    private var titles: Array<String>? = null

    constructor(fm: FragmentManager, fragmentList: List<Fragment>) : super(
        fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        this.fragmentList = fragmentList

    }

    constructor(
        fm: FragmentManager,
        fragmentList: List<Fragment>,
        titles: Array<String>
    ) : super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        this.fragmentList = fragmentList
        this.titles = titles
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList!![position]
    }

    override fun getCount(): Int {
        return fragmentList!!.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (titles != null) {
            titles!![position]
        } else super.getPageTitle(position)
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE  //没有找到child要求重新加载
    }
}