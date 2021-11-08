package com.example.javBridge.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Movie::class,Url::class], version = 1, exportSchema = false)
@TypeConverters(MovieTypeConverts::class)
abstract class BridgeRoom : RoomDatabase() {
    abstract fun bridgeDao(): BridgeDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: BridgeRoom? = null

        fun getDatabase(context: Context): BridgeRoom {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BridgeRoom::class.java,
                    "bridge_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
