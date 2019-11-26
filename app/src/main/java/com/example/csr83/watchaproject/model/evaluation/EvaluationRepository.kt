package com.example.csr83.watchaproject.model.evaluation

import android.content.Context
import com.example.csr83.watchaproject.data.rating.MovieRating
import com.example.csr83.watchaproject.data.remote.Movie
import java.util.*
import kotlin.collections.ArrayList

class EvaluationRepository(
    private val context: Context
): EvaluationSource {

    private val evaluationDataSource = EvaluationDataSource(context)

    override fun init() {
        evaluationDataSource.init()
    }

    override fun getMovieAndRatings(callback: EvaluationSource.LoadMovieAndRatingsCallBack) {
        evaluationDataSource.getMovieAndRatings(object : EvaluationSource.LoadMovieAndRatingsCallBack {
            override fun onLoadMovieAndRatings(movieRatings: List<MovieRating>) {
                callback.onLoadMovieAndRatings(movieRatings)
            }
        })
    }

    override fun updateMovieRating(title: String, rating: Float) {
        evaluationDataSource.updateMovieRating(title, rating)
    }

    override fun close() {
        evaluationDataSource.close()
    }
}