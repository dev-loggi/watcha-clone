//package com.example.csr83.watchaproject.legacy.movieDetail
//
//import android.os.Bundle
//import android.support.constraint.ConstraintLayout
//import android.support.v4.widget.NestedScrollView
//import android.util.Log
//import android.view.*
//import android.widget.*
//import com.bumptech.glide.Glide
//
//import com.example.csr83.watchaproject.R
//import com.example.csr83.watchaproject.data.remote.Movie
//import com.example.csr83.watchaproject.utils.Constants
//import com.example.csr83.watchaproject.utils.Utils
//import com.example.csr83.watchaproject.view.BaseChildFragment
//import com.example.csr83.watchaproject.view.main.MainActivity
//import kotlinx.android.synthetic.main.fragment_movie_detail.*
//import kotlinx.android.synthetic.main.movie_detail_contents_more_movie.*
//
//
//class MovieDetailFragment : BaseChildFragment(), MovieDetailContract.View, MainActivity.OnBackPressedListener {
//
//    val TAG = "MovieDetailFragment"
//
//    private lateinit var presenter: MovieDetailPresenter
//    private lateinit var rvAdapter: MovieDetailMoreMovieRvAdapter
//
//    private lateinit var movie: Movie
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Utils.setTranslucentStatusBar(activity!!.window)
//        arguments?.let {
//            movie = it.getSerializable(Constants.ARG_PARAM_MOVIE) as Movie
//        }
//        rvAdapter = MovieDetailMoreMovieRvAdapter(this)
//        presenter = MovieDetailPresenter(this, rvAdapter)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? = inflater.inflate(R.layout.fragment_movie_detail, container, false)
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
////        nested_scroll_view.visibility = View.GONE
//
//        initView()
//        setHeaderAnimation()
//        addOnBackPressedListener()
//
//
//        if (context != null) {
//            presenter.loadMovieRating(context!!, movie.title)
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        presenter.onDestroyView()
//    }
//
//
//    /**
//     * ([MovieDetailContract.View])'s override functions.
//     */
//    override fun initView() {
//        recycler_view.adapter = rvAdapter
//
//        Glide.with(activity)
//            .load(movie.image_wide)
//            .error(R.drawable.watcha)
//            .fitCenter()
//            .into(iv_movie_wide)
//
//        Glide.with(activity)
//            .load(movie.image_tall)
//            .error(R.drawable.watcha)
//            .fitCenter()
//            .into(iv_movie_tall)
//
//        tv_movie_title.text = movie.title
//        tv_movie_subtitle.text = "${movie.year} · 영화"
//        tv_toolbar_title.text = movie.title
//
//        // scrim_status_bar 의 height 설정
//        (scrim_status_bar.layoutParams as ConstraintLayout.LayoutParams).height = Utils.getStatusBarHeight(context)
//
//        ib_back.setOnClickListener {
//            super.onBackPressed()
//        }
//    }
//
//    override fun setHeaderAnimation() {
//        // 스크롤뷰 리스너
//        nested_scroll_view.setOnScrollChangeListener { view: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
//            // 상단바 show & hide
//            if (contents.y < scrollY) {
//                toolbar_layout.setBackgroundDrawable(activity!!.resources.getDrawable(R.drawable.toolbar_background))
//                tv_toolbar_title.visibility = View.VISIBLE
//                ib_back.setColorFilter(activity!!.resources.getColor(android.R.color.holo_red_light))
//                ib_share.setColorFilter(activity!!.resources.getColor(android.R.color.holo_red_light))
//            } else {
//                toolbar_layout.setBackgroundColor(activity!!.resources.getColor(android.R.color.transparent))
//                tv_toolbar_title.visibility = View.GONE
//                ib_back.setColorFilter(activity!!.resources.getColor(android.R.color.white))
//                ib_share.setColorFilter(activity!!.resources.getColor(android.R.color.white))
//            }
//        }
//    }
//
//    override fun addOnBackPressedListener() {
//        (activity as? MainActivity)?.addOnBackPressedListener(this)
//    }
//
//    override fun startMovieDetailFragment(movie: Movie) {
//        (activity as MainActivity).myFragmentManager.createNewFragment(
//            R.id.fragment_container,
//            newInstance(
//                super.getTabPosition(),
//                (super.getFragmentFloor() + 1),
//                movie
//            ),
//            super.getTabPosition(),
//            (super.getFragmentFloor() + 1)
//        )
//    }
//
//    override fun onLoadedMovieRating(rating: Float) {
//        with(ratingBar) {
//            this.rating = rating
//            onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
//                Log.d(TAG, "OnRatingBarChangeListener(), rating=$rating, fromUser=$fromUser")
//                if (!fromUser)
//                    return@OnRatingBarChangeListener
//
//                presenter.updateMovieRating(movie.title, rating)
//            }
//        }
//    }
//
//    companion object {
//        @JvmStatic
//        fun newInstance(tabPosition: Int, fragmentFloor: Int, movie: Movie) =
//            MovieDetailFragment().apply {
//                arguments = Bundle().apply {
//                    putInt(ARG_PARAM_TAB_POSITION, tabPosition)
//                    putInt(ARG_PARAM_FRAGMENT_FLOOR, fragmentFloor)
//                    putSerializable(Constants.ARG_PARAM_MOVIE, movie)
//                }
//            }
//    }
//}
