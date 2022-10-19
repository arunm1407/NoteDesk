package com.example.version2.presentation.homeScreen.listener

import com.example.version2.presentation.homeScreen.enums.SortBy
import com.example.version2.presentation.homeScreen.enums.SortValues


interface SortLisenter {

    fun onSortOptionSelected(sortValues: SortValues, sortBy: SortBy)

}