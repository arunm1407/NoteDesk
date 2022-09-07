package com.example.notedesk.presentation.home.Listener

import com.example.notedesk.presentation.home.enums.SortBy
import com.example.notedesk.presentation.home.enums.SortValues

interface SortLisenter {

    fun onOptionSelected(sortValues: SortValues, sortBy: SortBy)

}