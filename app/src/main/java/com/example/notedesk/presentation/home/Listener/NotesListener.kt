package com.example.notedesk.presentation.home.Listener

import androidx.recyclerview.widget.RecyclerView
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.presentation.activity.NotesRVViewHolder

interface NotesListener {
    fun onClick(holder: NotesRVViewHolder.NotesViewHolder, data: Notes)
    fun onLongClicked(holder: RecyclerView.ViewHolder, data: Notes)
    fun isAllSelected():Boolean
}