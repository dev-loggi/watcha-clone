package com.example.csr83.watchaproject.data.rating

import com.example.csr83.watchaproject.data.remote.Movie

data class MovieRating(
    var id: Int,
    var title: String,
    var rating: Float,
    var image_wide: String,
    var image_tall: String,
    var year: String) {



    constructor(
        movie: Movie, rating: Float
    ) : this(movie.id!!, movie.title, rating, movie.image_wide, movie.image_tall, movie.year)
}

fun MovieRating.converted() = Movie(id, title, image_wide, image_tall, year)