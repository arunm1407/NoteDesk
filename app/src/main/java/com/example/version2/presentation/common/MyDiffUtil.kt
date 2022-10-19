package com.example.version2.presentation.common

import androidx.recyclerview.widget.DiffUtil
import com.example.version2.presentation.model.NotesRvItem

class MyDiffUtil(
    private val oldList:List<NotesRvItem.UINotes>,
    private val newList:List<NotesRvItem.UINotes>

):DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        return oldList[oldItemPosition].note.id == newList[newItemPosition].note.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}