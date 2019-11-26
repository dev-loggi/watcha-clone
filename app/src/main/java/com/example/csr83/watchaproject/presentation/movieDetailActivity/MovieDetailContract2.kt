package com.example.csr83.watchaproject.presentation.movieDetailActivity

import android.content.Context
import com.example.csr83.watchaproject.data.remote.Movie

interface MovieDetailContract2 {

    interface View {
        val mContext: Context

        fun startTransitionAnimation()
        fun initView()
        fun setHeaderAnimation()
        fun startMovieDetailActivity(movie: Movie, sharedElement: android.view.View)

        fun showMovieRating(rating: Float)
        fun showMoreMovies(list: List<Movie>)

        fun showRvMoreMovie()
        fun hideRvMoreMovie()

        fun showRvMoreMovieLoadingSpinner()
        fun hideRvMoreMovieLoadingSpinner()
    }
    interface Presenter {
        fun loadMovieRating(context: Context, title: String)
        fun updateMovieRating(title: String, rating: Float)
        fun loadMoreMovies()
        fun onDestroyView()
    }
}