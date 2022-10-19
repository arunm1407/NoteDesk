package com.example.version2.presentation.previewNote

import com.example.version2.domain.model.Attachment
import com.example.version2.domain.model.Note
import com.example.version2.domain.repository.NoteRepository
import com.example.version2.presentation.common.NotesViewModel

class PreviewViewModel(noteRepository: NoteRepository) : NotesViewModel(noteRepository) {


    lateinit var notes: Note
    lateinit var filenames: List<Attachment>
}