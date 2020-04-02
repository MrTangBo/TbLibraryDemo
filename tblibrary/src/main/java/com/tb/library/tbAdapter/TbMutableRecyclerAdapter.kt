package com.tb.library.tbAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.tb.library.base.TbConfig
import com.tb.library.tbExtend.TbItemClick
import com.tb.library.util.FontUtil

/**
 * Created by Tb on 2020/4/2.
 * describe:多布局item适配器
 */
abstract class TbMutableRecyclerAdapter<T, G : ViewDataBinding>(var listData: ArrayList<T>) :
    RecyclerView.Adapter<TbMutableRecyclerAdapter.MyHolder<G>>() {

    var tbItemClick: TbItemClick = { _ -> Unit }//item点击事件

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder<G> {
        val itemBing: ViewDataBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false)
        if (TbConfig.getInstance().fontType.isNotEmpty()) {
            FontUtil.replaceFont(itemBing.root, TbConfig.getInstance().fontType)
        }
        return MyHolder(itemBing as G)
    }

    
    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: MyHolder<G>, position: Int) {
        holder.itemBinding.root.setOnClickListener {
            tbItemClick.invoke(position)
        }
        onBind(holder.itemBinding, listData[position], position)
        holder.itemBinding.executePendingBindings()
    }

    abstract fun onBind(itemBinding: G, info: T, position: Int)


    class MyHolder<G : ViewDataBinding>(mItemBinding: G) :
        RecyclerView.ViewHolder(mItemBinding.root) {
        var itemBinding: G = mItemBinding
    }


}