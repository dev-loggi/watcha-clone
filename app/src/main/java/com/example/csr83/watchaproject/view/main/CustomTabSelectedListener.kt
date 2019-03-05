package com.example.csr83.watchaproject.view.main

import android.support.design.widget.TabLayout
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.view.evaluation.EvaluationFragment
import com.example.csr83.watchaproject.view.mypage.MypageFragment
import com.example.csr83.watchaproject.view.news.NewsFragment
import com.example.csr83.watchaproject.view.recommendation.RecommendationFragment

class CustomTabSelectedListener(private val myFragmentAdapter: MyFragmentAdapter) : TabLayout.OnTabSelectedListener {
    private val TAG = javaClass.simpleName

    // 맨 처음에 Tab 포지션을 Reselect 할 때에만 사용
    private var flagOnlyFirstExecution = true

    override fun onTabReselected(tab: TabLayout.Tab?) {
        if (flagOnlyFirstExecution) {
            onTabSelected(tab)
            flagOnlyFirstExecution = false
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) { }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab == null)
            return

        val position = tab.position
        val isExistsParentFragment =
            when (position) {
                0 -> myFragmentAdapter.createNewFragment(R.id.fragment_container, RecommendationFragment.newInstance(position, 0), position, 0)
                1 -> myFragmentAdapter.createNewFragment(R.id.fragment_container, EvaluationFragment.newInstance(position, 0), position, 0)
                2 -> myFragmentAdapter.createNewFragment(R.id.fragment_container, NewsFragment.newInstance(position, 0), position, 0)
                3 -> myFragmentAdapter.createNewFragment(R.id.fragment_container, MypageFragment.newInstance(position, 0), position, 0)
                else -> true
            }

        if (!isExistsParentFragment) {
            myFragmentAdapter.replaceTopFragmentOfCurrentTab(R.id.fragment_container, position)
        }
    }
}
