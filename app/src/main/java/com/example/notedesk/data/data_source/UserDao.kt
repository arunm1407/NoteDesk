package com.example.notedesk.data.data_source

import androidx.room.*


@Dao
interface UserDao {



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUser(user:User):Long

    @Update
    suspend fun updateUser(user: User)


    @Query("UPDATE USER SET isOnBoarded = :status where userId = :userId")
    suspend fun setOnBoardedStatus(status: Boolean, userId: Int)



    @Query("SELECT isOnBoarded from USER  where userId = :userId")
    suspend fun getOnBoardedStatus( userId: Int):Boolean


    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE email = :emailId  )")
    suspend fun isExistingEmail(emailId: String): Int


    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE email = :emailId AND password = :password)")
    suspend fun validatePassword(emailId: String, password: String): Int

    @Query("SELECT userId FROM User WHERE email = :emailId")
    suspend fun getUserId(emailId: String): Long

    @Query("SELECT * FROM User WHERE userId = :userId")
    suspend fun getUser(userId: Long): User


}