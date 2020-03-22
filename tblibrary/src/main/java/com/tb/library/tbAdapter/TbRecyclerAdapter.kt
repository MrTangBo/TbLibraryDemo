package com.tb.library.tbAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tb.library.base.TbConfig
import com.tb.library.tbExtend.TbItemClick
import com.tb.library.util.FontUtil

/**
 * @CreateDate: 2020/3/7 5:39
 * @Description: RecyclerView适配器基类
 * @Author: TangBo
 */

abstract class TbRecyclerAdapter(var listData: ArrayList<*>, @LayoutRes var layoutId: Int ) :
    RecyclerView.Adapter<TbRecyclerAdapter.MyHolder>() {

    var tbItemClick: TbItemClick = null//item点击事件

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val itemBing: ViewDataBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false)
        if (TbConfig.getInstance().fontType.isNotEmpty()) {
            FontUtil.replaceFont(itemBing.root, TbConfig.getInstance().fontType)
        }
        return MyHolder(itemBing)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        onBind(holder, position)
        holder.itemBinding.executePendingBindings()
    }

    abstract fun onBind(holder: MyHolder, position: Int)


    class MyHolder(mItemBinding: ViewDataBinding) : RecyclerView.ViewHolder(mItemBinding.root) {
        var itemBinding: ViewDataBinding = mItemBinding
    }


}