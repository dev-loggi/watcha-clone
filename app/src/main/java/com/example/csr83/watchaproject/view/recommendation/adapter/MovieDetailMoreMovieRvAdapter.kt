package com.example.csr83.watchaproject.view.recommendation.adapter

import android.content.res.AssetManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.model.Movie
import com.example.csr83.watchaproject.utils.Utils
import com.example.csr83.watchaproject.view.recommendation.MovieDetailFragment
import com.example.csr83.watchaproject.view.recommendation.RecommendationFragment
import kotlinx.android.synthetic.main.recycler_view_card.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream
import java.lang.Exception

class MovieDetailMoreMovieRvAdapter(val fragment: MovieDetailFragment) : RecyclerView.Adapter<MovieDetailMoreMovieRvAdapter.CardViewHolder>() {

    val TAG = javaClass.simpleName

    private var listMovie = ArrayList<Movie>()

    init {
        listMovie = Utils.loadData(fragment.activity!!)
    }

    override fun getItemCount(): Int { return 20 }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = CardViewHolder(parent)

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        listMovie[position].let { item ->
            with(holder) {
                Glide.with(fragment.activity)
                    .load(item.image_tall)
                    .fitCenter()
                    .centerCrop()
                    .into(ivMovie)
                tvMovieTitle.text = item.title
                tvMovieSubtitle.text = "${item.year} · 한국"

                itemView.setOnClickListener {
                    fragment.startMovieDetailFragment(item)
                }
            }
        }
    }

    inner class CardViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_card2, parent, false)) {

        val ivMovie = itemView.iv_movie
        val tvMovieTitle = itemView.tv_movie_title
        val tvMovieSubtitle = itemView.tv_movie_subtitle
    }
}