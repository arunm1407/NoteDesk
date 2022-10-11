package com.example.notedesk.presentation.home.listener

import androidx.fragment.app.Fragment

interface FragmentNavigationLisenter {

    fun navigate(fragment: Fragment,name:String)
    fun<T> navigateActivity(it: Class<T>)


}