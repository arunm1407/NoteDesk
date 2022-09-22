package com.example.notedesk.presentation.createNote

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notedesk.data.data_source.FileName
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.domain.util.keys.Keys
import com.example.notedesk.presentation.home.enums.MenuActions
import com.example.notedesk.presentation.activity.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateNoteViewModel(application: Application) : NotesViewModel(application) {

    val fileName: MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf())
    val webUrl: MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf())
    lateinit var notes: Notes
    var isEdit: Boolean = false
    var priority: Int = Keys.GREEN
    lateinit var originalList: MutableList<String>
    var selectedNoteColor = Keys.SELECTED_NOTED_COLOR
    var tempList: MutableList<String> = mutableListOf()
    var action: MenuActions? = null


    fun updateNote(note: Notes) = viewModelScope.launch(Dispatchers.IO) {
        repo.update(note)
    }

    suspend fun addListFileName(fileName: FileName): Long {
        return repo.insertFileName(fileName)
    }


    suspend fun deleteFileName(fileName: String) {
        repo.deleteFileName(fileName)
    }

    fun addUrl(url: String) {
        webUrl.value?.add(url)
        webUrl.value = webUrl.value
    }

    fun removeUrl(url: String) {
        webUrl.value?.remove(url)
        webUrl.value = webUrl.value
    }

    fun addFileName(name: String) {

        fileName.value?.add(name)
        fileName.value = fileName.value

    }

    fun removeFileName(name: String) {
        fileName.value?.remove(name)
        fileName.value = fileName.value

    }


}