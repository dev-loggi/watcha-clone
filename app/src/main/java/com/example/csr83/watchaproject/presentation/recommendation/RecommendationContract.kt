package com.example.csr83.watchaproject.presentation.recommendation

import android.content.Context
import com.example.csr83.watchaproject.data.remote.Movie

interface RecommendationContract {
    interface View {
        val mContext: Context

        fun setHeaderAnimation()
        fun showMovieRecyclerView()
        fun hideMovieRecyclerView()
        fun showLoadingSpinner()
        fun hideLoadingSpinner()
        fun startMovieDetailActivity(movie: Movie, shareElement: android.view.View)
        fun startSearchFragment()
        fun startStartMovieFragment()
        fun startStartTVFragment()
        fun startStartBookFragment()
    }
    interface Presenter {

        fun loadData()
    }
}