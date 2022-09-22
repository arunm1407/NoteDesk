package com.example.notedesk.data.data_source



sealed class NotesRvItem {
    data class Title(val id: Int, val title: String) : NotesRvItem()
    data class Suggestion(var suggestion: String): NotesRvItem()

}