package com.example.version2.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.version2.data.db.entity.DbNotes

@Dao
interface NotesDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: DbNotes): Long

    @Update
    suspend fun update(note: DbNotes)

    @Query("DELETE FROM DbNotes where id=:id and userID=:userId")
    suspend fun delete(id: Int, userId: Int)

    @Query("SELECT * FROM DbNotes where userID=:userId order by id asc")
     fun getAllNotes(userId: Int): LiveData<List<DbNotes>>


}