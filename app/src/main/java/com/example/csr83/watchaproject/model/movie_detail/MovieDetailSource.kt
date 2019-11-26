package com.example.csr83.watchaproject.model.movie_detail

import android.content.Context
import com.example.csr83.watchaproject.data.remote.Movie

interface MovieDetailSource {

    interface LoadMovieRatingCallBack {
        fun onLoadMovieRating(rating: Float)
    }

    fun init()
    fun getMovieRating(title: String, callback: LoadMovieRatingCallBack)
    fun updateMovieRating(title: String, rating: Float)
    fun close()

    interface LoadMoreMoviesCallBack {
        fun onLoadMoreMovies(list: List<Movie>)
    }

    fun getMoreMovies(callback: LoadMoreMoviesCallBack)
}