package com.example.csr83.watchaproject.view.recommendation

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.example.csr83.watchaproject.view.recommendation.adapter.RecommendationRvAdapter
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.model.Movie
import com.example.csr83.watchaproject.utils.Utils
import com.example.csr83.watchaproject.view.base.BaseParentFragment
import com.example.csr83.watchaproject.view.main.MainActivity
import com.example.csr83.watchaproject.view.movie_detail.MovieDetailFragment
import kotlinx.android.synthetic.main.fragment_recommendation2.*

class RecommendationFragment : BaseParentFragment() {

    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setTranslucentStatusBar(activity?.window)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recommendation2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.adapter = RecommendationRvAdapter(this)
        recycler_view.layoutManager = LinearLayoutManager(context)

        swipe_refresh_layout.setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = false
        }

        search_layout.setOnClickListener {  }
        search_layout2.setOnClickListener {  }
        frame_layout_movie.setOnClickListener {  }
        frame_layout_tv.setOnClickListener {  }
        frame_layout_book.setOnClickListener {  }

        setScrollingAnimation()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



    }

    fun startMovieDetailFragment(movie: Movie) {
        (activity as MainActivity).myFragmentAdapter!!.createNewFragment(
            R.id.fragment_container,
            MovieDetailFragment.newInstance(super.getTabPosition(), super.getFragmentFloor() + 1, movie),
            super.getTabPosition(),
            super.getFragmentFloor() + 1
        )
    }

    private fun setScrollingAnimation() {
        // statusbar 의 height 측정
        val statusBarHeight = Utils.getStatusBarHeight(context)

        // scrim_status_bar 의 height 설정
        (scrim_status_bar.layoutParams as LinearLayout.LayoutParams).height = statusBarHeight

        // 스크롤뷰 리스너
        nested_scroll_view.setOnScrollChangeListener { view: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            Log.d(TAG, "setOnScrollChangeListener(), scrollX_"+scrollX+"_scrollY_"+scrollY+"_oldScrollX_"+oldScrollX+"_oldScrollY_"+oldScrollY)

            // WATCHA 로고 움직이기
//            (logo_watcha.layoutParams as ConstraintLayout.LayoutParams).topMargin = scrollY / 2
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
