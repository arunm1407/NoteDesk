package com.example.version2.domain.model

import java.io.Serializable


data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val bio: String,
    val dob: String,
    val gender: Gender = Gender.NOT_SPECIFIED,
    val mobileNumber: String,
    val image: String?,
    val addressLine1: String,
    val addressLine2: String,
    val city: String,
    val pinCode: Int,
    val password: String,
    val isOnBoarded: Boolean
):Serializable


