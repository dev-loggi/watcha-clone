package com.example.csr83.watchaproject.view.movie_detail

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.model.Movie
import com.example.csr83.watchaproject.utils.Constants
import com.example.csr83.watchaproject.utils.DBHelper
import com.example.csr83.watchaproject.utils.Utils
import com.example.csr83.watchaproject.view.base.BaseChildFragment
import com.example.csr83.watchaproject.view.main.MainActivity
import kotlinx.android.synthetic.main.fragment_movie_detail.*
import kotlinx.android.synthetic.main.movie_detail_contents_more_movie.*


class MovieDetailFragment : BaseChildFragment(), MainActivity.OnBackPressedListener {

    val TAG = "MovieDetailFragment"

    private var movie = Movie("", "", "", "")
    private var dbHelper: DBHelper? = null
    private var movieRatingPoints: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movie = it.getSerializable(Constants.ARG_PARAM_MOVIE) as Movie
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DBHelper(context!!)

        (activity as MainActivity).addOnBackPressedListener(this)

        initView()

        setRatingBar()
        setScrollingAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dbHelper!!.close()
    }

    fun startMovieDetailFragment(movie: Movie) {
        (activity as MainActivity).myFragmentAdapter!!.createNewFragment(
            R.id.fragment_container,
            newInstance(
                super.getTabPosition(),
                super.getFragmentFloor() + 1,
                movie
            ),
            super.getTabPosition(),
            super.getFragmentFloor() + 1
        )
    }

    private fun initView() {
        Glide.with(activity)
            .load(movie.image_wide)
            .error(R.drawable.watcha)
            .fitCenter()
            .into(iv_movie_wide)

        Glide.with(activity)
            .load(movie.image_tall)
            .error(R.drawable.watcha)
            .fitCenter()
            .into(iv_movie_tall)

        tv_movie_title.text = movie.title
        tv_movie_subtitle.text = "${movie.year} · 영화"
        tv_toolbar_title.text = movie.title

        // scrim_status_bar 의 height 설정
        (scrim_status_bar.layoutParams as ConstraintLayout.LayoutParams).height = Utils.getStatusBarHeight(context)

        ib_back.setOnClickListener {
            super.onBackPressed()
        }

        recycler_view.adapter = MovieDetailMoreMovieRvAdapter(this)
    }

    private fun setRatingBar() {
        movieRatingPoints = dbHelper!!.selectMovieRating(movie.title) ?: 0f
        ratingBar.rating = movieRatingPoints

        ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
            Log.d(TAG, "OnRatingBarChangeListener(), rating=$rating, fromUser=$fromUser")
            if (fromUser) {
                dbHelper!!.updateMovieRating(movie.title, rating)
                movieRatingPoints = rating
            }
        }

//        ratingBar.setOnTouchListener { v, event ->
//            Log.d(TAG, "setOnTouchListener(), event.action=${event.action}, ratingBar.rating=${ratingBar.rating}")
//            var returnValue = false
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> ratingBar.tag = ratingBar.rating
//                MotionEvent.ACTION_UP -> {
//                    Log.d(TAG, "setOnTouchListener(), tag=${ratingBar.tag}, rating=${ratingBar.rating}")
//                    if (ratingBar.tag == ratingBar.rating) {
//                        Log.e(TAG, "ok!!")
//                        dbHelper!!.deleteMovieRating(movie.title)
//                        ratingBar.rating = 0f
//                        returnValue = true
//                    }
//                }
//            }
//            returnValue
//        }
    }

    private fun setScrollingAnimation() {
        // 스크롤뷰 리스너
        nested_scroll_view.setOnScrollChangeListener { view: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            // 상단바 show & hide
            if (container_header.y < scrollY) {
                toolbar_layout.setBackgroundDrawable(activity!!.resources.getDrawable(R.drawable.toolbar_background))
                tv_toolbar_title.visibility = View.VISIBLE
                ib_back.setColorFilter(activity!!.resources.getColor(android.R.color.holo_red_light))
                ib_share.setColorFilter(activity!!.resources.getColor(android.R.color.holo_red_light))
            } else {
                toolbar_layout.setBackgroundColor(activity!!.resources.getColor(android.R.color.transparent))
                tv_toolbar_title.visibility = View.GONE
                ib_back.setColorFilter(activity!!.resources.getColor(android.R.color.white))
                ib_share.setColorFilter(activity!!.resources.getColor(android.R.color.white))
            }
        }
    }



    companion object {
        @JvmStatic
        fun newInstance(tabPosition: Int, fragmentFloor: Int, movie: Movie) =
            MovieDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM_TAB_POSITION, tabPosition)
                    putInt(ARG_PARAM_FRAGMENT_FLOOR, fragmentFloor)
                    putSerializable(Constants.ARG_PARAM_MOVIE, movie)
                }
            }
    }
}
