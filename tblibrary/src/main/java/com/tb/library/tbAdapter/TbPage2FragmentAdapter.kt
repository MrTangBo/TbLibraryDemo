package com.tb.library.tbAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by Tb on 2020/4/24.
 * describe:ViewPager2+Fragment适配器
 */
class TbPage2FragmentAdapter(var fragmentList: List<Fragment>, fActivity: FragmentActivity) :
    FragmentStateAdapter(fActivity) {
    override fun getItemCount(): Int {
        return fragmentList.size
    }
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}