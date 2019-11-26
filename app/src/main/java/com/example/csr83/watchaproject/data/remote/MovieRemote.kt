package com.example.csr83.watchaproject.data.remote

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "MovieRemote")
data class MovieRemote (
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "image_wide") var imageWide: String,
    @ColumnInfo(name = "image_tall") var imageTall: String,
    @ColumnInfo(name = "year") var year: String
)