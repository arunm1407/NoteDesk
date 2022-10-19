package com.example.version2.presentation.model.mapper

import com.example.version2.domain.model.Note
import com.example.version2.presentation.model.NotesRvItem

object UiNotesMapper:UIMapper<Note,NotesRvItem.UINotes> {
    override fun mapToView(input: Note): NotesRvItem.UINotes {
        return NotesRvItem.UINotes(note = input)
    }

    override fun viewToDomain(input: NotesRvItem.UINotes): Note {
        return input.note
    }

}