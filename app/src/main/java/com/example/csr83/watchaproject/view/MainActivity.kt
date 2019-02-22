package com.example.csr83.watchaproject.view

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.util.Log
import com.example.csr83.watchaproject.view.recommendation.MovieDetailFragment
import com.example.csr83.watchaproject.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    var movieDetailFragment : MovieDetailFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        view_pager.adapter = sectionsPagerAdapter
        view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))

        initView()
    }

    override fun onBackPressed() {
        if (movieDetailFragment != null && view_pager.currentItem == 0) {
            Log.d(TAG, "movieDetailFragment not null")

            val transaction = supportFragmentManager.beginTransaction()
            transaction.remove(movieDetailFragment!!)
            transaction.commit()

            movieDetailFragment = null
        } else {
            super.onBackPressed()
        }
    }

    private fun initView() {
        tab_layout.setupWithViewPager(view_pager)
        tab_layout.getTabAt(0)!!
            .setIcon(R.drawable.home)
            .setText(R.string.tab_title_recommendation)
        tab_layout.getTabAt(1)!!
            .setIcon(R.drawable.star)
            .setText(R.string.tab_title_evaluation)

        setTabIconTint(tab_layout.getTabAt(0)!!, android.R.color.holo_red_light)

        tab_layout.setTabTextColors(
            resources.getColor(android.R.color.black),
            resources.getColor(android.R.color.holo_red_light))

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                setTabIconTint(tab, android.R.color.holo_red_light)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                setTabIconTint(tab, android.R.color.black)
            }

            override fun onTabReselected(tab: TabLayout.Tab) { }
        })
    }

    private fun setTabIconTint(tab: TabLayout.Tab, @ColorRes color: Int) {
        if(Build.VERSION.SDK_INT < 21)
            return
        tab.icon!!.setTint(resources.getColor(color))
    }
    /**
     * MainActivity 에서 child fragment 를 참조하기 위해 fragment 생성시 singleton 레퍼런스를 저장
     */
    fun saveFragment(f: Fragment) {
        if (f is MovieDetailFragment) {
            movieDetailFragment = f
        }
    }
}
