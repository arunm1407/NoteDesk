package com.example.version2.presentation.homeScreen.listener

import com.example.version2.domain.model.SortBy
import com.example.version2.domain.model.SortValues


interface SortLisenter {

    fun onSortOptionSelected(sortValues: SortValues, sortBy: SortBy)

}