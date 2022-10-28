package com.example.version2.domain.usecase


sealed class ValidationResult {

    object Successful : ValidationResult()
    class Error(val message: String) : ValidationResult()

}