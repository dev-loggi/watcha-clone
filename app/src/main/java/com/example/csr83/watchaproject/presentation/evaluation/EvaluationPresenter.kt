package com.example.csr83.watchaproject.presentation.evaluation

import android.util.Log
import com.example.csr83.watchaproject.data.rating.MovieRating
import com.example.csr83.watchaproject.model.evaluation.EvaluationRepository
import com.example.csr83.watchaproject.model.evaluation.EvaluationSource

class EvaluationPresenter(
    private val view: EvaluationContract.View
) : EvaluationContract.Presenter {
    private val TAG = javaClass.simpleName

    private lateinit var rvAdapter: EvaluationRvAdapter
    private val repository = EvaluationRepository(view.mContext)

    override fun onViewCreated(rvAdapter: EvaluationRvAdapter) {
        this.rvAdapter = rvAdapter
        repository.init()
    }

    override fun onDestroyView() {
        repository.close()
    }

    override fun loadMovieAndRatings() {
        Log.d(TAG, "loadMovieAndRatings()")
        view.hideMovies()
        view.showLoadingSpinner()
        repository.getMovieAndRatings(object : EvaluationSource.LoadMovieAndRatingsCallBack {
            override fun onLoadMovieAndRatings(movieRatings: List<MovieRating>) {
                Log.d(TAG, "onLoadMovieAndRatings()")
                rvAdapter.setMovieAndRatings(movieRatings)
                view.hideLoadingSpinner()
                view.showMovies()
            }
        })
    }

    override fun saveRating(title: String, rating: Float) {
        repository.updateMovieRating(title, rating)
    }
}