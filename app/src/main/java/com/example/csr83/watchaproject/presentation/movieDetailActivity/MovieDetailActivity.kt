package com.example.csr83.watchaproject.presentation.movieDetailActivity

import android.app.ActivityOptions
import android.app.SharedElementCallback
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.RatingBar
import com.bumptech.glide.Glide
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.data.remote.Movie
import com.example.csr83.watchaproject.utils.Constants
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.loading_progressbar.*
import kotlinx.android.synthetic.main.movie_detail_contents_more_movie.*

class MovieDetailActivity : AppCompatActivity(), MovieDetailContract2.View {
    val TAG = "MovieDetailFragment"

    override val mContext: Context
        get() = this

    private lateinit var presenter2: MovieDetailPresenter2
    private lateinit var rvAdapter2: MovieDetailMoreMovieRvAdapter2

    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
//        Utils.setTranslucentStatusBar(window)

        intent?.let {
            movie = it.getSerializableExtra(Constants.ARG_PARAM_MOVIE) as Movie
        }
        rvAdapter2 = MovieDetailMoreMovieRvAdapter2(this)
        presenter2 = MovieDetailPresenter2(this, rvAdapter2)

        startTransitionAnimation()
        initView()
        setHeaderAnimation()

        presenter2.loadMovieRating(this, movie.title)
        presenter2.loadMoreMovies()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter2.onDestroyView()
    }

    /**
     * ([MovieDetailContract2.View])'s override functions.
     */
    override fun startTransitionAnimation() {
        if (Build.VERSION.SDK_INT >= 21) {
            setEnterSharedElementCallback(object : SharedElementCallback() {

                override fun onSharedElementEnd(
                    sharedElementNames: MutableList<String>?,
                    sharedElements: MutableList<View>?,
                    sharedElementSnapshots: MutableList<View>?
                ) {
//                    super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
                    Log.d(TAG, "sharedElementNames=$sharedElementNames")
                    val animTransition = TranslateAnimation(
                        Animation.RELATIVE_TO_PARENT, 0f,
                        Animation.RELATIVE_TO_PARENT, 0f,
                        Animation.RELATIVE_TO_PARENT, 1f,
                        Animation.RELATIVE_TO_PARENT, 0f).apply {
                        startOffset = 200
                        duration = 300
                        interpolator = AnimationUtils.loadInterpolator(this@MovieDetailActivity, android.R.interpolator.accelerate_decelerate)
                    }
                    when (sharedElementNames!![0]) {
                        resources.getString(R.string.transition_name_movie_wide) -> {
                            container_contents.startAnimation(animTransition)
                        }
                        resources.getString(R.string.transition_name_movie_tall) -> {
                            contents.startAnimation(animTransition)
                        }
                    }

                }
            })
        }
    }

    override fun initView() {
        recycler_view_more_movies.adapter = rvAdapter2

        Glide.with(this)
            .load(movie.image_wide)
            .error(R.drawable.watcha)
            .fitCenter()
            .into(iv_movie_wide)

        Glide.with(this)
            .load(movie.image_tall)
            .error(R.drawable.watcha)
            .fitCenter()
            .into(iv_movie_tall)

        tv_movie_title.text = movie.title
        tv_movie_subtitle.text = "${movie.year} · 영화"
        tv_toolbar_title.text = movie.title


        // scrim_status_bar 의 height 설정
//        (scrim_status_bar.layoutParams as ConstraintLayout.LayoutParams).height = Utils.getStatusBarHeight(this)

        ib_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun setHeaderAnimation() {
        // 스크롤뷰 리스너
        nested_scroll_view.setOnScrollChangeListener { view: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            // 상단바 show & hide
            if (contents.y < scrollY) {
                toolbar_layout.setBackgroundDrawable(resources.getDrawable(R.drawable.toolbar_background))
                tv_toolbar_title.visibility = View.VISIBLE
                ib_back.setColorFilter(resources.getColor(android.R.color.holo_red_light))
                ib_share.setColorFilter(resources.getColor(android.R.color.holo_red_light))
            } else {
                toolbar_layout.setBackgroundColor(resources.getColor(android.R.color.transparent))
                tv_toolbar_title.visibility = View.GONE
                ib_back.setColorFilter(resources.getColor(android.R.color.white))
                ib_share.setColorFilter(resources.getColor(android.R.color.white))
            }
        }
    }

    override fun onBackPressed() {
//        supportFinishAfterTransition()
        finish()
    }

    override fun startMovieDetailActivity(movie: Movie, sharedElement: View) {
        if (Build.VERSION.SDK_INT >= 21) {
            startActivity(
                Intent(this, MovieDetailActivity::class.java).putExtra(Constants.ARG_PARAM_MOVIE, movie),
                ActivityOptions.makeSceneTransitionAnimation(this, sharedElement, sharedElement.transitionName).toBundle()
            )
        }
    }

    override fun showMovieRating(rating: Float) {
        with(ratingBar) {
            this.rating = rating
            onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                Log.d(TAG, "OnRatingBarChangeListener(), rating=$rating, fromUser=$fromUser")
                if (!fromUser)
                    return@OnRatingBarChangeListener

                presenter2.updateMovieRating(movie.title, rating)
            }
        }
    }

    override fun showMoreMovies(list: List<Movie>) {
        rvAdapter2.addAllMoreMovies(list)
    }

    override fun showRvMoreMovie() {
        recycler_view_more_movies?.visibility = View.VISIBLE
    }

    override fun hideRvMoreMovie() {
        recycler_view_more_movies?.visibility = View.GONE
    }

    override fun showRvMoreMovieLoadingSpinner() {
        loading_spinner?.visibility = View.VISIBLE
    }

    override fun hideRvMoreMovieLoadingSpinner() {
        loading_spinner?.visibility = View.GONE
    }
}