package com.example.version2.domain.usecase

class CheckField {


    operator fun invoke(field: String, fieldName: String): ValidationResult {


        if (field.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "The $fieldName is Empty"
            )
        }
        return ValidationResult(
            successful = true
        )
    }

}