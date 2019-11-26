package com.example.csr83.watchaproject.presentation.search

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.data.remote.Movie
import kotlinx.android.synthetic.main.recycler_view_search_result_item.view.*

class SearchResultRvAdapter(
    private val view: SearchContract.View,
    private val presenter: SearchContract.Presenter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = javaClass.simpleName

    private val listMovie = arrayListOf<Movie>()

    override fun getItemCount(): Int = listMovie.size

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder = ItemViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with (holder as ItemViewHolder) {
            val movie = listMovie[position]

            Glide.with(view.mContext)
                .load(movie.image_tall)
                .centerCrop()
                .fitCenter()
                .into(ivMovie)

            tvTitle.text = movie.title
            tvSubtitle.text = "${movie.year} · 한국"

            itemView.setOnClickListener {
                view.startMovieDetailActivity(movie, ivMovie)
            }
        }
    }

    fun setSearchResult(list: List<Movie>) {
        listMovie.clear()
        listMovie.addAll(list)
        notifyDataSetChanged()
    }

    private inner class ItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(view.mContext)
            .inflate(R.layout.recycler_view_search_result_item, parent, false)) {
        val ivMovie = itemView.iv_movie
        val tvTitle = itemView.tv_movie_title
        val tvSubtitle = itemView.tv_movie_subtitle
    }
}