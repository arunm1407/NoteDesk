package com.example.notedesk.data.data_source

import android.app.Application
import androidx.room.*


@Database(entities = [Notes::class, FileName::class,History::class], version = 2, exportSchema = true)
@TypeConverters(Notes.StringConvertor::class )
abstract class NoteDataBase: RoomDatabase() {

    abstract fun getNotesDao(): NoteDao


    companion object
    {
        @Volatile

        private var INSTANCE: NoteDataBase?=null
        fun getDatabase(application: Application): NoteDataBase
        {
            return INSTANCE ?: synchronized(this) {
                val instance= Room.databaseBuilder(
                    application.applicationContext,
                    NoteDataBase::class.java,
                    "note_database"


                ).fallbackToDestructiveMigration().build()
                INSTANCE =instance
                instance
            }
        }
    }

}