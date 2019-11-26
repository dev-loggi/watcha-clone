package com.example.csr83.watchaproject.data.remote

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface MovieDao {

    @Query("SELECT * FROM Movie")
    fun getAllMovieData(): List<Movie>

    @Query("SELECT * FROM Movie WHERE title LIKE :title")
    fun getMovieData(title: String): List<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movies: ArrayList<Movie>)

    @Query("DELETE FROM Movie")
    fun deleteAll()

}