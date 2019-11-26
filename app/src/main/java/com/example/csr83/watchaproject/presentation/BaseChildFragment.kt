package com.example.csr83.watchaproject.presentation

import android.util.Log
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.presentation.main.MainActivity

open class BaseChildFragment : BaseFragment() {

    override fun onBackPressed() {
        Log.d(javaClass.simpleName, "onBackPressed(), isCurrentView=${getCurrentTopView()}")
        if (!getCurrentTopView())
            return

        (activity as MainActivity).myFragmentManager
            .removeFragment(R.id.fragment_container, this)
    }
}