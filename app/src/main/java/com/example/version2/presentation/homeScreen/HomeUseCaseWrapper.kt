package com.example.version2.presentation.homeScreen

import com.example.version2.domain.usecase.FilterList
import com.example.version2.domain.usecase.SortList

data class HomeUseCaseWrapper(
    val sortList: SortList,
    val filterList: FilterList
)
