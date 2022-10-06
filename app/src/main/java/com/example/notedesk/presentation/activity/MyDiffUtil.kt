package com.example.notedesk.presentation.activity

import androidx.recyclerview.widget.DiffUtil
import com.example.notedesk.domain.model.Note

class MyDiffUtil(
    private val oldList:List<Note>,
    private val newList:List<Note>

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