package com.example.version2.presentation.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.version2.domain.repository.NoteRepository
import com.example.version2.domain.repository.SuggestionRepository
import com.example.version2.presentation.homeScreen.HomeUseCaseWrapper
import com.example.version2.presentation.search.SearchViewModel


@Suppress("UNCHECKED_CAST")
class SearchViewModelFactory(private val suggestionRepository: SuggestionRepository,private val noteRepository: NoteRepository,private val homeUseCaseWrapper: HomeUseCaseWrapper) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java))
        {
            return SearchViewModel(suggestionRepository,noteRepository,homeUseCaseWrapper) as T
        }
        throw IllegalAccessException("Cannot able to create SearchViewModel ")
    }
}