package com.example.version2.presentation.homeScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.version2.presentation.util.keys.Keys
import com.example.version2.domain.model.Note
import com.example.version2.domain.repository.NoteRepository
import com.example.version2.domain.repository.UserRepository
import com.example.version2.presentation.common.NotesViewModel
import com.example.version2.presentation.homeScreen.enums.FilterChoiceSelected
import com.example.version2.domain.model.SortBy
import com.example.version2.domain.model.SortValues


class HomeViewModel(
    noteRepository: NoteRepository,
    private val userRepository: UserRepository,
    private val homeUseCaseWrapper: HomeUseCaseWrapper
) :
    NotesViewModel(noteRepository) {


    private var _userId: Int = 0


    val userId: Int
        get() = _userId

    fun setUserId(id: Int) {
        _userId = id
    }


    private val _contextual: MutableLiveData<Boolean> = MutableLiveData()

    val contextual: LiveData<Boolean>
        get() = _contextual

    fun setContextual(value: Boolean) {
        _contextual.value = value
    }

    private var _filterChoiceSelected: MutableLiveData<FilterChoiceSelected> = MutableLiveData()

    val filterChoiceSelected: LiveData<FilterChoiceSelected>
        get() = _filterChoiceSelected

    fun setFilterChoiceSelected(filterChoiceSelected: FilterChoiceSelected) {
        _filterChoiceSelected.value = filterChoiceSelected
    }


    private val _text = MutableLiveData<String>()

    val text: LiveData<String>
        get() = _text


    fun setText(string: String) {
        _text.value = string
    }


    private var _isEnable = false

    val isEnable: Boolean
        get() = _isEnable

    fun setIsEnable(value: Boolean) {
        _isEnable = value
    }


    private var _isContextualActive: Boolean = false


    val isContextualActive: Boolean
        get() = _isContextualActive

    fun setContextualActive(value: Boolean) {
        _isContextualActive = value
    }


    var oldMyNotes: List<Note> = listOf()
    var displayList: List<Note> = listOf()
    var filterList: List<Note> = listOf()


    private var _currentMode: Int = Keys.LIST_VIEW

    val currentMode: Int
        get() = _currentMode


    fun setCurrentMode(mode: Int) {
        _currentMode = mode
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


    private var _selectList: MutableList<Note> = mutableListOf()

    val selectList: List<Note>
        get() = _selectList


    fun addSelectList(notes: Note) {
        _selectList.add(notes)
    }

    fun removeFromSelectedList(notes: Note) {
        _selectList.remove(notes)
    }


    fun clearSelectedList() {
        _selectList.clear()
    }


    fun addListToSelectedList(list: List<Note>) {
        _selectList.addAll(list)
    }


    suspend fun getProfileImage(userId: Long): String? {
        return userRepository.getUser(userId).image


    }

    suspend fun getFullName(userId: Long): String {

        return userRepository.getUser(userId).firstName + " " + userRepository.getUser(userId).lastName


    }


    fun sortChoiceByList(list: List<Note>, sortBy: SortBy, sortValues: SortValues): List<Note> {
        return homeUseCaseWrapper.sortList(sortValues, sortBy, list)
    }

    fun filterListByChoice(
        list: List<Note>,
        filterChoiceSelected: FilterChoiceSelected
    ): List<Note> {
        return homeUseCaseWrapper.filterList(
            list,
            filterChoiceSelected.isFavorite,
            filterChoiceSelected.isPriority_red,
            filterChoiceSelected.isPriority_yellow,
            filterChoiceSelected.isPriority_green
        )


    }


}