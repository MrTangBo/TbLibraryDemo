package com.tb.library.view

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.tb.library.tbExtend.tbShowToast
import java.util.*


/**
 * Created by Tb on 2021/3/2.
 * describe:
 */
open class TbItemTouchHelperCallback<T>(
    var datas: ArrayList<T>,
    var updateData: (ArrayList<T>) -> Unit = {}

) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (recyclerView.layoutManager is GridLayoutManager) {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            val swipeFlags = 0
            makeMovementFlags(
                dragFlags,
                swipeFlags
            )
        } else {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = 0
            //final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            makeMovementFlags(
                dragFlags,
                swipeFlags
            )
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // 不同Type之间不可移动
        if (viewHolder.itemViewType != target.itemViewType) {
            return false;
        }
        //得到当拖拽的viewHolder的Position
        val fromPosition = viewHolder.adapterPosition
        //拿到当前拖拽到的item的viewHolder
        val toPosition = target.adapterPosition
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(datas, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(datas, i, i - 1)
            }
        }
        recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
        updateData.invoke(datas)
        return true
    }


    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }


}