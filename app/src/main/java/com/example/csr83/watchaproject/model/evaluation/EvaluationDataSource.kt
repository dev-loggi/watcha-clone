package com.example.csr83.watchaproject.model.evaluation

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.csr83.watchaproject.data.rating.MovieRating
import com.example.csr83.watchaproject.data.remote.MovieDB
import com.example.csr83.watchaproject.model.SQLiteHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EvaluationDataSource(
    private val context: Context
) : EvaluationSource {

    companion object {
        const val SERVICE_LATENCY_IN_MILLIS = 1500L
    }
    private val TAG = javaClass.simpleName

    private val movieDao = MovieDB.getInstance(context).movieDAO()
    private var sqliteHelper: SQLiteHelper? = null

    override fun init() {
        sqliteHelper = SQLiteHelper(context)
    }

    override fun getMovieAndRatings(callback: EvaluationSource.LoadMovieAndRatingsCallBack) {
        if (sqliteHelper == null)
            return

        Handler().postDelayed({
            val movieRatings = arrayListOf<MovieRating>()

            val movies = movieDao.getAllMovieData()
            val ratings = sqliteHelper!!.selectAllMovieRating()

            for (i in movies.indices) {
                val movieRating = MovieRating(movies[i], 0f)

                for (j in ratings.indices) {
                    // ratings 가 존재하는 경우
                    if (movies[i].title == ratings[j].title) {
                        movieRating.rating = ratings[j].rating
                        break
                    }
                }

                movieRatings.add(movieRating)
            }

            callback.onLoadMovieAndRatings(movieRatings.shuffled())
        }, SERVICE_LATENCY_IN_MILLIS)
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
}