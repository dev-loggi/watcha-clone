package com.example.csr83.watchaproject.presentation.movieDetailActivity

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.data.remote.Movie
import kotlinx.android.synthetic.main.recycler_view_card.view.*
import java.util.*
import kotlin.collections.ArrayList

class MovieDetailMoreMovieRvAdapter2(
    val view: MovieDetailContract2.View
) : RecyclerView.Adapter<MovieDetailMoreMovieRvAdapter2.CardViewHolder>() {
    val TAG = javaClass.simpleName

    private val listMovie = ArrayList<Movie>()

    override fun getItemCount(): Int = listMovie.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = CardViewHolder(parent)

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        listMovie[position].let { item ->
            with(holder) {
                Glide.with(view.mContext)
                    .load(item.image_tall)
                    .fitCenter()
                    .centerCrop()
                    .into(ivMovie)
                tvMovieTitle.text = item.title
                tvMovieSubtitle.text = "${item.year} · 한국"

                itemView.setOnClickListener {
                    view.startMovieDetailActivity(item, ivMovie)
                }
            }
        }
    }

    fun addAllMoreMovies(list: List<Movie>) {
        listMovie.clear()
//        listMovie.addAll(list)
        val random = Random()
        for (i in 0 until list.size) {
            if (random.nextInt() % 2 == 0) {
                listMovie.add(0, list[i])
            }
        }
        notifyDataSetChanged()
    }

    inner class CardViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_card2, parent, false)) {

        val ivMovie = itemView.iv_movie
        val tvMovieTitle = itemView.tv_movie_title
        val tvMovieSubtitle = itemView.tv_movie_subtitle
    }
}