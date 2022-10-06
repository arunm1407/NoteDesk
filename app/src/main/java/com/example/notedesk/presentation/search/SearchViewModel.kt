package com.example.notedesk.presentation.search

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notedesk.data.data_source.History
import com.example.notedesk.domain.model.Note
import com.example.notedesk.presentation.activity.NotesViewModel
import com.example.notedesk.presentation.home.enums.FilterChoiceSelected
import com.example.notedesk.presentation.home.enums.SortBy
import com.example.notedesk.presentation.home.enums.SortValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*


class SearchViewModel(application: Application) : NotesViewModel(application) {


    private val _filterChoiceSelected: MutableLiveData<FilterChoiceSelected> = MutableLiveData()

    val filterChoiceSelected: LiveData<FilterChoiceSelected>
        get() = _filterChoiceSelected


    fun setFilterChoiceSelected(filterChoiceSelected: FilterChoiceSelected) {
        _filterChoiceSelected.value = filterChoiceSelected

    }


    var currentSortOptions: SortValues = SortValues.ALPHABETICALLY_TITLE
    var sortBy: SortBy = SortBy.DESCENDING
    lateinit var filterList: List<Note>
    lateinit var oldMyNotes: List<Note>
    lateinit var displayList: List<Note>
    private var _searchQuery:String=""

    val searchQuery:String
        get() = _searchQuery

    fun setSearchQuery(name: String)
    {
        _searchQuery=name
    }



    fun addSuggestion(name: String,userId:Int) {

        viewModelScope.launch(Dispatchers.IO) {
            repo.insertHistory(History(userId,Calendar.getInstance().timeInMillis, name))
        }

    }

    suspend fun getSuggestion(userId: Int): List<String> {

        val list = viewModelScope.async(Dispatchers.IO)
        {
            return@async repo.getHistory(userId)
        }
        return list.await()

    }


    fun deleteSearchHistory(name: String,userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteHistory(name,userId)
        }

    }


}
