package com.example.csr83.watchaproject.legacy

import android.support.design.widget.TabLayout
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.presentation.evaluation.EvaluationFragment
import com.example.csr83.watchaproject.presentation.main.MyFragmentManager
import com.example.csr83.watchaproject.presentation.mypage.MypageFragment
import com.example.csr83.watchaproject.presentation.news.NewsFragment
import com.example.csr83.watchaproject.presentation.recommendation.RecommendationFragment

class CustomTabSelectedListener(private val myFragmentManager: MyFragmentManager) : TabLayout.OnTabSelectedListener {
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
                0 -> myFragmentManager.createNewFragment(R.id.fragment_container, RecommendationFragment.newInstance(position, 0), position, 0)
                1 -> myFragmentManager.createNewFragment(R.id.fragment_container, EvaluationFragment.newInstance(position, 0), position, 0)
                2 -> myFragmentManager.createNewFragment(R.id.fragment_container, NewsFragment.newInstance(position, 0), position, 0)
                3 -> myFragmentManager.createNewFragment(R.id.fragment_container, MypageFragment.newInstance(position, 0), position, 0)
                else -> true
            }

        if (!isExistsParentFragment) {
            myFragmentManager.replaceTopFragmentOfCurrentTab(R.id.fragment_container, position)
        }
    }
}
