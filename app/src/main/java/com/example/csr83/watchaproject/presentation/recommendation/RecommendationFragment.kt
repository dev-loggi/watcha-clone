package com.example.csr83.watchaproject.presentation.recommendation

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.data.remote.Movie
import com.example.csr83.watchaproject.utils.Constants
import com.example.csr83.watchaproject.utils.Utils
import com.example.csr83.watchaproject.presentation.BaseParentFragment
import com.example.csr83.watchaproject.presentation.main.MainActivity
import com.example.csr83.watchaproject.presentation.movieDetailActivity.MovieDetailActivity
import com.example.csr83.watchaproject.presentation.search.SearchFragment
import kotlinx.android.synthetic.main.fragment_recommendation.*
import kotlinx.android.synthetic.main.loading_progressbar.*

class RecommendationFragment : BaseParentFragment(), RecommendationContract.View {
    private val TAG = javaClass.simpleName

    override val mContext: Context
        get() = context!!

    private lateinit var presenter: RecommendationContract.Presenter
    private lateinit var rvAdapter: RecommendationRvAdapter

    /**
     * ([BaseParentFragment]) override function.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setTranslucentStatusBar(activity!!.window)

        rvAdapter = RecommendationRvAdapter(this)
        presenter = RecommendationPresenter(this, rvAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_recommendation, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(recycler_view) {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(context)

            if (activity != null) {
                this@RecommendationFragment.presenter.loadData()
            }
        }

        swipe_refresh_layout.setOnRefreshListener {
            presenter.loadData()
            swipe_refresh_layout.isRefreshing = false
        }

        search_layout.setOnClickListener { startSearchFragment() }
        search_layout2.setOnClickListener { startSearchFragment() }
        frame_layout_movie.setOnClickListener { startStartMovieFragment() }
        frame_layout_tv.setOnClickListener { startStartTVFragment() }
        frame_layout_book.setOnClickListener { startStartBookFragment() }

        setHeaderAnimation()
    }

    /**
     * ([RecommendationContract.View]) override function.
     */
    override fun setHeaderAnimation() {
        // statusbar 의 height 측정
        val statusBarHeight = Utils.getStatusBarHeight(context)

        // scrim_status_bar 의 height 설정
        (scrim_status_bar.layoutParams as LinearLayout.LayoutParams).height = statusBarHeight

        // 스크롤뷰 리스너
        nested_scroll_view.setOnScrollChangeListener { view: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            Log.d(TAG, "setOnScrollChangeListener(), scrollX_"+scrollX+"_scrollY_"+scrollY+"_oldScrollX_"+oldScrollX+"_oldScrollY_"+oldScrollY)

            // WATCHA 로고 움직이기
            val params = logo_watcha.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = scrollY / 2
            logo_watcha.layoutParams = params

            // 상단바 show & hide
            if (search_layout.y < statusBarHeight + scrollY) {
                toolbar_layout.visibility = View.VISIBLE
            } else {
                toolbar_layout.visibility = View.GONE
            }
        }
    }

    override fun showMovieRecyclerView() {
        recycler_view?.visibility = View.VISIBLE
    }

    override fun hideMovieRecyclerView() {
        recycler_view?.visibility = View.GONE
    }

    override fun showLoadingSpinner() {
        loading_spinner?.visibility = View.VISIBLE
    }

    override fun hideLoadingSpinner() {
        loading_spinner?.visibility = View.GONE
    }

    override fun startMovieDetailActivity(movie: Movie, shareElement: View) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity!!.startActivity(
                Intent(context, MovieDetailActivity::class.java).putExtra(Constants.ARG_PARAM_MOVIE, movie),
                ActivityOptions.makeSceneTransitionAnimation(activity, shareElement, shareElement.transitionName).toBundle()
            )
        }
    }

    override fun startSearchFragment() {
        (activity as MainActivity).myFragmentManager.createNewFragment(
            R.id.fragment_container,
            SearchFragment.newInstance(super.getTabPosition(), super.getFragmentFloor() + 1),
            super.getTabPosition(),
            (super.getFragmentFloor() + 1)
        )
    }

    override fun startStartMovieFragment() { }
    override fun startStartTVFragment() { }
    override fun startStartBookFragment() { }

    companion object {
        @JvmStatic
        fun newInstance(tabPosition: Int, fragmentFloor: Int) =
            RecommendationFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM_TAB_POSITION, tabPosition)
                    putInt(ARG_PARAM_FRAGMENT_FLOOR, fragmentFloor)
                }
            }
    }
}
