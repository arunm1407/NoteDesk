package com.example.version2.domain.usecase


data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)