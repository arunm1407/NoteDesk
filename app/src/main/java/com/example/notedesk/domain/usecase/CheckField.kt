package com.example.notedesk.domain.usecase

object CheckField {



    fun execute(field: String,fieldName:String): ValidationResult {


        if(field.isBlank()) {
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