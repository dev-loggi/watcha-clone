package com.example.csr83.watchaproject.data.local

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "RecentSearch")
data class RecentSearch(
    @PrimaryKey(autoGenerate = true) var id: Int?,
//    @PrimaryKey @ColumnInfo(name = "entryid") var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "search_data") var searchData: String
)