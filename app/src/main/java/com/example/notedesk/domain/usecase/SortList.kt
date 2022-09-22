package com.example.notedesk.domain.usecase


import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.data.data_source.NotesRvItem
import com.example.notedesk.presentation.home.enums.SortBy
import com.example.notedesk.presentation.home.enums.SortValues
import java.util.ArrayList

object SortList {


    fun sortList(currentSortOptions: SortValues, sortBy: SortBy, arrayList: ArrayList<Notes>):List<Notes> {
        when (currentSortOptions) {
            SortValues.ALPHABETICALLY_TITLE -> {
                if (sortBy == SortBy.DESCENDING) {
                    arrayList.sortByDescending { notes -> notes.title }
                } else {
                    arrayList.sortBy { notes -> notes.title }
                }

            }
            SortValues.ALPHABETICALLY_SUBTITLE -> {
                if (sortBy == SortBy.DESCENDING) {
                    arrayList.sortByDescending { notes -> notes.subtitle }
                } else {
                    arrayList.sortBy { notes -> notes.subtitle }
                }
            }


            SortValues.CREATION_DATE -> {
                if (sortBy == SortBy.DESCENDING) {
                    arrayList.sortByDescending { notes -> notes.createdTime }
                } else {
                    arrayList.sortBy { notes -> notes.createdTime }
                }
            }
            SortValues.MODIFICATION_DATE -> {
                if (sortBy == SortBy.DESCENDING) {
                    arrayList.sortByDescending { notes -> notes.modifiedTime }
                } else {
                    arrayList.sortBy { notes -> notes.modifiedTime }
                }
            }

            SortValues.PRIORITY -> {
                if (sortBy == SortBy.DESCENDING) {
                    arrayList.sortByDescending { notes -> notes.priority }
                } else {
                    arrayList.sortBy { notes -> notes.priority }
                }
            }
        }

        return arrayList
    }
}




fun <T: Any> convert(item: T, block: (T) -> Notes): Notes {
    return block(item)
}

class NotesItem(
    val note: Notes
)



object Something {
    val item1 = Notes()
    val item2 = NotesItem(Notes())

    init {
        val note1: Notes = convert(item1) {
            it
        }
        val note2: Notes = convert(item2) {
            it.note
        }
    }
}