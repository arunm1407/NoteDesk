package com.example.notedesk.presentation.home.Listener

import com.example.notedesk.presentation.home.enums.FilterChoiceSelected

interface FilterChoiceLisenter {
    fun onClickDone(choice: FilterChoiceSelected)
    fun onClear()
}