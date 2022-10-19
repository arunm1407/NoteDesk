package com.example.version2.domain.usecase

data class LoginUseCase(
    val validateEmail: ValidateNewEmail,
    val checkAuth: CheckUserAuthentication
)
