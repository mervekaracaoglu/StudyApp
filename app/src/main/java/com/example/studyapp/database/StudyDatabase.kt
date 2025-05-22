package com.example.studyapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StudySession::class], version = 1)
abstract class StudyDatabase : RoomDatabase(){
    abstract fun sessionDao(): SessionDao
    companion object{
        @Volatile
        private var INSTANCE : StudyDatabase ? = null
        fun getDatabase(context: Context): StudyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudyDatabase::class.java,
                    "study_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}