package com.example.csr83.watchaproject.presentation.search

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.data.local.RecentSearch
import com.example.csr83.watchaproject.data.remote.Movie
import com.example.csr83.watchaproject.utils.Constants
import com.example.csr83.watchaproject.utils.SimpleDividerItemDecoration
import com.example.csr83.watchaproject.utils.Utils
import com.example.csr83.watchaproject.presentation.BaseChildFragment
import com.example.csr83.watchaproject.presentation.movieDetailActivity.MovieDetailActivity
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.loading_progressbar.*

class SearchFragment : BaseChildFragment(), SearchContract.View {
    private val TAG = javaClass.simpleName

    override val mContext: Context
        get() = context!!

    private lateinit var presenter: SearchPresenter
    private lateinit var searchRecentRvAdapter: SearchRecentRvAdapter
    private lateinit var searchResultRvAdapter: SearchResultRvAdapter

    /**
     * ([BaseChildFragment])'s override functions.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = SearchPresenter(this)
        searchResultRvAdapter = SearchResultRvAdapter(this, presenter)
        searchRecentRvAdapter = SearchRecentRvAdapter(this, presenter)

        initView()
    }

    override fun onBackPressed() {
        if (edt_search.editableText.isEmpty()) {
            super.onBackPressed()
        } else {
            edt_search.editableText.clear()
            presenter.getAllRecentSearchData()
        }
    }

    /**
     * ([SearchContract.View])'s override functions.
     */
    override fun initView() {
        (scrim_status_bar.layoutParams as LinearLayout.LayoutParams).height = Utils.getStatusBarHeight(context)

        with (recycler_view_search_recent) {
            addItemDecoration(SimpleDividerItemDecoration(
                context,
                resources.getDimension(R.dimen.search_fragment_side_margin).toInt(),
                resources.getDimension(R.dimen.search_fragment_side_margin).toInt()
            ))
            adapter = searchRecentRvAdapter
            layoutManager = LinearLayoutManager(context)
        }
        with (recycler_view_search_result) {
            adapter = searchResultRvAdapter
            layoutManager = GridLayoutManager(context, 3)
        }

        presenter.getAllRecentSearchData()

        iv_back.setOnClickListener {
            onBackPressed()
        }

        with (edt_search) {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (recycler_view_search_recent.visibility == View.GONE) {
                        showRvSearchRecent(null)
                        hideNoSearchData()
                        hideRvSearchResult()
                    }
                }
            })
            setOnEditorActionListener { view, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(view.editableText.toString())
                }
                true
            }
            Utils.showSoftInput(context!!, edt_search)
        }

        iv_search.setOnClickListener {
            search(edt_search.editableText.toString())
        }
    }

    override fun showRvSearchRecent(recentSearches: List<RecentSearch>?) {
        if (recentSearches != null) {
            searchRecentRvAdapter.insertAllItems(recentSearches)
        } else {
            searchRecentRvAdapter.notifyDataSetChanged()
        }
        recycler_view_search_recent.visibility = View.VISIBLE
    }
    override fun hideRvSearchRecent() {
        recycler_view_search_recent.visibility = View.GONE
    }

    override fun insertRvSearchRecentItem(recentSearch: RecentSearch) { searchRecentRvAdapter.insertItem(recentSearch) }
    override fun removeRvSearchRecentItem(text: String) { searchRecentRvAdapter.removeItem(text) }
    override fun removeAllRvSearchRecentItem() { searchRecentRvAdapter.removeAllItems() }

    override fun showAlertDialogRemoveItem(isRemoveAll: Boolean, text: String?) {
        AlertDialog.Builder(context)
            .setMessage(
                if (isRemoveAll) R.string.alert_dialog_message_remove_all_recent_search_history
                else R.string.alert_dialog_message_remove_recent_search_history)
            .setPositiveButton("확인") { _, _ ->
                if (isRemoveAll) presenter.deleteAllSearchData()
                else presenter.deleteSearchData(text!!) }
            .setNegativeButton("취소") { _, _ -> }
            .create()
            .show()
    }

    override fun showRvSearchResult(movies: List<Movie>) {
        searchResultRvAdapter.setSearchResult(movies)
        recycler_view_search_result_container.visibility = View.VISIBLE
    }
    override fun hideRvSearchResult() {
        recycler_view_search_result_container.visibility = View.GONE
    }

    override fun showNoSearchData() { ll_no_search_data.visibility = View.VISIBLE }
    override fun hideNoSearchData() { ll_no_search_data.visibility = View.GONE }

    override fun showLoadingSpinner() { loading_spinner.visibility = View.VISIBLE }
    override fun hideLoadingSpinner() { loading_spinner.visibility = View.GONE }

    override fun showSoftInput() { Utils.showSoftInput(context!!, edt_search) }
    override fun hideSoftInput() { Utils.hideSoftInput(context!!, edt_search) }

    override fun startMovieDetailActivity(movie: Movie, sharedElement: View) {
        if (Build.VERSION.SDK_INT >= 21) {
            startActivity(
                Intent(activity, MovieDetailActivity::class.java).putExtra(Constants.ARG_PARAM_MOVIE, movie),
                ActivityOptions.makeSceneTransitionAnimation(activity, sharedElement, sharedElement.transitionName).toBundle()
            )
        }
    }

    /**
     * Public Functions.
     */
    fun search(text: String) {
        if (text.isEmpty())
            return
        edt_search.editableText.clear()
        edt_search.editableText.append(text)
        presenter.executeSearch(text)
    }

    companion object {
        @JvmStatic
        fun newInstance(tabPosition: Int, fragmentFloor: Int) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM_TAB_POSITION, tabPosition)
                    putInt(ARG_PARAM_FRAGMENT_FLOOR, fragmentFloor)
                }
            }
    }
}