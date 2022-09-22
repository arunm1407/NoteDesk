package com.example.notedesk.presentation.search.listner

import com.example.notedesk.data.data_source.Notes

interface SuggestionLisenter {
    fun addSuggestion(name:String)
    fun onSuggestionClicked(name:String)
    fun deleteSearchHistory(name:String)
    fun onClickedNote(notes: Notes)
}