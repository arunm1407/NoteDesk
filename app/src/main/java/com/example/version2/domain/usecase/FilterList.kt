package com.example.version2.domain.usecase

import com.example.version2.domain.model.Note
import com.example.version2.domain.model.Priority


class FilterList {


    operator fun invoke(
        oldMyNotes: List<Note>,
        isFavorite: Boolean,
        isPriority_red: Boolean,
        isPriority_yellow: Boolean,
        isPriority_green: Boolean
    ): List<Note> {

        val ans = mutableListOf<Note>()
        if (!(isFavorite || isPriority_red || isPriority_yellow || isPriority_green)) {
            return oldMyNotes
        }



        oldMyNotes.forEach {
            if (it.favorite && isFavorite)
                ans.add(it)
            else if (it.priority == Priority.IMPORTANT && isPriority_red)
                ans.add(it)
            else if (it.priority == Priority.MEDIUM && isPriority_yellow)
                ans.add(it)
            else if (it.priority == Priority.LOW && isPriority_green)
                ans.add(it)

        }

        return ans.toList()

    }
}
