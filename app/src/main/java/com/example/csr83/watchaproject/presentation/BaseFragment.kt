package com.example.csr83.watchaproject.presentation

import android.os.Bundle
import android.support.v4.app.Fragment
import com.example.csr83.watchaproject.presentation.main.MainActivity

abstract class BaseFragment : Fragment(), MainActivity.OnBackPressedListener {
    private val TAG = javaClass.simpleName

    private var isCurrentTopView = false    // 현재 자신(fragment)이 화면에 보여지고 있는지
    private var tabPosition = 0
    private var fragmentFloor = 0
    private var scrollY = 0

    fun getCurrentTopView() = isCurrentTopView
    fun getTabPosition() = tabPosition
    fun getFragmentFloor() = fragmentFloor

    companion object {
        const val BUNDLE_KEY_SCROLL_Y = "scroll_y"
        const val ARG_PARAM_TAB_POSITION = "tab_position"
        const val ARG_PARAM_FRAGMENT_FLOOR = "fragment_floor"
    }

    abstract override fun onBackPressed()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tabPosition = it.getInt(ARG_PARAM_TAB_POSITION)
            fragmentFloor = it.getInt(ARG_PARAM_FRAGMENT_FLOOR)
        }
        (activity as MainActivity).addOnBackPressedListener(this)
//        (activity as MainActivity).addOnBackPressedListener(object : MainActivity.OnBackPressedListener {
//            override fun onBackPressed() {
//
//            }
//        })
//        Utils.setTranslucentStatusBar(activity!!.window)
    }

    override fun onResume() {
        super.onResume()
        isCurrentTopView = true
    }

    override fun onPause() {
        super.onPause()
        isCurrentTopView = false
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).removeOnBackPressedListener(this)
    }


}