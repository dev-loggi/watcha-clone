package com.example.csr83.watchaproject.presentation.evaluation

import android.app.Activity.RESULT_OK
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.data.remote.Movie
import com.example.csr83.watchaproject.utils.Constants
import com.example.csr83.watchaproject.utils.Utils
import com.example.csr83.watchaproject.presentation.BaseParentFragment
import com.example.csr83.watchaproject.presentation.movieDetailActivity.MovieDetailActivity
import kotlinx.android.synthetic.main.fragment_evaluation.*
import kotlinx.android.synthetic.main.loading_progressbar.*


class EvaluationFragment : BaseParentFragment(), EvaluationContract.View {

    override val mContext: Context
        get() = context!!

    private lateinit var presenter: EvaluationPresenter
    private lateinit var rvAdapter: EvaluationRvAdapter

    /**
     * ([BaseParentFragment])'s override functions
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Utils.setNoTranslucentStatusBar(activity!!.window)

        presenter = EvaluationPresenter(this)
        rvAdapter = EvaluationRvAdapter(this, presenter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_evaluation, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.onViewCreated(rvAdapter)
        initView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_CODE_CATEGORY && resultCode == RESULT_OK) {
            val category = data?.extras?.getString(SelectCategoryActivity.INTENT_KEY_CATEGORY)
            tv_category.text = category
            presenter.loadMovieAndRatings()
        }
    }

    override fun onDestroyView() {
        presenter.onDestroyView()
        super.onDestroyView()
    }

    /**
     * ([EvaluationContract.View])'s override functions
     */
    override fun initView() {
        (scrimStatusBar.layoutParams as LinearLayout.LayoutParams).height = Utils.getStatusBarHeight(context)

        with(recycler_view) {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(context)
        }

        tv_category.text = "랜덤 영화"
        btnCategory.setOnClickListener { showSelectCategoryActivity() }

        swipe_refresh_layout.setOnRefreshListener { refreshMovies() }

        (tv_header_title_number.layoutParams as LinearLayout.LayoutParams).topMargin += Utils.getStatusBarHeight(context)

        presenter.loadMovieAndRatings()
    }

    override fun setHeaderTitle(number: Int) {
        tv_header_title_number?.text = number.toString()
        tv_header_comment?.text = "${number}개 달성! 평가를 더 하시면 추천이 더 정확해져요."
    }


    override fun startMovieDetailActivity(movie: Movie, sharedElement: View) {
        if (Build.VERSION.SDK_INT >= 21) {
            startActivity(
                Intent(activity, MovieDetailActivity::class.java).putExtra(Constants.ARG_PARAM_MOVIE, movie),
                ActivityOptions.makeSceneTransitionAnimation(activity, sharedElement, sharedElement.transitionName).toBundle()
            )
        }
    }

    override fun showMovies() {
        recycler_view?.visibility = View.VISIBLE
    }

    override fun hideMovies() {
        recycler_view?.visibility = View.GONE
    }

    override fun refreshMovies() {
        presenter.loadMovieAndRatings()
        swipe_refresh_layout.isRefreshing = false
    }

    override fun showLoadingSpinner() {
        loading_spinner?.visibility = View.VISIBLE
    }

    override fun hideLoadingSpinner() {
        loading_spinner?.visibility = View.GONE
    }

    override fun showSelectCategoryActivity() {
        startActivityForResult(
            Intent(activity, SelectCategoryActivity::class.java)
                .putExtra(SelectCategoryActivity.INTENT_KEY_CATEGORY, tv_category.text.toString())
            , Constants.REQUEST_CODE_CATEGORY
        )
    }

    override fun showBottomSheetDialogFragment(movie: Movie) {
        CustomBottomSheetDialogFragment().apply {
            arguments = Bundle().also {
                it.putSerializable(Constants.ARG_PARAM_MOVIE, movie)
            }
        }.show(activity!!.supportFragmentManager, null)
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
