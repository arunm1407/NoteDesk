package com.example.notedesk.presentation.createNote

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notedesk.data.data_source.FileName
import com.example.notedesk.domain.model.Note
import com.example.notedesk.util.keys.Keys
import com.example.notedesk.presentation.home.enums.MenuActions
import com.example.notedesk.presentation.activity.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CreateNoteViewModel(application: Application) : NotesViewModel(application) {

    private val _fileName: MutableLiveData<List<String>> = MutableLiveData(listOf())


    val fileName: LiveData<List<String>>
        get() = _fileName

    fun updateFileNameList(list: List<String>) {
        _fileName.value = list
    }


    fun addFileName(name: String) {
        _fileName.value = _fileName.value!!.toMutableList().also {
            it.add(name)
        }
    }

    fun removeFileName(name: String) {
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


    private var _tempList: List<String> = listOf()

    val tempList:List<String>
        get() = _tempList

    fun setTempList(list: List<String>)
    {
        _tempList=list
    }

    private var _isEdit: Boolean = false

    val isEdit:Boolean
        get() = _isEdit

    fun setIsEdit(value:Boolean)
    {
        _isEdit=value
    }

    private var _priority: Int = Keys.GREEN

    val priority:Int
        get() = _priority

    fun setPriority(int: Int)
    {
        _priority=int
    }




    private var _selectedNoteColor = Keys.SELECTED_NOTED_COLOR


    val selectedNoteColor:String
        get() = _selectedNoteColor


    fun setSelectedNoteColor(string: String)
    {
        _selectedNoteColor=string
    }



    private var _action: MenuActions? = null

    val action:MenuActions?
        get() = _action


    fun setMenuAction(menuActions: MenuActions)
    {
        _action=menuActions
    }




    lateinit var originalList: List<String>
    lateinit var notes: Note






    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.update(note)
        }
    }

    suspend fun addListFileName(fileName: FileName): Long {
        val list = viewModelScope.async(Dispatchers.IO)
        {
            repo.insertFileName(fileName)
        }
        return list.await()

    }


    fun deleteFileName(fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFileName(fileName)
        }

    }


}