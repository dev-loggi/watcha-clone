package com.example.csr83.watchaproject.presentation.search

import com.example.csr83.watchaproject.data.local.RecentSearch
import com.example.csr83.watchaproject.data.local.RecentSearchDB
import com.example.csr83.watchaproject.data.local.RecentSearchDao
import com.example.csr83.watchaproject.data.remote.Movie
import com.example.csr83.watchaproject.model.movie.MovieRepository
import com.example.csr83.watchaproject.model.movie.MovieSource

class SearchPresenter(
    private val view: SearchContract.View
) : SearchContract.Presenter {

    private val recentSearchDao: RecentSearchDao = RecentSearchDB.getInstance(view.mContext).recentSearchDAO()
    private val repository: MovieRepository = MovieRepository(view.mContext)

    override fun getAllRecentSearchData() {
        view.showLoadingSpinner()
        recentSearchDao.getAllSearchData().let {
            view.hideLoadingSpinner()
            view.showRvSearchRecent(it)
        }
    }

    override fun deleteSearchData(text: String) {
        recentSearchDao.deleteSearchData(text)
        view.removeRvSearchRecentItem(text)
    }

    override fun deleteAllSearchData() {
        recentSearchDao.deleteAllSearchData()
        view.removeAllRvSearchRecentItem()
    }

    override fun insertSearchData(text: String) {
        if (recentSearchDao.getSearchData(text) != null) {
            recentSearchDao.deleteSearchData(text)
            view.removeRvSearchRecentItem(text)
        }

        RecentSearch(null, text).let {
            recentSearchDao.insert(it)
            view.insertRvSearchRecentItem(it)
        }
    }

    override fun executeSearch(text: String) {
        insertSearchData(text)

        view.hideSoftInput()
        view.hideRvSearchRecent()
        view.hideNoSearchData()
        view.showLoadingSpinner()

        repository.getData("%$text%", object : MovieSource.LoadDataCallBack {
            override fun onLoadData(list: List<Movie>) {
                view.hideLoadingSpinner()
                if (list.isEmpty()) {
                    view.showNoSearchData()
                } else {
                    view.showRvSearchResult(list)
                }
            }
        })
    }
}