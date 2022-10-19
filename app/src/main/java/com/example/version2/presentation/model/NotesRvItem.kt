package com.example.version2.presentation.model

import com.example.version2.domain.model.Note


sealed class NotesRvItem {
    data class UITitle(val id: Int, val title: String) : NotesRvItem()
    data class UISuggestion(var suggestion: String): NotesRvItem()
    data class UINotes(val note: Note): NotesRvItem()
}