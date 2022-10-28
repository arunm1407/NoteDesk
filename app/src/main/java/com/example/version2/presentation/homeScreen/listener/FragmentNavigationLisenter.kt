package com.example.version2.presentation.homeScreen.listener

import com.example.version2.domain.model.Note
import com.example.version2.domain.model.User
import com.example.version2.presentation.homeScreen.enums.MenuActions

interface FragmentNavigationLisenter {



    fun navigateToHomeScreen()
    fun navigateToCreateNoteScreen(note: Note?,menu:MenuActions)
    fun navigateToPolicyScreen()
    fun navigateToProfileScreen(userId:Int)
    fun navigateToPreviewNoteScreen(note:Note)
    fun navigateToSearchScreen()
    fun navigateToAttachmentPreviewScreen(name: String)
    fun navigateToSettingsPage()
    fun navigateToLoginScreen()
    fun navigateToEditPage(note: Note)
    fun navigateToEditProfilePage(user: User)
    fun navigateToPreviousScreen()



}