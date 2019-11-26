package com.example.csr83.watchaproject.presentation

open class BaseParentFragment : BaseFragment() {

    override fun onBackPressed() {
        if (!getCurrentTopView())
            return

        activity!!.finish()
    }
}