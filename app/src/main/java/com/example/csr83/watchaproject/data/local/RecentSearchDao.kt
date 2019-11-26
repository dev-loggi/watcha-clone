package com.example.csr83.watchaproject.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao interface RecentSearchDao {

    @Query("SELECT * FROM RecentSearch")
    fun getAllSearchData(): List<RecentSearch>

    @Query("SELECT * FROM RecentSearch LIMIT :count")
    fun getSearchDatas(count: Int = 20): List<RecentSearch>

    @Query("SELECT * FROM RecentSearch WHERE search_data = :text")
    fun getSearchData(text: String): RecentSearch?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: RecentSearch)

    @Query("DELETE FROM RecentSearch")
    fun deleteAllSearchData()

    @Query("DELETE FROM RecentSearch WHERE search_data = :text")
    fun deleteSearchData(text: String)

//    @Query("DELETE FROM RecentSearch WHERE id = :id")
//    fun deleteSearchData(id: Int)

}