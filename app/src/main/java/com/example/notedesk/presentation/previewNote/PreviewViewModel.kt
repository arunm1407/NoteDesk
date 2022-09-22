package com.example.notedesk.presentation.previewNote

import android.app.Application
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.presentation.activity.NotesViewModel

class PreviewViewModel(application: Application) : NotesViewModel(application) {

     lateinit var notes: Notes
    lateinit var filenames: List<String>



}