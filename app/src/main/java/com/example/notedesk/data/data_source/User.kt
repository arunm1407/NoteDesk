package com.example.notedesk.data.data_source

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.notedesk.presentation.login.Gender
import java.io.Serializable

@Entity(
    indices = [
        Index(value = ["email"], unique = true)
    ]
)
data class User(

    var firstName: String="",
    var lastName: String="",
    var email: String="",


    var bio:String="",
    var dob:String="",
    var gender:Gender=Gender.NOT_INTERESTED,
    var mobileNumber: String="",
    var image: String? = null,
    var addressLine1:String="",
    var addressLine2: String="",
    var city:String="",
    var pinCode:Int=0,


    var password: String="",

    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
):Serializable
