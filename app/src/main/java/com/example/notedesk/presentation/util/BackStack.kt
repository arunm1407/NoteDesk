package com.example.notedesk.presentation.util

import com.example.notedesk.presentation.createNote.Priority
import com.example.notesappfragment.R

object BackStack {

    const val HOME="Home"
    const val EDIT="edit"
    const val PREVIEW="preview"
    const val ATTACHMENT_PREVIEW="attachment"
    const val POLICY="Policy"
    const val CREATE="create"


     val priortyList= mutableListOf(Priority("Low",R.drawable.priority_green),Priority("Medium",R.drawable.priority_yellow),Priority("High",R.drawable.priority_red))

}