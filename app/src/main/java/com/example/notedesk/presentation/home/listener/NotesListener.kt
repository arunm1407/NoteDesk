package com.example.notedesk.presentation.home.listener

import com.example.notedesk.domain.model.Note


interface NotesListener {
    fun onClick(pos: Int)
    fun onLongClicked(pos: Int)
    fun getSelectedNote() :List<Note>
}