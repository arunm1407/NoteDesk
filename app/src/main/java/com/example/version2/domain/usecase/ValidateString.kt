package com.example.version2.domain.usecase



class ValidateString {

    operator fun invoke(field: String,max:Int,fieldName:String): ValidationResult {

        if (field.length !in 3..max) {
            return ValidationResult.Error(" $fieldName should have 3 to $max letters ")
        }
        return ValidationResult.Successful
    }



}