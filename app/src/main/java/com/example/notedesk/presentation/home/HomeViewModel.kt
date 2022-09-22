package com.example.notedesk.presentation.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.notedesk.domain.util.keys.Keys
import com.example.notedesk.presentation.home.Listener.FragmentNavigationLisenter
import com.example.notedesk.presentation.home.Listener.SettingsLisenter
import com.example.notedesk.presentation.home.enums.SortBy
import com.example.notedesk.presentation.home.enums.SortValues
import com.example.notedesk.presentation.activity.NotesViewModel
import com.example.notedesk.data.data_source.Notes

class HomeViewModel(application: Application) : NotesViewModel(application) {

    var contextual: MutableLiveData<Boolean> = MutableLiveData()
    var filterSelectedCount: MutableLiveData<Int> = MutableLiveData()
    var fragmentNavigationLisenter: FragmentNavigationLisenter? = null
    var settingsLisenter: SettingsLisenter? = null
    var listEmpty:Boolean=false
    var currentMode: Int = Keys.LIST_VIEW
    var currentSortOptions: SortValues = SortValues.ALPHABETICALLY_TITLE
    var sortBy: SortBy = SortBy.DESCENDING
    var isEnable = false
    var isSelectAll = false
    var selectList: ArrayList<Notes> = ArrayList()
    var text = MutableLiveData<String>()


    suspend fun deleteFile(id: Int) {
        repo.deleteFile(id)
    }


}