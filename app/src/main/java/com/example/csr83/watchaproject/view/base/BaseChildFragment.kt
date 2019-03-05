package com.example.csr83.watchaproject.view.base

import android.util.Log
import android.view.Window
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.view.main.MainActivity

open class BaseChildFragment : BaseFragment() {

    override fun onBackPressed() {
        Log.d(javaClass.simpleName, "onBackPressed(), isCurrentView=${getCurrentTopView()}")
        if (!getCurrentTopView())
            return

        (activity as MainActivity).myFragmentAdapter!!
            .removeFragment(R.id.fragment_container, this)
    }
}