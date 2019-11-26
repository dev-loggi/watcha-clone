package com.example.csr83.watchaproject.presentation.evaluation

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RatingBar
import com.bumptech.glide.Glide
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.data.rating.MovieRating
import com.example.csr83.watchaproject.data.rating.converted
import kotlinx.android.synthetic.main.recycler_view_evaluation_item.view.*

class EvaluationRvAdapter(
    private val view: EvaluationContract.View,
    private val presenter: EvaluationPresenter
) : RecyclerView.Adapter<EvaluationRvAdapter.CardViewHolder>() {
    val TAG = javaClass.simpleName

    private var listMovieRating = arrayListOf<MovieRating>()
    private var prePosition = -1

    override fun getItemCount(): Int = listMovieRating.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = CardViewHolder(parent)

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        listMovieRating[position].let { item ->
            with(holder) {
                val animItemShow = AnimationUtils.loadAnimation(view.mContext,
                    if (position > prePosition)
                        R.anim.anim_recycler_item_show
                    else
                        R.anim.anim_recycler_item_show_reverse
                )
                itemView.startAnimation(animItemShow)
                prePosition = position

                Glide.with(view.mContext)
                    .load(item.image_tall)
                    .fitCenter()
                    .centerCrop()
                    .into(ivMovie)

                tvMovieTitle.text = item.title
                tvMovieSubtitle.text = "${item.year} · 한국"

                ivMovie.setOnClickListener {
                    view.startMovieDetailActivity(item.converted(), ivMovie)
                }

                ibMore.setOnClickListener {
                    view.showBottomSheetDialogFragment(item.converted())
                }

                setRatingBar(ratingBar, item, position)
            }
        }
    }

    fun setMovieAndRatings(movieRatings: List<MovieRating>) {
        listMovieRating.clear()
        listMovieRating.addAll(movieRatings)
        view.setHeaderTitle(getNumberOfMoviesRated())
        notifyDataSetChanged()
    }

    @SuppressWarnings("ClickableViewAccessibility")
    private fun setRatingBar(ratingBar: RatingBar, item: MovieRating, position: Int) {
        ratingBar.rating = item.rating

        ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (fromUser) {
                presenter.saveRating(item.title, rating)
                listMovieRating[position].rating = rating
                view.setHeaderTitle(getNumberOfMoviesRated())
//                SQLiteHelper!!.updateMovieRating(item.title, rating)
//                listRatingPoints[position] = rating
//                view.setHeaderTitle(getNumberOfMoviesRated())
            }
        }
    }

    private fun getNumberOfMoviesRated(): Int {
        var sum = 0
        for (i in listMovieRating.indices) {
            if (listMovieRating[i].rating > 0) {
                sum++
            }
        }
        return sum
    }

    inner class CardViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_evaluation_item, parent, false)) {

        val ivMovie = itemView.iv_movie
        val tvMovieTitle = itemView.tv_movie_title
        val tvMovieSubtitle = itemView.tv_movie_subtitle
        val ratingBar = itemView.ratingBar
        val ibMore = itemView.ib_more
    }
}