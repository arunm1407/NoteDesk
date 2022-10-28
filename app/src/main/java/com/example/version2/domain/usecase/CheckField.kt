package com.example.version2.domain.usecase

class CheckField {


    operator fun invoke(field: String, fieldName: String): ValidationResult {


        if (field.isBlank()) {
            return ValidationResult.Error("The $fieldName is Empty")
        }
        return ValidationResult.Successful
    }

}