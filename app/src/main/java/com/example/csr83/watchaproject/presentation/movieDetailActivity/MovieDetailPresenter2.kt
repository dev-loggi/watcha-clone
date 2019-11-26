package com.example.csr83.watchaproject.presentation.movieDetailActivity

import android.content.Context
import com.example.csr83.watchaproject.data.remote.Movie
import com.example.csr83.watchaproject.model.movie_detail.MovieDetailRepository
import com.example.csr83.watchaproject.model.movie_detail.MovieDetailSource

class MovieDetailPresenter2(
    private val view: MovieDetailContract2.View,
    private val rvAdapter2: MovieDetailMoreMovieRvAdapter2
) : MovieDetailContract2.Presenter {

    private val repository = MovieDetailRepository(view.mContext)

    init {
        repository.init()
    }

    override fun loadMovieRating(context: Context, title: String) {
        repository.getMovieRating(title, object : MovieDetailSource.LoadMovieRatingCallBack {
            override fun onLoadMovieRating(rating: Float) {
                view.showMovieRating(rating)
            }
        })
    }

    override fun updateMovieRating(title: String, rating: Float) {
        repository.updateMovieRating(title, rating)
    }

    override fun loadMoreMovies() {
        view.showRvMoreMovieLoadingSpinner()
        repository.getMoreMovies(object :MovieDetailSource.LoadMoreMoviesCallBack {
            override fun onLoadMoreMovies(list: List<Movie>) {
                view.showMoreMovies(list)
                view.showRvMoreMovie()
                view.hideRvMoreMovieLoadingSpinner()
            }
        })
    }

    override fun onDestroyView() {
        repository.close()
    }
}