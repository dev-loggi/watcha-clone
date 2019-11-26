package com.example.csr83.watchaproject.model.movie_detail

import android.content.Context
import com.example.csr83.watchaproject.data.remote.Movie

class MovieDetailRepository(
    private val context: Context
) : MovieDetailSource {

    private val movieRatingSource = MovieDetailDataSource(context)

    override fun init() {
        movieRatingSource.init()
    }

    override fun getMovieRating(title: String, callback: MovieDetailSource.LoadMovieRatingCallBack) {
        movieRatingSource.getMovieRating(title, object : MovieDetailSource.LoadMovieRatingCallBack {
            override fun onLoadMovieRating(rating: Float) {
                callback.onLoadMovieRating(rating)
            }
        })
    }

    override fun updateMovieRating(title: String, rating: Float) {
        movieRatingSource.updateMovieRating(title, rating)
    }

    override fun close() {
        movieRatingSource.close()
    }

    override fun getMoreMovies(callback: MovieDetailSource.LoadMoreMoviesCallBack) {
        movieRatingSource.getMoreMovies(object : MovieDetailSource.LoadMoreMoviesCallBack {
            override fun onLoadMoreMovies(list: List<Movie>) {
                callback.onLoadMoreMovies(list)
            }
        })
    }
}