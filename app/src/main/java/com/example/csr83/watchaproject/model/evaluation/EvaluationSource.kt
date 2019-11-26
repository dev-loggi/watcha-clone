package com.example.csr83.watchaproject.model.evaluation

import android.content.Context
import com.example.csr83.watchaproject.data.rating.MovieRating
import com.example.csr83.watchaproject.data.remote.Movie
import com.example.csr83.watchaproject.model.movie_detail.MovieDetailSource

interface EvaluationSource {


    interface LoadMovieAndRatingsCallBack {
        fun onLoadMovieAndRatings(movieRatings: List<MovieRating>)
    }

    fun init()
    fun getMovieAndRatings(callback: LoadMovieAndRatingsCallBack)
    fun updateMovieRating(title: String, rating: Float)
    fun close()
}