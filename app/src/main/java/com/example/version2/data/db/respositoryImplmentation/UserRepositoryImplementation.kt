package com.example.version2.data.db.respositoryImplmentation


import com.example.version2.data.db.dao.UserDao
import com.example.version2.data.db.mapper.UsersEntityMapperImpl
import com.example.version2.domain.model.User
import com.example.version2.domain.repository.UserRepository

class UserRepositoryImplementation(private val userDao: UserDao) : UserRepository {


    override suspend fun isExistingEmail(name: String): Boolean {
        return userDao.isExistingEmail(name) == 1
    }

    override suspend fun createUser(user: User, userId: Int): Long {
        return userDao.createUser(UsersEntityMapperImpl.toEntity(user, userId))
    }

    override suspend fun validatePassword(email: String, password: String): Boolean {
        return userDao.validatePassword(email, password) == 1
    }

    override suspend fun getUserId(emailId: String): Long? {
        return userDao.getUserId(emailId)
    }

    override suspend fun getUser(userId: Long): User {
        return UsersEntityMapperImpl.fromEntity(userDao.getUser(userId)!!)
    }

    override suspend fun updateUser(user: User, userId: Int) {
        userDao.updateUser(UsersEntityMapperImpl.toEntity(user, userId))
    }

    override suspend fun setOnBoardedStatus(status: Boolean, userId: Int) {
        userDao.setOnBoardedStatus(status, userId)
    }

    override suspend fun getOnBoardedStatus(userId: Int): Boolean {
        return userDao.getOnBoardedStatus(userId)
    }


}


