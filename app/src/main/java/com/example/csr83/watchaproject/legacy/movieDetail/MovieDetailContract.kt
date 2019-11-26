//package com.example.csr83.watchaproject.legacy.movieDetail
//
//import android.content.Context
//import com.example.csr83.watchaproject.data.remote.Movie
//
//interface MovieDetailContract {
//
//    interface View {
//        fun initView()
//        fun setHeaderAnimation()
//        fun addOnBackPressedListener()
//        fun startMovieDetailFragment(movie: Movie)
//        fun onLoadedMovieRating(rating: Float)
//    }
//    interface Presenter {
//        fun loadMovieRating(context: Context, title: String)
//        fun updateMovieRating(title: String, rating: Float)
//        fun onDestroyView()
//    }
//}