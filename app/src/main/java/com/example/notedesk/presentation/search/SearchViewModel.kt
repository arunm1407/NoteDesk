package com.example.notedesk.presentation.search

import android.app.Application
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notedesk.data.data_source.History
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.presentation.home.Listener.FragmentNavigationLisenter
import com.example.notedesk.presentation.home.enums.SortBy
import com.example.notedesk.presentation.home.enums.SortValues
import com.example.notedesk.presentation.activity.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.util.*


class SearchViewModel(application: Application) : NotesViewModel(application) {

    var fragmentNavigationLisenter: FragmentNavigationLisenter? = null
    var filterSelectedCount: MutableLiveData<Int> = MutableLiveData()
    var currentSortOptions: SortValues = SortValues.ALPHABETICALLY_TITLE
    var sortBy: SortBy = SortBy.DESCENDING
    lateinit var dialog: DialogFragment
    fun getSearchNotes(): List<Notes> {
        return repo.getSearchNotes()
    }

    init {
        filterSelectedCount.value = 0
    }

    fun addSuggestion(name: String) {

        viewModelScope.launch(Dispatchers.IO) {
            repo.insertHistory(History(Calendar.getInstance().timeInMillis, name))
        }

    }

    fun getSuggestion(): List<String> {
        return repo.getHistory()
    }


    fun addFilterCount() {
        filterSelectedCount.value = filterSelectedCount.value?.plus(1)
    }


    fun resetFilterCount() {
        filterSelectedCount.value = 0
    }


    fun deleteSearchHistory(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteHistory(name)
        }

    }


}
