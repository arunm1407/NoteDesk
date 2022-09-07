package com.example.notedesk.domain.usecase

import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.domain.util.keys.IndentKeys
import com.example.notedesk.presentation.home.enums.FilterChoiceSelected

object FilterList {


    fun filterListByChoice(
        oldMyNotes: List<Notes>,
        filterChoiceSelected: FilterChoiceSelected
    ): ArrayList<Notes> {


        val ans = ArrayList<Notes>()
        oldMyNotes.forEach {
            if (it.favorite && filterChoiceSelected.isFavorite)
                ans.add(it)
            if (it.priority == IndentKeys.RED && filterChoiceSelected.isPriority_red)
                ans.add(it)
            else if (it.priority == IndentKeys.YELLOW && filterChoiceSelected.isPriority_yellow)
                ans.add(it)
            else if (it.priority == IndentKeys.GREEN && filterChoiceSelected.isPriority_green)
                ans.add(it)

        }

        return ans

    }
}
