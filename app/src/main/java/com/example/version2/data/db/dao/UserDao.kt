package com.example.version2.data.db.dao

import androidx.room.*
import com.example.version2.data.db.entity.DbUser


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUser(user: DbUser):Long

    @Update
    suspend fun updateUser(user: DbUser)


    @Query("UPDATE DbUser SET isOnBoarded = :status where id = :userId")
    suspend fun setOnBoardedStatus(status: Boolean, userId: Int)



    @Query("SELECT isOnBoarded from DbUser  where id = :userId")
    suspend fun getOnBoardedStatus( userId: Int):Boolean


    @Query("SELECT EXISTS(SELECT 1 FROM DbUser WHERE email = :emailId  )")
    suspend fun isExistingEmail(emailId: String): Int


    @Query("SELECT EXISTS(SELECT 1 FROM DbUser WHERE email = :emailId AND password = :password)")
    suspend fun validatePassword(emailId: String, password: String): Int

    @Query("SELECT id FROM DbUser WHERE email = :emailId")
    suspend fun getUserId(emailId: String): Long?

    @Query("SELECT * FROM DbUser WHERE id = :userId")
    suspend fun getUser(userId: Long): DbUser?
    
    
    
}