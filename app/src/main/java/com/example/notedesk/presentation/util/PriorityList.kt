package com.example.notedesk.presentation.util

import com.example.notedesk.presentation.model.Priority
import com.example.notedesk.util.keys.Keys.HIGH
import com.example.notedesk.util.keys.Keys.LOW
import com.example.notedesk.util.keys.Keys.MEDIUM
import com.example.notedesk.R

object PriorityList {
    val priorityList= mutableListOf(
        Priority(LOW, R.drawable.priority_green),
        Priority(MEDIUM, R.drawable.priority_yellow),
        Priority(HIGH, R.drawable.priority_red)
    )

}