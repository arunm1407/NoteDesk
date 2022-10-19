package com.example.version2.presentation.homeScreen.listener

import com.example.version2.presentation.homeScreen.enums.FilterChoiceSelected


interface FilterChoiceLisenter {
    fun onFilterClickDone(choice: FilterChoiceSelected)
    fun onFilterClear()
}