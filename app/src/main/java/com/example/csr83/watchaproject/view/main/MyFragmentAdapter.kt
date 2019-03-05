package com.example.csr83.watchaproject.view.main

import android.support.annotation.IdRes
import android.support.v4.app.FragmentManager
import android.util.Log
import com.example.csr83.watchaproject.view.base.BaseFragment

class MyFragmentAdapter(private val fragmentManager: FragmentManager) {
    private val TAG = javaClass.simpleName

    val listFragments = arrayListOf<BaseFragment>()

    fun createNewFragment(@IdRes id: Int, fragment: BaseFragment, position: Int, floor: Int): Boolean {
        if (getFragment(position, floor) != null)
            return false

        replaceFragmentByFM(id, fragment)

        listFragments.add(fragment)
        return true
    }

    fun replaceTopFragmentOfCurrentTab(@IdRes id: Int, tabPosition: Int) {
        replaceFragmentByFM(id, getTopFragmentOfCurrentTab(tabPosition)!!)
    }

    fun removeFragment(@IdRes id: Int, fragment: BaseFragment) {
        val downStairsFragment = getFragment(fragment.getTabPosition(), fragment.getFragmentFloor() - 1)
            ?: return

        replaceFragmentByFM(id, downStairsFragment)

        listFragments.remove(fragment)
    }

    private fun replaceFragmentByFM(@IdRes id: Int, fragment: BaseFragment) {
        fragmentManager
            .beginTransaction()
            .replace(id, fragment)
            .commit()
    }

    private fun getFragment(position: Int, floor: Int): BaseFragment? {
        for (f in listFragments) {
            if (f.getTabPosition() == position && f.getFragmentFloor() == floor)
                return f
        }
        return null
    }

    private fun getTopFragmentOfCurrentTab(p: Int): BaseFragment? {
        var topFragment: BaseFragment? = null
        for (f in listFragments) {
            if (p != f.getTabPosition())
                continue
            if (topFragment == null)
                topFragment = f
            if (f.getFragmentFloor() > topFragment.getFragmentFloor())
                topFragment = f
        }
        return topFragment
    }
}