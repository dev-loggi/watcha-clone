//package com.example.csr83.watchaproject.legacy.movieDetail
//
//import android.support.v7.widget.RecyclerView
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import com.bumptech.glide.Glide
//import com.example.csr83.watchaproject.R
//import com.example.csr83.watchaproject.data.remote.Movie
//import com.example.csr83.watchaproject.utils.Utils
//import kotlinx.android.synthetic.main.recycler_view_card.view.*
//
//class MovieDetailMoreMovieRvAdapter(val fragment: MovieDetailFragment) : RecyclerView.Adapter<MovieDetailMoreMovieRvAdapter.CardViewHolder>() {
//
//    val TAG = javaClass.simpleName
//
//    private var listMovie = ArrayList<Movie>()
//
//    init {
//        listMovie = Utils.loadData(fragment.activity!!)
//    }
//
//    override fun getItemCount(): Int { return 20 }
//
//    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = CardViewHolder(parent)
//
//    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
//        listMovie[position].let { item ->
//            with(holder) {
//                Glide.with(fragment.activity)
//                    .load(item.image_tall)
//                    .fitCenter()
//                    .centerCrop()
//                    .into(ivMovie)
//                tvMovieTitle.text = item.title
//                tvMovieSubtitle.text = "${item.year} · 한국"
//
//                itemView.setOnClickListener {
//                    fragment.startMovieDetailFragment(item)
//                }
//            }
//        }
//    }
//
//    inner class CardViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
//        LayoutInflater.from(parent.context)
//            .inflate(R.layout.recycler_view_card2, parent, false)) {
//
//        val ivMovie = itemView.iv_movie
//        val tvMovieTitle = itemView.tv_movie_title
//        val tvMovieSubtitle = itemView.tv_movie_subtitle
//    }
//}