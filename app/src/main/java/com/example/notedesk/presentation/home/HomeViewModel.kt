package com.example.notedesk.presentation.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.notedesk.presentation.util.NotesViewModel

class HomeViewModel(application: Application) : NotesViewModel(application) {

    var contextual: MutableLiveData<Boolean> = MutableLiveData()
    var mutableLiveData = MutableLiveData<String>()
        var filterSelectedCount:MutableLiveData<Int> = MutableLiveData()

    fun setText(s: String) {
        mutableLiveData.value = s
    }


    fun getText(): MutableLiveData<String> {
        return mutableLiveData
    }



    fun setTrueContextualActionMode() {
        contextual.value = true
    }


    fun addFilterCount()
    {
        filterSelectedCount.value = filterSelectedCount.value?.plus(1)
    }


    fun resetFilterCount()
    {
        filterSelectedCount.value=0
    }
}