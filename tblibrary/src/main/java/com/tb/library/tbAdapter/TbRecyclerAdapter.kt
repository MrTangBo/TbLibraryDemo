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

@Suppress("UNCHECKED_CAST")
abstract class TbRecyclerAdapter<T, G : ViewDataBinding>(
    var listData: ArrayList<T>,
    @LayoutRes var layoutId: Int
) :
    RecyclerView.Adapter<TbRecyclerAdapter.MyHolder<G>>() {

    var tbItemClick: TbItemClick = { _ -> Unit }//item点击事件


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder<G> {
        val itemBing: ViewDataBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false)
        if (TbConfig.getInstance().fontType.isNotEmpty()) {
            FontUtil.replaceFont(itemBing.root, TbConfig.getInstance().fontType)
        }
        return MyHolder(itemBing as G)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: MyHolder<G>, position: Int) {
        holder.itemBinding.root.setOnClickListener {
            tbItemClick.invoke(position)
        }
        onBind(holder.itemBinding, listData[position], position)
        onBind(holder,holder.itemBinding, listData[position], position)
        holder.itemBinding.executePendingBindings()
    }

    abstract fun onBind(itemBinding: G, info: T, position: Int)

    /*holder便于拖拽变化位置*/
    open fun onBind(holder: MyHolder<G>, itemBinding: G, info: T, position: Int) {}


    class MyHolder<G : ViewDataBinding>(mItemBinding: G) :
        RecyclerView.ViewHolder(mItemBinding.root) {
        var itemBinding: G = mItemBinding
    }

}