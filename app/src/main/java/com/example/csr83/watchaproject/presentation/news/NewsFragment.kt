package com.example.csr83.watchaproject.presentation.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.presentation.BaseParentFragment


class NewsFragment : BaseParentFragment() {
    private val TAG = javaClass.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_news, container, false)

    companion object {
        @JvmStatic
        fun newInstance(tabPosition: Int, fragmentFloor: Int) =
            NewsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM_TAB_POSITION, tabPosition)
                    putInt(ARG_PARAM_FRAGMENT_FLOOR, fragmentFloor)
                }
            }
    }
}
