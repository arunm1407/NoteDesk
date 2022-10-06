package com.example.notedesk.domain.usecase

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)
