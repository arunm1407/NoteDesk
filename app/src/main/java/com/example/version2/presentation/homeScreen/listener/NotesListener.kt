package com.example.version2.presentation.homeScreen.listener

import com.example.version2.domain.model.Note


interface NotesListener {
    fun onClick(pos: Int)
    fun onLongClicked(pos: Int)
    fun getSelectedNote() :List<Note>
}