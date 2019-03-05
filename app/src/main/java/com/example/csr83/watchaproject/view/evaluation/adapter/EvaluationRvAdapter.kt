package com.example.csr83.watchaproject.view.evaluation.adapter

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RatingBar
import com.bumptech.glide.Glide
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.model.Movie
import com.example.csr83.watchaproject.utils.Constants
import com.example.csr83.watchaproject.utils.DBHelper
import com.example.csr83.watchaproject.utils.Utils
import com.example.csr83.watchaproject.view.evaluation.CustomBottomSheetDialogFragment
import com.example.csr83.watchaproject.view.evaluation.EvaluationFragment
import kotlinx.android.synthetic.main.recycler_view_evaluation_item.view.*

class EvaluationRvAdapter(val fragment: EvaluationFragment) : RecyclerView.Adapter<EvaluationRvAdapter.CardViewHolder>() {

    val TAG = javaClass.simpleName

    private var listMovie = arrayListOf<Movie>()
    private var listRatingPoints = arrayListOf<Float>()
    private var dbHelper: DBHelper? = null

    init {
        listMovie = Utils.loadData(fragment.activity!!)
        dbHelper = DBHelper(fragment.activity!!)

        for (movie in listMovie) {
            listRatingPoints.add(dbHelper!!.selectMovieRating(movie.title) ?: 0f)
        }
        fragment.setHeaderTitle(getNumberOfMoviesRated())
    }

    override fun getItemCount(): Int { return listMovie.size }

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

                ivMovie.setOnClickListener {
                    fragment.startMovieDetailFragment(item)
                }

                ibMore.setOnClickListener {
                    val dialog = CustomBottomSheetDialogFragment()
                    val arg = Bundle()
                    arg.putSerializable(Constants.ARG_PARAM_MOVIE, item)
                    dialog.arguments = arg
                    dialog.show(fragment.activity!!.supportFragmentManager, dialog.tag)
                }

                setRatingBar(ratingBar, item, position)
            }
        }
    }

    @SuppressWarnings("ClickableViewAccessibility")
    private fun setRatingBar(ratingBar: RatingBar, item: Movie, position: Int) {
        ratingBar.rating = dbHelper!!.selectMovieRating(item.title) ?: 0f

        ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (fromUser) {
                dbHelper!!.updateMovieRating(item.title, rating)
                listRatingPoints[position] = rating
                fragment.setHeaderTitle(getNumberOfMoviesRated())
            }
        }
    }

    private fun getNumberOfMoviesRated(): Int {
        var sum = 0
        for (rating in listRatingPoints) {
            if (rating > 0) {
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