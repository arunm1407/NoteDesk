package com.example.notedesk.presentation.createNote

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.notedesk.data.data_source.Notes
import com.example.notedesk.presentation.util.NotesViewModel

class CreateNoteViewModel(application: Application) : NotesViewModel(application) {


    val fileName: MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf())
    val webUrl:MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf())


    fun addUrl(url:String)
    {
        webUrl.value?.add(url)
        webUrl.value=webUrl.value
    }

    fun removeUrl(url: String)
    {
        webUrl.value?.remove(url)
        webUrl.value=webUrl.value
    }



    var tempNotes: Notes = Notes()


    fun addFileName(name: String) {

        fileName.value?.add(name)
        fileName.value= fileName.value

    }

     fun removeFileName(name: String)
    {
        fileName.value?.remove(name)
        fileName.value=fileName.value

    }


}