package com.example.notedesk.domain.usecase


import com.example.notedesk.domain.model.Note
import com.example.notedesk.presentation.home.enums.SortBy
import com.example.notedesk.presentation.home.enums.SortValues

object SortList {


    fun sortList(
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
                SortValues.PRIORITY -> return@sort it.priority.toString()
                SortValues.CREATION_DATE -> return@sort it.createdTime.toString()
                else -> return@sort it.modifiedTime.toString()
            }
        }

        return list

    }


}


fun <T, R : Comparable<R>> MutableList<T>.sort(
    sortBy: SortBy,
    selector: (T) -> R?
) {


    when (sortBy) {
        SortBy.ASCENDING -> sortBy(selector)
        SortBy.DESCENDING -> sortByDescending(selector)
    }


}








