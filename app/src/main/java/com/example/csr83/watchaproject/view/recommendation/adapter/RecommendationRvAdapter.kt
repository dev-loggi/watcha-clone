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

class RecommendationRvAdapter(val fragment: RecommendationFragment) : RecyclerView.Adapter<RecommendationRvAdapter.CardViewHolder>() {

    val TAG = "RecommendationRvAdapter"

    private var listMovie = ArrayList<Movie>()

    init {
        listMovie = Utils.loadData(fragment.activity!!)
    }

    override fun getItemCount(): Int { return 20 }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = CardViewHolder(parent)

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        listMovie[position].let { item ->
            with(holder) {
                tvCardTitle.text = "트렌드 추천"
                tvCardSubtitleLeft.text = "요즘 왓챠 인기작 중, "
                tvCardSubtitleUserName.text = "조성록"
                tvCardSubtitleRight.text = "님이 좋아할 작품"
                Log.d(TAG, "onBindViewHolder(), image_wide=${item.image_wide}")
                Glide.with(fragment.activity)
                    .load(item.image_wide)
                    .fitCenter()
                    .centerCrop()
                    .into(ivMovie)
                tvMovieTitle.text = item.title
                tvMovieSubtitle.text = "영화 · ${item.year}"

                itemView.setOnClickListener {
                    fragment.startMovieDetailFragment(item)
                }
            }
        }
    }

    inner class CardViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_card, parent, false)) {

        val tvCardTitle = itemView.tv_card_title
        val tvCardSubtitleLeft = itemView.tv_card_subtitle_left
        val tvCardSubtitleUserName = itemView.tv_card_subtitle_user_name
        val tvCardSubtitleRight = itemView.tv_card_subtitle_right
        val ivMovie = itemView.iv_movie
        val tvMovieTitle = itemView.tv_movie_title
        val tvMovieSubtitle = itemView.tv_movie_subtitle
    }
}