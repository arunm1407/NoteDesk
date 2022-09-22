package com.example.notedesk.domain.usecase

import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.domain.util.keys.Keys
import com.example.notedesk.presentation.home.enums.FilterChoiceSelected

object FilterList {


    fun filterListByChoice(
        oldMyNotes: List<Notes>,
        filterChoiceSelected: FilterChoiceSelected
    ): ArrayList<Notes> {
        if (!(filterChoiceSelected.isFavorite || filterChoiceSelected.isPriority_red || filterChoiceSelected.isPriority_yellow || filterChoiceSelected.isPriority_green))
        {

            if (oldMyNotes.isEmpty())
            {
                return ArrayList()
            }
            return oldMyNotes as ArrayList<Notes>
        }


        val ans = ArrayList<Notes>()
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

        return ans

    }
}
