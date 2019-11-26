package com.example.csr83.watchaproject.presentation.search

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.data.local.RecentSearch
import kotlinx.android.synthetic.main.recycler_view_search_recent_item.view.*

class SearchRecentRvAdapter(
    private val view: SearchContract.View,
    private val presenter: SearchContract.Presenter
) : RecyclerView.Adapter<SearchRecentRvAdapter.ViewHolder>() {
    private val TAG = javaClass.simpleName

    companion object {
        const val HEADER = 0
        const val TEXT = 1
    }

    private val listRecentSearch = arrayListOf<RecentSearch>()

    override fun getItemViewType(position: Int): Int =
        if (position == 0) HEADER
        else TEXT

    override fun getItemCount(): Int = listRecentSearch.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder =
        if (position == 0)  ViewHolder(parent, HEADER)
        else                ViewHolder(parent, TEXT)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with (holder) {
            when (getItemViewType(position)) {
                HEADER -> {
                    tvDeleteAll.setOnClickListener {
                        view.showAlertDialogRemoveItem(true)
                    }
                }
                TEXT -> {
                    val index = position - 1

                    tvSearch.text = listRecentSearch[index].searchData
                    itemView.setOnClickListener {
                        (view as SearchFragment).search(listRecentSearch[index].searchData)
                    }

                    ivDeleteOne.setOnClickListener {
                        view.showAlertDialogRemoveItem(false, listRecentSearch[index].searchData)
                    }
                }
            }
        }
    }

    fun insertItem(recentSearch: RecentSearch) {
        listRecentSearch.add(0, recentSearch)
        notifyDataSetChanged()
    }

    fun insertAllItems(recentSearches: List<RecentSearch>) {
        listRecentSearch.clear()
        for (search in recentSearches) {
            listRecentSearch.add(0, search)
        }
        notifyDataSetChanged()
    }

    fun removeItem(text: String) {
        listRecentSearch.removeAll { it.searchData == text }
        notifyDataSetChanged()
    }

    fun removeAllItems() {
        listRecentSearch.clear()
        notifyDataSetChanged()
    }


    inner class ViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder(
        LayoutInflater.from(view.mContext)
            .inflate(R.layout.recycler_view_search_recent_item, parent, false)) {

        val tvSearch = itemView.tv_search
        val tvDeleteAll = itemView.tv_delete_all
        val ivDeleteOne = itemView.iv_delete_one

        init {
            when (viewType) {

                HEADER -> {
                    tvSearch.text = "최근 검색어"
                    tvSearch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    tvSearch.setTypeface(null, Typeface.BOLD)
                    tvDeleteAll.visibility = View.VISIBLE
                    ivDeleteOne.visibility = View.GONE
                }

                TEXT -> {

                }
            }
        }
    }
}