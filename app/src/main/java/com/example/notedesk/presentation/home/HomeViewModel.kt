package com.example.notedesk.presentation.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notedesk.data.data_source.User
import com.example.notedesk.util.keys.Keys
import com.example.notedesk.presentation.home.enums.SortBy
import com.example.notedesk.presentation.home.enums.SortValues
import com.example.notedesk.presentation.activity.NotesViewModel
import com.example.notedesk.domain.model.Note
import com.example.notedesk.presentation.home.enums.FilterChoiceSelected
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : NotesViewModel(application) {

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


    lateinit var oldMyNotes: List<Note>
    lateinit var displayList: List<Note>
    lateinit var filterList: List<Note>


    var currentMode: Int = Keys.LIST_VIEW
    var currentSortOptions: SortValues = SortValues.ALPHABETICALLY_TITLE
    var sortBy: SortBy = SortBy.DESCENDING


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


    fun deleteFile(id: Int, userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFile(id, userId)
        }

    }


    suspend fun getProfileImage(userId: Long): String? {
        Log.i("natz","  $repo")
    return repo.getUser(userId).image





    }

    suspend fun getFullName(userId: Int): String {

        return repo.getUser(userId.toLong()).firstName + "   " + repo.getUser(userId.toLong()).lastName
    }


    suspend fun getUser(userId: Int): User
    {
        return repo.getUser(userId.toLong())
    }


}