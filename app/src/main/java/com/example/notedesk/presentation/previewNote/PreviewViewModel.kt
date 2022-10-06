package com.example.notedesk.presentation.previewNote

import android.app.Application
import com.example.notedesk.domain.model.Note
import com.example.notedesk.presentation.activity.NotesViewModel

class PreviewViewModel(application: Application) : NotesViewModel(application) {

    lateinit var notes: Note
    lateinit var filenames: List<String>



}