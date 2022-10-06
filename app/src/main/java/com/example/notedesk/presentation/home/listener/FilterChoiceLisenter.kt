package com.example.notedesk.presentation.home.listener

import com.example.notedesk.presentation.home.enums.FilterChoiceSelected

interface FilterChoiceLisenter {
    fun onFilterClickDone(choice: FilterChoiceSelected)
    fun onFilterClear()
}