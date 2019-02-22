package com.example.csr83.watchaproject.view.recommendation

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.model.Movie
import com.example.csr83.watchaproject.utils.DBHelper
import com.example.csr83.watchaproject.utils.Utils
import com.example.csr83.watchaproject.view.MainActivity
import kotlinx.android.synthetic.main.fragment_movie_detail.*
import java.util.concurrent.locks.ReentrantLock


private const val ARG_PARAM1 = "movie"

class MovieDetailFragment : Fragment() {

    val TAG = "MovieDetailFragment"

    private var movie: Movie =
        Movie("", "", "", "")
    private var dbHelper: DBHelper? = null
    private var movieRatingPoints: Float = 0f
    private var statusBarHeight : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movie = it.getSerializable(ARG_PARAM1) as Movie
        }
        (activity as MainActivity).saveFragment(this)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dbHelper = DBHelper(context!!)

        initView()

        setScrollingAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dbHelper!!.close()
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

        tv_movie_title.setText(movie.title)
        tv_movie_subtitle.setText(movie.year + " · 영화")

        movieRatingPoints = dbHelper!!.selectMovieRating(movie.title) ?: 0f
        ratingBar.rating = movieRatingPoints

        // statusbar 의 height 측정
        statusBarHeight = Utils.getStatusBarHeight(context)

        // fake_status_bar 의 height 설정
        val layoutParams = fake_status_bar.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.height = statusBarHeight
        fake_status_bar.layoutParams = layoutParams

        ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
            Log.d(TAG, "OnRatingBarChangeListener(), rating=$rating, fromUser=$fromUser")
            if (fromUser) {
                dbHelper!!.updateMovieRating(movie.title, rating)
            }
        }

        ratingBar.setOnTouchListener { v, event ->
            Log.d(TAG, "setOnTouchListener(), event.action=${event}, movieRatingPoints=$movieRatingPoints, rating=${(v as RatingBar).rating}, ")
//            if (event.action == MotionEvent.ACTION_UP && (v as RatingBar).rating == movieRatingPoints) {
//                Log.e(TAG, "ok")
//                (v as RatingBar).rating = 0f
//                dbHelper!!.deleteMovieRating(movie.title)
//            }
            false
        }

        ib_back.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
        }
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
        fun newInstance(movie : Movie) =
            MovieDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, movie)
                }
            }
    }
}
