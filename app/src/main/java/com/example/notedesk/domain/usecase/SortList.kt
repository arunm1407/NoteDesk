package com.example.notedesk.domain.usecase


import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.presentation.home.enums.SortBy
import com.example.notedesk.presentation.home.enums.SortValues
import java.util.ArrayList

object SortList {


    fun sortList(currentSortOptions: SortValues, sortBy: SortBy, arrayList: ArrayList<Notes>):List<Notes> {
        when (currentSortOptions) {
            SortValues.ALPHABETICALLY_TITLE -> {
                if (sortBy == SortBy.DESENDING) {
                    arrayList.sortByDescending { notes -> notes.title }
                } else {
                    arrayList.sortBy { notes -> notes.title }
                }

            }
            SortValues.ALPHABETICALLY_SUBTITLE -> {
                if (sortBy == SortBy.DESENDING) {
                    arrayList.sortByDescending { notes -> notes.subtitle }
                } else {
                    arrayList.sortBy { notes -> notes.subtitle }
                }
            }


            SortValues.CREATION_DATE -> {
                if (sortBy == SortBy.DESENDING) {
                    arrayList.sortByDescending { notes -> notes.createdTime }
                } else {
                    arrayList.sortBy { notes -> notes.createdTime }
                }
            }
            SortValues.MODIFICATION_DATE -> {
                if (sortBy == SortBy.DESENDING) {
                    arrayList.sortByDescending { notes -> notes.modifiedTime }
                } else {
                    arrayList.sortBy { notes -> notes.modifiedTime }
                }
            }

            SortValues.PRIORITY -> {
                if (sortBy == SortBy.DESENDING) {
                    arrayList.sortByDescending { notes -> notes.priority }
                } else {
                    arrayList.sortBy { notes -> notes.priority }
                }
            }
        }

        return arrayList
    }
}