package com.example.csr83.watchaproject.presentation.evaluation

import android.content.Context
import com.example.csr83.watchaproject.data.remote.Movie

interface EvaluationContract {

    interface View {
        val mContext: Context

        fun initView()
        fun setHeaderTitle(number: Int)
        fun startMovieDetailActivity(movie: Movie, sharedElement: android.view.View)

        fun showMovies()
        fun hideMovies()

        fun refreshMovies()

        fun showLoadingSpinner()
        fun hideLoadingSpinner()

        fun showSelectCategoryActivity()
        fun showBottomSheetDialogFragment(movie: Movie)
    }

    interface Presenter {

        fun onViewCreated(rvAdapter: EvaluationRvAdapter)
        fun onDestroyView()

        fun loadMovieAndRatings()
        fun saveRating(title: String, rating: Float)
    }
}