package com.example.version2.domain.usecase


import com.example.version2.domain.model.Note
import com.example.version2.presentation.homeScreen.enums.SortBy
import com.example.version2.presentation.homeScreen.enums.SortValues

class SortList {


    operator fun invoke(
        currentSortOptions: SortValues,
        sortBy: SortBy,
        List: List<Note>
    ): List<Note> {

        val list = List.toMutableList()

        list.sort(sortBy)
        {
            when (currentSortOptions) {
                SortValues.ALPHABETICALLY_TITLE -> return@sort it.title
                SortValues.ALPHABETICALLY_SUBTITLE -> return@sort it.subtitle
                SortValues.PRIORITY -> return@sort it.priority.ordinal.toString()
                SortValues.CREATION_DATE -> return@sort it.createdTime.toString()
                else -> return@sort it.modifiedTime.toString()
            }
        }

        return list

    }

    private fun <T, R : Comparable<R>> MutableList<T>.sort(
        sortBy: SortBy,
        selector: (T) -> R?
    ) {


        when (sortBy) {
            SortBy.ASCENDING -> sortBy(selector)
            SortBy.DESCENDING -> sortByDescending(selector)
        }


    }




}











