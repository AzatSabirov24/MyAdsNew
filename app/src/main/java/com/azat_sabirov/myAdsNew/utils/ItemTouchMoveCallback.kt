package com.azat_sabirov.myAdsNew.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.azat_sabirov.myAdsNew.frag.FragRvAdapter

class ItemTouchMoveCallback (val adapter: FragRvAdapter): ItemTouchHelper.Callback(){
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
       val itemDrug = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(itemDrug,0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onMove(viewHolder.adapterPosition, target.adapterPosition)
       return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState!=ItemTouchHelper.ACTION_STATE_IDLE)viewHolder?.itemView?.alpha = 0.5f
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.alpha = 1.0f
        adapter.onClear()
        super.clearView(recyclerView, viewHolder)
    }
    interface ItemTouchAdapter{
        fun onMove(startPos: Int, targetPos: Int)
        fun onClear()
    }
}