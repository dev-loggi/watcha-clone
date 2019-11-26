//package com.example.csr83.watchaproject.legacy.movieDetail
//
//import android.content.Context
//import com.example.csr83.watchaproject.model.movie_detail.MovieDetailRepository
//import com.example.csr83.watchaproject.model.movie_detail.MovieDetailSource
//
//class MovieDetailPresenter(
//    private val view: MovieDetailContract.View,
//    private val rvAdapter: MovieDetailMoreMovieRvAdapter
//) : MovieDetailContract.Presenter {
//
//    private val repository = MovieDetailRepository
//
//    init {
//        val context = (view as MovieDetailFragment).context
//        if (context != null) {
//            repository.init(context)
//        }
//    }
//
//    override fun loadMovieRating(context: Context, title: String) {
//        repository.getMovieRating(title, object : MovieDetailSource.LoadDataCallBack {
//            override fun onLoadData(rating: Float) {
//                view.onLoadedMovieRating(rating)
//            }
//        })
//    }
//
//    override fun updateMovieRating(title: String, rating: Float) {
//        repository.updateMovieRating(title, rating)
//
//    }
//
//    override fun onDestroyView() {
//        repository.close()
//    }
//}