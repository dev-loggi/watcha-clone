package com.example.csr83.watchaproject.presentation.search

import android.content.Context
import com.example.csr83.watchaproject.data.local.RecentSearch
import com.example.csr83.watchaproject.data.remote.Movie


interface SearchContract {

    interface View {
        val mContext: Context

        fun initView()

        fun showRvSearchRecent(recentSearches: List<RecentSearch>?)
        fun hideRvSearchRecent()

        fun insertRvSearchRecentItem(recentSearch: RecentSearch)
        fun removeRvSearchRecentItem(text: String)
        fun removeAllRvSearchRecentItem()

        fun showAlertDialogRemoveItem(isRemoveAll: Boolean, text: String? = null)

        fun showRvSearchResult(movies: List<Movie>)
        fun hideRvSearchResult()

        fun showNoSearchData()
        fun hideNoSearchData()

        fun showLoadingSpinner()
        fun hideLoadingSpinner()

        fun showSoftInput()
        fun hideSoftInput()

        fun startMovieDetailActivity(movie: Movie, sharedElement: android.view.View)
    }

    interface Presenter {
        fun getAllRecentSearchData()
        fun deleteSearchData(text: String)
        fun deleteAllSearchData()
        fun insertSearchData(text: String)
        fun executeSearch(text: String)
    }
}