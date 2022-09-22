package com.example.notedesk.presentation.activity

import androidx.recyclerview.widget.DiffUtil
import com.example.notedesk.data.data_source.Notes

class MyDiffUtil(
    private val oldList:List<Notes>,
    private val newList:List<Notes>

):DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}