package com.example.version2.presentation.createNote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.version2.presentation.util.keys.Keys
import com.example.version2.domain.model.Attachment
import com.example.version2.domain.model.Note
import com.example.version2.domain.model.Priority
import com.example.version2.domain.repository.NoteRepository
import com.example.version2.presentation.common.NotesViewModel
import com.example.version2.presentation.homeScreen.enums.MenuActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateNoteViewModel(noteRepository: NoteRepository) : NotesViewModel(noteRepository) {


    private var _userId: Int=0


    val userId: Int
        get() = _userId


    fun setUserId(id: Int) {
        _userId = id
    }


    private val _fileName: MutableLiveData<List<Attachment>> = MutableLiveData(listOf())


    val fileName: LiveData<List<Attachment>>
        get() = _fileName

    fun updateFileNameList(list: List<Attachment>) {
        _fileName.value = list
    }


    fun addFileName(name: Attachment) {

        _fileName.value = _fileName.value!!.toMutableList().also {
            it.add(name)
        }
    }

    fun removeFileName(name: Attachment) {
        _fileName.value = _fileName.value!!.toMutableList().also {
            it.remove(name)
        }

    }


    private val _webUrl: MutableLiveData<List<String>> = MutableLiveData(listOf())


    val webUrl: LiveData<List<String>>
        get() = _webUrl


    fun addUrl(url: String) {
        _webUrl.value = webUrl.value!!.toMutableList().also {
            it.add(url)
        }
    }

    fun removeUrl(url: String) {
        _webUrl.value = webUrl.value!!.toMutableList().also {
            it.remove(url)
        }
    }


    fun updateUrlList(list: List<String>) {
        _webUrl.value = list
    }


    private var _isEdit: Boolean = false

    val isEdit: Boolean
        get() = _isEdit

    fun setIsEdit(value: Boolean) {
        _isEdit = value
    }

    private var _priority: Priority = Priority.LOW

    val priority: Priority
        get() = _priority

    fun setPriority(priority: Priority) {
        _priority = priority
    }


    private var _selectedNoteColor = Keys.SELECTED_NOTED_COLOR


    val selectedNoteColor: String
        get() = _selectedNoteColor


    fun setSelectedNoteColor(string: String) {
        _selectedNoteColor = string
    }


    private var _action: MenuActions? = null

    val action: MenuActions?
        get() = _action


    fun setMenuAction(menuActions: MenuActions) {
        _action = menuActions
    }


    lateinit var notes: Note


    fun updateNote(note: Note, userInt: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.update(note, userInt)
        }
    }


}