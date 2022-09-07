package com.example.notedesk.data.data_source

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface NoteDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Notes):Long

    @Update
    suspend fun update(note: Notes)

    @Query("DELETE FROM Notes where id=:id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM notes order by id asc")
    fun getAllNotes(): LiveData<List<Notes>>





    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFileName(fileName: FileName):Long

    @Query("SELECT name FROM  FileName WHERE noteId =:noteId")
     fun getFileName(noteId: Int):MutableList<String>

     @Query("DELETE  FROM FILENAME WHERE name=:fileName")
     suspend fun deleteFileName(fileName: String)

     @Query("DELETE FROM FILENAME WHERE noteId=:id")
     suspend fun deleteFile(id: Int)
}