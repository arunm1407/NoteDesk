package com.example.notedesk.data.repository

import com.example.notedesk.data.data_source.User
import com.example.notedesk.data.data_source.UserDao
import com.example.notedesk.domain.repository.UserRepository

class UserRepositoryImplementation(private val userDao: UserDao):UserRepository {


    override suspend fun isExistingEmail(name: String):Int {
        return userDao.isExistingEmail(name)
    }

    override suspend fun createUser(user: User):Long {
        return userDao.createUser(user)
    }

    override suspend fun validatePassword(email: String, password: String):Int {
        return userDao.validatePassword(email,password)
    }

    override suspend fun getUserId(emailId: String): Long {
        return userDao.getUserId(emailId)
    }

    override suspend fun getUser(userId: Long): User {
        return userDao.getUser(userId)
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    override suspend fun setOnBoardedStatus(status: Boolean, userId: Int) {
        userDao.setOnBoardedStatus(status,userId)
    }

    override suspend fun getOnBoardedStatus(userId: Int): Boolean {
        return userDao.getOnBoardedStatus(userId)
    }
}