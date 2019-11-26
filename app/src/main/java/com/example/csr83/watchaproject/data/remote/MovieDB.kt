package com.example.csr83.watchaproject.data.remote

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [Movie::class], version = 2, exportSchema = false)
abstract class MovieDB : RoomDatabase() {
    abstract fun movieDAO(): MovieDao

    companion object {
        private var INSTANCE: MovieDB? = null
        private val lock = Any()

        fun getInstance(context: Context): MovieDB {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        MovieDB::class.java, "MovieDB.db")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
                return INSTANCE!!
            }
        }
    }
}