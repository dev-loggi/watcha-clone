package com.example.csr83.watchaproject.presentation.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.data.remote.MovieDB
import com.example.csr83.watchaproject.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName

    lateinit var myFragmentManager: MyFragmentManager
    private val listBackPressedListener : ArrayList<OnBackPressedListener> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        with (MovieDB.getInstance(this).movieDAO()) {
            deleteAll()
            insert(Utils.loadData(this@MainActivity))
        }

        initView()
    }

    override fun onBackPressed() {
        for (listener in listBackPressedListener) {
            listener.onBackPressed()
        }
    }

    private fun initView() {
        myFragmentManager = MyFragmentManager(supportFragmentManager)
        val bottomNavigationListener = OnBottomNavigationListener(this, myFragmentManager, tabRecommendation, tabEvaluation, tabNews, tabMyPage)
        bottomNavigationListener.onTabSelected(0)
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
