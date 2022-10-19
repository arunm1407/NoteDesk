package com.example.version2.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.version2.data.db.entity.DbSuggestionHistory
@Dao
interface SuggestionDao {


    @Query("SELECT suggestion FROM DbSuggestionHistory where  userID=:userId order by timeStamp Desc limit 5")
    suspend fun getHistory(userId: Int): List<String>

    @Query("DELETE FROM DbSuggestionHistory WHERE suggestion=:name and userID=:userId")
    suspend fun deleteHistory(name: String,userId: Int)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: DbSuggestionHistory): Long

}