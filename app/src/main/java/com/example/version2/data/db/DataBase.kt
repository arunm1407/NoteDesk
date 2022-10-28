package com.example.version2.data.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.version2.data.db.convertor.AttachmentConvertor
import com.example.version2.data.db.convertor.StringConvertor
import com.example.version2.data.db.dao.NotesDao
import com.example.version2.data.db.dao.SuggestionDao
import com.example.version2.data.db.dao.UserDao
import com.example.version2.data.db.entity.DbNotes
import com.example.version2.data.db.entity.DbSuggestionHistory
import com.example.version2.data.db.entity.DbUser


@Database(entities = [DbNotes::class,DbSuggestionHistory::class,DbUser::class], version = 1, exportSchema = true)
@TypeConverters(StringConvertor::class,AttachmentConvertor::class )
abstract class DataBase : RoomDatabase() {


    abstract fun getNotesDao(): NotesDao
    abstract fun getUserDao(): UserDao
    abstract fun getSuggestionDao(): SuggestionDao


    companion object {
        @Volatile

        private var INSTANCE: DataBase? = null
        fun getDatabase(application: Application): DataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    application.applicationContext,
                    DataBase::class.java,
                    "notedesk_database").fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }


    }


}