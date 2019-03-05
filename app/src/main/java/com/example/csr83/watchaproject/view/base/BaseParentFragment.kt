package com.example.csr83.watchaproject.view.base

open class BaseParentFragment : BaseFragment() {

    override fun onBackPressed() {
        if (!getCurrentTopView())
            return

        activity!!.finish()
    }
}