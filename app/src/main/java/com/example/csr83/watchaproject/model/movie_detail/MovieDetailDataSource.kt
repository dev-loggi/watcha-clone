package com.example.csr83.watchaproject.model.movie_detail

import android.content.Context
import android.os.Handler
import com.example.csr83.watchaproject.data.remote.MovieDB
import com.example.csr83.watchaproject.model.SQLiteHelper

class MovieDetailDataSource(private val context: Context) : MovieDetailSource {

    companion object {
        const val SERVICE_LATENCY_IN_MILLIS = 800L
    }

    private var sqliteHelper: SQLiteHelper? = null
    private val movieDao = MovieDB.getInstance(context).movieDAO()

    override fun init() {
        sqliteHelper = SQLiteHelper(context)
    }

    override fun getMovieRating(title: String, callback: MovieDetailSource.LoadMovieRatingCallBack) {
        sqliteHelper?.let {
            Handler().post {
                callback.onLoadMovieRating(it.selectMovieRating(title) ?: 0f)
            }
        }
    }

    override fun updateMovieRating(title: String, rating: Float) {
        sqliteHelper?.let {
            Handler().post {
                it.updateMovieRating(title, rating)
            }
        }
    }

    override fun close() {
        sqliteHelper?.close()
    }

    override fun getMoreMovies(callback: MovieDetailSource.LoadMoreMoviesCallBack) {
        Handler().postDelayed({
            callback.onLoadMoreMovies(movieDao.getAllMovieData().shuffled())
        }, SERVICE_LATENCY_IN_MILLIS)
    }
}