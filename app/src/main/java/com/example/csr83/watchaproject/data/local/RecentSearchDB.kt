package com.example.csr83.watchaproject.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [RecentSearch::class], version = 2, exportSchema = false)
abstract class RecentSearchDB : RoomDatabase() {
    abstract fun recentSearchDAO(): RecentSearchDao

    companion object {
        private var INSTANCE: RecentSearchDB? = null
        private val lock = Any()

        fun getInstance(context: Context): RecentSearchDB {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        RecentSearchDB::class.java, "RecentSearch.db")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
                return INSTANCE!!
            }
        }
    }
}