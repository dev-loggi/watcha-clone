package com.example.csr83.watchaproject.presentation.recommendation

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.data.remote.Movie
import com.example.csr83.watchaproject.utils.Constants
import com.example.csr83.watchaproject.presentation.exoplayer.ExoPlayerActivity
import kotlinx.android.synthetic.main.recycler_view_card.view.*
import java.util.*
import kotlin.collections.ArrayList

class RecommendationRvAdapter(
    private val view: RecommendationContract.View
) : RecyclerView.Adapter<RecommendationRvAdapter.CardViewHolder>() {
    private val TAG = javaClass.simpleName

    private var listMovie = ArrayList<Movie>()

    override fun getItemCount(): Int = listMovie.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = CardViewHolder(parent)

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        listMovie[position].let { item ->
            with(holder) {
                tvCardTitle.text = "트렌드 추천"
                tvCardSubtitleLeft.text = "요즘 왓챠 인기작 중, "
                tvCardSubtitleUserName.text = "조성록"
                tvCardSubtitleRight.text = "님이 좋아할 작품"
                Log.d(TAG, "onBindViewHolder(), image_wide=${item.image_wide}")
                Glide.with(view.mContext)
                    .load(item.image_wide)
                    .fitCenter()
                    .centerCrop()
                    .into(ivMovie)
                tvMovieTitle.text = item.title
                tvMovieSubtitle.text = "영화 · ${item.year}"

                if (position < 3) {
                    ivPlay.visibility = View.VISIBLE
                    ivPlay.setOnClickListener {
                        view.mContext.startActivity(
                            Intent(view.mContext, ExoPlayerActivity::class.java)
                                .putExtra(Constants.INTENT_KEY_MOVIE_TITLE, item.title)
                        )
                    }
                }

                itemView.setOnClickListener {
                    view.startMovieDetailActivity(item, ivMovie)
//                    view.startMovieDetailFragment(item)
                }
            }
        }
    }

    fun setData(movies: List<Movie>) {
        listMovie.clear()
        val random = Random()
        for (movie in movies) {
            if (random.nextInt(4) == 0) {
                listMovie.add(movie)
            }
        }
//        listMovie.addAll(movies)
        notifyDataSetChanged()
    }

    inner class CardViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_card, parent, false)) {

        val tvCardTitle = itemView.tv_card_title
        val tvCardSubtitleLeft = itemView.tv_card_subtitle_left
        val tvCardSubtitleUserName = itemView.tv_card_subtitle_user_name
        val tvCardSubtitleRight = itemView.tv_card_subtitle_right
        val ivMovie = itemView.iv_movie
        val ivPlay = itemView.iv_play
        val tvMovieTitle = itemView.tv_movie_title
        val tvMovieSubtitle = itemView.tv_movie_subtitle
    }
}