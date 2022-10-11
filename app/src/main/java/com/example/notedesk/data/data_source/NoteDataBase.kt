package com.example.notedesk.data.data_source

import android.app.Application
import androidx.room.*
import com.example.notedesk.data.convertor.StringConvertor


@Database(entities = [Notes::class, FileName::class,History::class,User::class], version = 1, exportSchema = true)
@TypeConverters(StringConvertor::class )
abstract class NoteDataBase: RoomDatabase() {

    abstract fun getNotesDao(): NoteDao
    abstract fun getUserDao():UserDao



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
