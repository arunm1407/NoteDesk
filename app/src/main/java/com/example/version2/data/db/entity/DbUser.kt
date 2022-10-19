package com.example.version2.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.version2.domain.model.Gender


@Entity(
    indices = [
        Index(value = ["email"], unique = true)
    ]
)


data class DbUser(
    val firstName: String,
    val lastName: String,
    val email: String,
    val bio:String,
    val dob:String,
    val gender: Gender = Gender.NOT_SPECIFIED,
    val mobileNumber: String,
    val image: String?,
    val addressLine1:String,
    val addressLine2: String,
    val city:String,
    val pinCode:Int,
    val password: String,
    val isOnBoarded:Boolean=false,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
