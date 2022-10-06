package com.example.notedesk.domain.usecase

import com.example.notedesk.domain.model.Note
import com.example.notedesk.util.keys.Keys
import com.example.notedesk.presentation.home.enums.FilterChoiceSelected

object FilterList {


    fun filterListByChoice(
        oldMyNotes: List<Note>,
        filterChoiceSelected: FilterChoiceSelected
    ): List<Note> {

        val ans = mutableListOf<Note>()
        if (!(filterChoiceSelected.isFavorite || filterChoiceSelected.isPriority_red || filterChoiceSelected.isPriority_yellow || filterChoiceSelected.isPriority_green))
        {
            return oldMyNotes
        }



        oldMyNotes.forEach {
            if (it.favorite && filterChoiceSelected.isFavorite)
                ans.add(it)
            else if (it.priority == Keys.RED && filterChoiceSelected.isPriority_red)
                ans.add(it)
            else if (it.priority == Keys.YELLOW && filterChoiceSelected.isPriority_yellow)
                ans.add(it)
            else if (it.priority == Keys.GREEN && filterChoiceSelected.isPriority_green)
                ans.add(it)

        }

        return ans.toList()

    }
}
