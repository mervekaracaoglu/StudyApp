package com.example.studyapp.reminders

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
/**
 * Room database for storing [ReminderEntity] items.
 *
 * This database provides a singleton instance via [getDatabase] and includes a single DAO: [ReminderDao].
 */
@Database(entities = [ReminderEntity::class], version = 1)
abstract class ReminderDatabase : RoomDatabase() {
    /**
     * Provides access to reminder-related database operations.
     */
    abstract fun reminderDao(): ReminderDao

    companion object {
        //Ensures the database instance is shared and safely accessed across threads.
        @Volatile private var INSTANCE: ReminderDatabase? = null
        /**
         * Retrieves the singleton instance of [ReminderDatabase].
         *
         * @param context The application context used to build the database.
         * @return The singleton instance of [ReminderDatabase].
         */
        fun getDatabase(context: Context): ReminderDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ReminderDatabase::class.java,
                    "reminders_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
