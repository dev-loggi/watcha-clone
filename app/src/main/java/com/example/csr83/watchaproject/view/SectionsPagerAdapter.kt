package com.example.csr83.watchaproject.view

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.csr83.watchaproject.view.evaluation.EvaluationFragment
import com.example.csr83.watchaproject.view.recommendation.RecommendationFragment

class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(p0: Int): Fragment {
        return when(p0 + 1) {
            1 -> RecommendationFragment.newInstance("", "")
            2 -> EvaluationFragment.newInstance("", "")
            else -> RecommendationFragment.newInstance("", "")
        }
    }

    override fun getCount(): Int {
        return 2
    }
}