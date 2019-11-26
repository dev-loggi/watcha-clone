//package com.example.csr83.watchaproject.data.remote
//
//import java.io.Serializable
//
//data class Movie (val title: String, val image_wide: String, val image_tall: String, val year: String): Serializable

package com.example.csr83.watchaproject.data.remote

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Movie")
data class Movie (
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "image_wide") var image_wide: String,
    @ColumnInfo(name = "image_tall") var image_tall: String,
    @ColumnInfo(name = "year") var year: String
) : Serializable