package com.example.version2.domain.usecase

import com.example.version2.domain.model.Note
import com.example.version2.domain.model.Priority
import com.example.version2.presentation.homeScreen.enums.FilterChoiceSelected


class FilterList {


    operator fun invoke(
        oldMyNotes: List<Note>,
        filterChoiceSelected: FilterChoiceSelected
    ): List<Note> {

        val ans = mutableListOf<Note>()
        if (!(filterChoiceSelected.isFavorite || filterChoiceSelected.isPriority_red || filterChoiceSelected.isPriority_yellow || filterChoiceSelected.isPriority_green)) {
            return oldMyNotes
        }



        oldMyNotes.forEach {
            if (it.favorite && filterChoiceSelected.isFavorite)
                ans.add(it)
            else if (it.priority == Priority.IMPORTANT && filterChoiceSelected.isPriority_red)
                ans.add(it)
            else if (it.priority == Priority.MEDIUM && filterChoiceSelected.isPriority_yellow)
                ans.add(it)
            else if (it.priority == Priority.LOW && filterChoiceSelected.isPriority_green)
                ans.add(it)

        }

        return ans.toList()

    }
}
