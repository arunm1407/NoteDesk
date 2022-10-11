package com.example.notedesk.domain.repository

import com.example.notedesk.data.data_source.User

interface UserRepository {


    suspend fun isExistingEmail(name: String):Int
    suspend fun createUser(user: User):Long
    suspend fun validatePassword(email: String, password: String):Int
    suspend fun getUserId(emailId: String): Long
    suspend fun getUser(userId: Long): User
    suspend fun updateUser(user: User)
    suspend fun setOnBoardedStatus(status: Boolean, userId: Int)
    suspend fun getOnBoardedStatus(userId: Int): Boolean
}