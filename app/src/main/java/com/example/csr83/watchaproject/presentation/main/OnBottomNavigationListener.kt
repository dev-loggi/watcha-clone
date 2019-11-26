package com.example.csr83.watchaproject.presentation.main

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.presentation.evaluation.EvaluationFragment
import com.example.csr83.watchaproject.presentation.mypage.MypageFragment
import com.example.csr83.watchaproject.presentation.news.NewsFragment
import com.example.csr83.watchaproject.presentation.recommendation.RecommendationFragment

class OnBottomNavigationListener(
    private val context: Context,
    private val fragmentManager: MyFragmentManager,
    vararg tabs: LinearLayout
) {
    private val TAG = javaClass.simpleName

    private val listTab = arrayListOf<Tab>()
    fun ArrayList<Tab>.버튼클릭(position: Int) {
        for (i in this.indices) {
            if (i == position) {
                this[i].setFocus(true)
            } else {
                this[i].setFocus(false)
            }
        }
    }

    init {
        for (position in tabs.indices) {
            listTab.add(
                Tab(tabs[position].apply {
                    setOnClickListener {
                        onTabSelected(position)
                    }
                }, position))
        }
    }

    fun onTabSelected(position: Int) {
        listTab.버튼클릭(position)

        val isExistsParentFragment =
            when (position) {
                0 -> fragmentManager.createNewFragment(R.id.fragment_container, RecommendationFragment.newInstance(position, 0), position, 0)
                1 -> fragmentManager.createNewFragment(R.id.fragment_container, EvaluationFragment.newInstance(position, 0), position, 0)
                2 -> fragmentManager.createNewFragment(R.id.fragment_container, NewsFragment.newInstance(position, 0), position, 0)
                3 -> fragmentManager.createNewFragment(R.id.fragment_container, MypageFragment.newInstance(position, 0), position, 0)
                else -> true
            }

        if (!isExistsParentFragment) {
            fragmentManager.replaceTopFragmentOfCurrentTab(R.id.fragment_container, position)
        }
    }


    inner class Tab(val tab: LinearLayout, val position: Int) {
        private val icon = tab.getChildAt(0)
        private val text = tab.getChildAt(1)

        fun setFocus(isFocus: Boolean) {
            if (icon is ImageView && text is TextView) {
                icon.setColorFilter(context.resources.getColor(
                    if (isFocus) R.color.red3
                    else R.color.gray1))
                text.setTextColor(context.resources.getColor(
                    if (isFocus) R.color.red3
                    else R.color.gray1))
            }
        }
    }
}