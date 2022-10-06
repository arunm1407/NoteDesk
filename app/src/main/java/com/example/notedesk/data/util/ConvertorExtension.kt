package com.example.notedesk.data.util

import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.domain.model.Note
import com.example.notedesk.presentation.Model.NotesRvItem


fun Notes.toDomain(): Note {
    return Note(
        title,
        subtitle,
        createdTime,
        modifiedTime,
        noteText,
        color,
        weblink,
        priority,
        attachmentCount,
        favorite,
        userID,
        id
    )
}

fun Note.fromDomain(): Notes {
    return Notes(
        title,
        subtitle,
        createdTime,
        modifiedTime,
        noteText,
        color,
        weblink,
        priority,
        attachmentCount,
        favorite,
        userID,
        id
    )
}


fun Note.presenter(): NotesRvItem.UNotes {
    return NotesRvItem.UNotes(
        Note(
            title,
            subtitle,
            createdTime,
            modifiedTime,
            noteText,
            color,
            weblink,
            priority,
            attachmentCount,
            favorite,
            userID,
            id
        )
    )
}




