package com.announce.framework.utils

import androidx.recyclerview.widget.DiffUtil
import com.announce.common.domain.DiffListItem

class AnnounceDiffUtilCallback<T: DiffListItem<T>>(
        val newList: List<T>,
        val oldList: List<T>
): DiffUtil.Callback() {


    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]
                .areItemsTheSame(newList[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]
                .areContentsTheSame(newList[newItemPosition])
    }
}