package com.example.csr83.watchaproject.view.main

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.csr83.watchaproject.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = javaClass.simpleName

    var myFragmentAdapter: MyFragmentAdapter? = null
    private val listBackPressedListener : ArrayList<OnBackPressedListener> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    override fun onBackPressed() {
        for (listener in listBackPressedListener) {
            listener.onBackPressed()
        }
    }

    private fun initView() {
        myFragmentAdapter = MyFragmentAdapter(supportFragmentManager)

        tab_layout.tabIconTint = getTabIconTintList()
        tab_layout.addOnTabSelectedListener(CustomTabSelectedListener(myFragmentAdapter!!))
        tab_layout.getTabAt(0)!!.select()
    }

    private fun getTabIconTintList(): ColorStateList? {
        return when (Build.VERSION.SDK_INT < 21) {
            true -> null
            false ->
                ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_selected),
                        intArrayOf(-android.R.attr.state_selected)
                    ),
                    intArrayOf(
                        Color.RED,
                        Color.BLACK)
                )
        }
    }

    interface OnBackPressedListener {
        fun onBackPressed()
    }

    fun addOnBackPressedListener(listener: OnBackPressedListener) {
        for (item in listBackPressedListener) {
            if (item == listener) return
        }
        listBackPressedListener.add(listener)
    }

    fun removeOnBackPressedListener(listener: OnBackPressedListener) {
        listBackPressedListener.remove(listener)
    }
}
