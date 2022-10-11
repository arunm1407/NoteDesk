package com.example.notedesk.presentation.search.listner

import com.example.notedesk.presentation.model.NotesRvItem


interface SuggestionLisenter {
    fun addSuggestion(name:String)
    fun onSuggestionClicked(name:String)
    fun deleteSearchHistory(name: String, position: Int)
    fun onClickedNote(notes: NotesRvItem.UNotes)
}