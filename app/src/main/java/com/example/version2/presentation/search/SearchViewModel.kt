package com.example.version2.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.version2.domain.model.Note
import com.example.version2.domain.model.SuggestionHistory
import com.example.version2.domain.repository.NoteRepository
import com.example.version2.domain.repository.SuggestionRepository
import com.example.version2.domain.usecase.HomeUseCase
import com.example.version2.presentation.common.NotesViewModel
import com.example.version2.presentation.homeScreen.enums.FilterChoiceSelected
import com.example.version2.presentation.homeScreen.enums.SortBy
import com.example.version2.presentation.homeScreen.enums.SortValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class SearchViewModel(
    private val suggestionRepository: SuggestionRepository,
    noteRepository: NoteRepository,
    private val useCase: HomeUseCase
) : NotesViewModel(noteRepository) {


    private val _filterChoiceSelected: MutableLiveData<FilterChoiceSelected> = MutableLiveData()

    val filterChoiceSelected: LiveData<FilterChoiceSelected>
        get() = _filterChoiceSelected


    fun setFilterChoiceSelected(filterChoiceSelected: FilterChoiceSelected) {
        _filterChoiceSelected.value = filterChoiceSelected

    }


    private var _currentSortOptions: SortValues = SortValues.ALPHABETICALLY_TITLE


    val currentSortOptions: SortValues
        get() = _currentSortOptions


    fun setCurrentSortOptions(options: SortValues) {
        _currentSortOptions = options
    }


    private var _sortBy: SortBy = SortBy.DESCENDING


    val sortBy: SortBy
        get() = _sortBy

    fun setSortBy(sort: SortBy) {
        _sortBy = sort
    }

    lateinit var filterList: List<Note>
    lateinit var oldMyNotes: List<Note>
    lateinit var displayList: List<Note>
    private var _searchQuery: String = ""

    val searchQuery: String
        get() = _searchQuery

    fun setSearchQuery(name: String) {
        _searchQuery = name
    }


    fun addSuggestion(name: String, userId: Int) {

        viewModelScope.launch(Dispatchers.IO) {
            suggestionRepository.insertHistory(
                SuggestionHistory(
                    Calendar.getInstance().timeInMillis,
                    name
                ), userId
            )
        }

    }

    suspend fun getSuggestion(userId: Int): List<String> {


        val list = viewModelScope.async(Dispatchers.IO)
        {

            return@async suggestionRepository.getHistory(userId)
        }
        return list.await()

    }


    fun deleteSearchHistory(name: String, userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            suggestionRepository.deleteHistory(name, userId)
        }

    }


    fun sortChoiceByList(list: List<Note>, sortBy: SortBy, sortValues: SortValues): List<Note> {
        return useCase.sortList(sortValues, sortBy, list)
    }

    fun filterListByChoice(
        list: List<Note>,
        filterChoiceSelected: FilterChoiceSelected
    ): List<Note> {
        return useCase.filterList(list, filterChoiceSelected)


    }

}