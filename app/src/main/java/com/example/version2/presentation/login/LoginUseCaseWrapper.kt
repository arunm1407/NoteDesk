package com.example.version2.presentation.login

import com.example.version2.domain.usecase.CheckUserAuthentication
import com.example.version2.domain.usecase.ValidateNewEmail

data class LoginUseCaseWrapper(
    val validateEmail: ValidateNewEmail,
    val checkAuth: CheckUserAuthentication
)
