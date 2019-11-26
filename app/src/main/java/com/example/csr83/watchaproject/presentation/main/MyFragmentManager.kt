package com.example.csr83.watchaproject.presentation.main

import android.content.Context
import android.os.Build
import android.support.annotation.IdRes
import android.support.v4.app.FragmentManager
import android.transition.Fade
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.util.Log
import android.view.View
import com.example.csr83.watchaproject.presentation.BaseFragment

class MyFragmentManager(private val fragmentManager: FragmentManager) {
    private val TAG = javaClass.simpleName
    private val MOVE_DEFAULT_TIME: Long = 1000
    private val FADE_DEFAULT_TIME: Long = 300

    private val listFragments = arrayListOf<BaseFragment>()

    fun createNewFragment(@IdRes id: Int, fragment: BaseFragment, position: Int, floor: Int): Boolean {
        if (getFragment(position, floor) != null)
            return false

        replaceFragmentByFM(id, fragment)

        listFragments.add(fragment)
        return true
    }
    fun createNewFragment(context: Context, @IdRes id: Int, curFragment: BaseFragment, newFragment: BaseFragment, position: Int, floor: Int, sharedElement: View): Boolean {
        if (getFragment(position, floor) != null)
            return false

        replaceFragmentByFM(context, id, curFragment, newFragment, sharedElement)

        listFragments.add(newFragment)
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
    private fun replaceFragmentByFM(context: Context, @IdRes id: Int, curFragment: BaseFragment, newFragment: BaseFragment, sharedElement: View) {
        if (Build.VERSION.SDK_INT >= 21) {
//            curFragment.exitTransition = Fade().apply {
//                duration = FADE_DEFAULT_TIME
//            }

            newFragment.sharedElementEnterTransition = TransitionSet().apply {
                addTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.move))
                duration = MOVE_DEFAULT_TIME
                startDelay = FADE_DEFAULT_TIME
            }
            newFragment.enterTransition = Fade().apply {
                startDelay = MOVE_DEFAULT_TIME + FADE_DEFAULT_TIME
                duration = FADE_DEFAULT_TIME
            }
            Log.d(TAG, "sharedElement.transitionName=${sharedElement.transitionName}")
            fragmentManager
                .beginTransaction()
                .addSharedElement(sharedElement, sharedElement.transitionName)
                .replace(id, newFragment)
                .commit()
        }


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