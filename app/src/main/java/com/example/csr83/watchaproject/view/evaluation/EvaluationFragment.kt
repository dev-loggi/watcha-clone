package com.example.csr83.watchaproject.view.evaluation

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.model.Movie
import com.example.csr83.watchaproject.utils.Utils
import com.example.csr83.watchaproject.view.base.BaseParentFragment
import com.example.csr83.watchaproject.view.evaluation.adapter.EvaluationRvAdapter
import com.example.csr83.watchaproject.view.main.MainActivity
import com.example.csr83.watchaproject.view.recommendation.MovieDetailFragment
import kotlinx.android.synthetic.main.fragment_evaluation.*


class EvaluationFragment : BaseParentFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setNoTranslucentStatusBar(activity?.window)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_evaluation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recycler_view.adapter = EvaluationRvAdapter(this)
        recycler_view.layoutManager = LinearLayoutManager(context)

        tv_category.text = "랜덤 영화"

        swipe_refresh_layout.setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = false
        }

        (tv_header_title_number.layoutParams as LinearLayout.LayoutParams).topMargin += Utils.getStatusBarHeight(context)
    }

    fun startMovieDetailFragment(movie: Movie) {
        (activity as MainActivity).myFragmentAdapter!!.createNewFragment(
            R.id.fragment_container,
            MovieDetailFragment.newInstance(super.getTabPosition(), super.getFragmentFloor() + 1, movie),
            super.getTabPosition(),
            super.getFragmentFloor() + 1
        )
    }

    fun setHeaderTitle(number: Int) {
        tv_header_title_number.text = number.toString()
        tv_header_comment.text = "${number}개 달성! 평가를 더 하시면 추천이 더 정확해져요."
    }

    companion object {
        @JvmStatic
        fun newInstance(tabPosition: Int, fragmentFloor: Int) =
            EvaluationFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM_TAB_POSITION, tabPosition)
                    putInt(ARG_PARAM_FRAGMENT_FLOOR, fragmentFloor)
                }
            }
    }
}
