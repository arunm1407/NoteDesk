package com.example.version2.presentation.search.listener

import com.example.version2.presentation.model.NotesRvItem


interface SuggestionLisenter {
    fun addSuggestion(name: String)
    fun onSuggestionClicked(name: String)
    fun deleteSearchHistory(name: String, position: Int)
    fun onClickedNote(notes: NotesRvItem.UINotes)
}