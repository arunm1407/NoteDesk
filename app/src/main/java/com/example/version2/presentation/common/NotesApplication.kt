package com.example.version2.presentation.common

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.version2.data.db.DataBase
import com.example.version2.data.db.dao.NotesDao
import com.example.version2.data.db.dao.SuggestionDao
import com.example.version2.data.db.dao.UserDao
import com.example.version2.data.db.respositoryImplmentation.NotesRepositoryImplementation
import com.example.version2.data.db.respositoryImplmentation.SuggestionRepositoryImplmentation
import com.example.version2.data.db.respositoryImplmentation.UserRepositoryImplementation
import com.example.version2.domain.repository.NoteRepository
import com.example.version2.domain.repository.SuggestionRepository
import com.example.version2.domain.repository.UserRepository
import com.example.version2.domain.usecase.*
import com.example.version2.presentation.homeScreen.HomeUseCaseWrapper
import com.example.version2.presentation.login.LoginUseCaseWrapper
import com.example.version2.presentation.profile.FormUseCaseWrapper
import com.example.version2.presentation.signUp.SignUpUseCaseWrapper
import com.example.version2.presentation.viewModelFactory.*

class NotesApplication : Application() {




    companion object{
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
    }


    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
    private val userDao: UserDao by lazy {
        DataBase.getDatabase(this).getUserDao()
    }
    private val notesDao: NotesDao by lazy {
        DataBase.getDatabase(this).getNotesDao()

    }
    private val suggestionDao: SuggestionDao by lazy {
        DataBase.getDatabase(this).getSuggestionDao()

    }


    private val userRepository: UserRepository by lazy {
        UserRepositoryImplementation(userDao)
    }

    private val notesRepository: NoteRepository by lazy {
        NotesRepositoryImplementation(notesDao)
    }

    private val suggestionRepository: SuggestionRepository by lazy {
        SuggestionRepositoryImplmentation(suggestionDao)
    }

    private val loginUseCaseWrapper: LoginUseCaseWrapper by lazy {
        LoginUseCaseWrapper(ValidateNewEmail(userRepository), CheckUserAuthentication(userRepository))
    }


    private val signUpUseCaseWrapper: SignUpUseCaseWrapper by lazy {

        SignUpUseCaseWrapper(
            CheckEmailExist(userRepository), ValidateMobileNumber(),
            ValidatePinCode(), ValidatePassword(),ValidateString()
        )
    }

    private val formUseCaseWrapper: FormUseCaseWrapper by lazy {

        FormUseCaseWrapper(
            CheckField(),
            ValidateMobileNumber(), ValidatePinCode(),ValidateString()
        )
    }

    private val homeUseCaseWrapper: HomeUseCaseWrapper by lazy {
        HomeUseCaseWrapper(
            SortList(),
            FilterList()
        )

    }

    val loginFactory: ViewModelProvider.Factory by lazy {
        LoginViewModelFactory(userRepository, loginUseCaseWrapper)
    }


    val signUpFactory: ViewModelProvider.Factory by lazy {
        SignUpViewModelFactory(userRepository, signUpUseCaseWrapper)
    }


    val homeFactory: ViewModelProvider.Factory by lazy {
        HomeViewModelFactory(notesRepository, userRepository, homeUseCaseWrapper)
    }


    val createNoteFactory: ViewModelProvider.Factory by lazy {
        CreateNoteViewModelFactory(notesRepository)
    }

    val previewNoteFactory: ViewModelProvider.Factory by lazy {
        PreviewNoteViewModelFactory(notesRepository)
    }

    val profilePreview: ViewModelProvider.Factory by lazy {
        ProfilePreviewViewModelFactory(userRepository, loginUseCaseWrapper)
    }

    val searchFactory: ViewModelProvider.Factory by lazy {

        SearchViewModelFactory(suggestionRepository, notesRepository, homeUseCaseWrapper)
    }

    val editProfileFactory: ViewModelProvider.Factory by lazy {

        EditProfileViewModelFactory(userRepository, formUseCaseWrapper)
    }
}