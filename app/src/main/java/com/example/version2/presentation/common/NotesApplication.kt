package com.example.version2.presentation.common

import android.app.Application
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
import com.example.version2.presentation.viewModelFactory.*

class NotesApplication : Application() {


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

    private val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(ValidateNewEmail(userRepository), CheckUserAuthentication(userRepository))
    }


    private val signUpUseCase: SignUpUseCase by lazy {

        SignUpUseCase(
            CheckEmailExist(userRepository), ValidateMobileNumber(),
            ValidatePinCode(), ValidatePassword()
        )
    }

    private val formUseCase: FormUseCase by lazy {

        FormUseCase(
            CheckField(),
            ValidateMobileNumber(), ValidatePinCode()
        )
    }

    private val homeUseCase: HomeUseCase by lazy {
        HomeUseCase(
            SortList(),
            FilterList()
        )

    }

    val loginFactory: ViewModelProvider.Factory by lazy {
        LoginViewModelFactory(userRepository, loginUseCase)
    }


    val signUpFactory: ViewModelProvider.Factory by lazy {
        SignUpViewModelFactory(userRepository, signUpUseCase)
    }


    val homeFactory: ViewModelProvider.Factory by lazy {
        HomeViewModelFactory(notesRepository, userRepository, homeUseCase)
    }


    val createNoteFactory: ViewModelProvider.Factory by lazy {
        CreateNoteViewModelFactory(notesRepository)
    }

    val previewNoteFactory: ViewModelProvider.Factory by lazy {
        PreviewNoteViewModelFactory(notesRepository)
    }

    val profilePreview: ViewModelProvider.Factory by lazy {
        ProfilePreviewViewModelFactory(userRepository, loginUseCase)
    }

    val searchFactory: ViewModelProvider.Factory by lazy {

        SearchViewModelFactory(suggestionRepository, notesRepository, homeUseCase)
    }

    val editProfileFactory: ViewModelProvider.Factory by lazy {

        EditProfileViewModelFactory(userRepository, formUseCase)
    }
}