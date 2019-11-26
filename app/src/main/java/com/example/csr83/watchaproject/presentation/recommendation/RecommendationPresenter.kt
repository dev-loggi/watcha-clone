package com.example.csr83.watchaproject.presentation.recommendation

import com.example.csr83.watchaproject.data.remote.Movie
import com.example.csr83.watchaproject.model.movie.MovieRepository
import com.example.csr83.watchaproject.model.movie.MovieSource

class RecommendationPresenter(
    private val view: RecommendationContract.View,
    private val adapter: RecommendationRvAdapter
) : RecommendationContract.Presenter {

    private val repository = MovieRepository(view.mContext)

    override fun loadData() {
        view.hideMovieRecyclerView()
        view.showLoadingSpinner()
        repository.getData(null, object : MovieSource.LoadDataCallBack {
            override fun onLoadData(list: List<Movie>) {
                adapter.setData(list)
                view.hideLoadingSpinner()
                view.showMovieRecyclerView()
            }
        })
    }
}