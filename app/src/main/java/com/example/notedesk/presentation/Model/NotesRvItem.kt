package com.example.notedesk.presentation.Model

import com.example.notedesk.domain.model.Note


sealed class NotesRvItem {
    data class Title(val id: Int, val title: String) : NotesRvItem()
    data class Suggestion(var suggestion: String): NotesRvItem()
    data class UNotes(val note: Note): NotesRvItem()
}