package com.example.csr83.watchaproject.view.recommendation

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import com.example.csr83.watchaproject.view.recommendation.adapter.RecommendationRvAdapter
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.model.Movie
import com.example.csr83.watchaproject.utils.Utils
import kotlinx.android.synthetic.main.fragment_recommendation.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RecommendationFragment : Fragment() {

    val TAG = "RecommendationFragment"

    private var param1: String? = null
    private var param2: String? = null

    private var statusBarHeight : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recommendation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recycler_view.adapter = RecommendationRvAdapter(this)
        recycler_view.layoutManager = LinearLayoutManager(context)

        swipe_refresh_layout.setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = false
        }

        setScrollingAnimation()
    }

    fun startMovieDetailFragment(movie: Movie) {
        val fragment = MovieDetailFragment.newInstance(movie)

        val childFragmentTransaction = childFragmentManager.beginTransaction()
        childFragmentTransaction.add(R.id.container, fragment)
        childFragmentTransaction.addToBackStack(null)
        childFragmentTransaction.commit()
    }

    private fun setScrollingAnimation() {
        // statusbar 의 height 측정
        statusBarHeight = Utils.getStatusBarHeight(context)

        // fake_status_bar 의 height 설정
        val layoutParams = fake_status_bar.layoutParams as LinearLayout.LayoutParams
        layoutParams.height = statusBarHeight
        fake_status_bar.layoutParams = layoutParams

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
            Log.d(TAG, "logoWatchaParams!!.topMargin=${params.topMargin}, search_layout.y=${search_layout.y}, statusBarHeight=$statusBarHeight")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecommendationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
