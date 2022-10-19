package com.example.version2.domain.repository

import com.example.version2.domain.model.User


interface UserRepository {

    suspend fun isExistingEmail(name: String): Boolean
    suspend fun createUser(user: User,userId: Int): Long
    suspend fun validatePassword(email: String, password: String): Boolean
    suspend fun getUserId(emailId: String): Long?
    suspend fun getUser(userId: Long): User
    suspend fun updateUser(user: User,userId: Int)
    suspend fun setOnBoardedStatus(status: Boolean, userId: Int)
    suspend fun getOnBoardedStatus(userId: Int): Boolean


}