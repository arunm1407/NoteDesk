package com.example.notedesk.data.data_source

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface NoteDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Notes): Long

    @Update
    suspend fun update(note: Notes)

    @Query("DELETE FROM Notes where id=:id and userID=:userId")
    suspend fun delete(id: Int,userId: Int)

    @Query("SELECT * FROM notes where userID=:userId order by id asc")
    fun getAllNotes(userId: Int): LiveData<List<Notes>>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFileName(fileName: FileName): Long

    @Query("SELECT name FROM  FileName WHERE noteId =:noteId and userID=:userId")
    suspend fun getFileName(noteId: Int,userId: Int): List<String>

    @Query("DELETE  FROM FILENAME WHERE name=:fileName ")
    suspend fun deleteFileName(fileName: String)

    @Query("DELETE FROM FILENAME WHERE noteId=:id and userID=:userId")
    suspend fun deleteFile(id: Int,userId: Int)


    @Query("SELECT suggestion FROM HISTORY where  userID=:userId order by timeStamp Desc limit 5")
   suspend fun getHistory(userId: Int): List<String>

    @Query("DELETE FROM HISTORY WHERE suggestion=:name and userID=:userId")
    suspend fun deleteHistory(name: String,userId: Int)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: History): Long








}