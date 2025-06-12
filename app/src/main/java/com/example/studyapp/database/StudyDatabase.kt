package com.example.studyapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
/**
 * The Room database for storing study sessions.
 *
 * It provides access to the [SessionDao] interface and manages the underlying SQLite database.
 */
@Database(entities = [StudySession::class], version = 1)
abstract class StudyDatabase : RoomDatabase(){
    /**
     * Returns the DAO used to access study session data.
     */
    abstract fun sessionDao(): SessionDao
    companion object{
        @Volatile
        private var INSTANCE : StudyDatabase ? = null
        /**
         * Returns a singleton instance of [StudyDatabase].
         *
         * This ensures that the database is created only once during the app's lifecycle.
         *
         * @param context The application context.
         * @return The singleton [StudyDatabase] instance.
         */
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