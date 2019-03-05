package com.example.csr83.watchaproject.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.util.Log
import com.example.csr83.watchaproject.view.recommendation.MovieDetailFragment
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.utils.VerticalFlipTransformation
import com.example.csr83.watchaproject.view.evaluation.EvaluationFragment
import com.example.csr83.watchaproject.view.mypage.MypageFragment
import com.example.csr83.watchaproject.view.news.NewsFragment
import com.example.csr83.watchaproject.view.recommendation.RecommendationFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    var movieDetailFragment : MovieDetailFragment? = null
    var sectionsPagerAdapter : SectionsPagerAdapter? = null

    val listBackPressedListener : ArrayList<OnBackPressedListener> = arrayListOf()
//    var backPressedListener : OnBackPressedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        sectionsPagerAdapter?.addFragment(RecommendationFragment.newInstance("", ""))
        sectionsPagerAdapter?.addFragment(EvaluationFragment.newInstance("", ""))
        sectionsPagerAdapter!!.addFragment(NewsFragment.newInstance("", ""))
        sectionsPagerAdapter!!.addFragment(MypageFragment.newInstance("", ""))

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

//        for (listener in listBackPressedListener) {
//            if ()
//            listener.onBackPressed()
//        }
    }

    private fun initView() {
        tab_layout.setupWithViewPager(view_pager)
        tab_layout.getTabAt(0)!!
            .setIcon(R.drawable.home)
            .setText(R.string.tab_title_recommendation)
            .select()
        tab_layout.getTabAt(1)!!
            .setIcon(R.drawable.star)
            .setText(R.string.tab_title_evaluation)
        tab_layout.getTabAt(2)!!
            .setIcon(R.drawable.star)
            .setText(R.string.tab_title_news)
        tab_layout.getTabAt(3)!!
            .setIcon(R.drawable.profile)
            .setText(R.string.tab_title_mypage)

        setTabIconTintList(tab_layout)
    }

    private fun setTabIconTintList(tabLayout: TabLayout) {
        if(Build.VERSION.SDK_INT < 21)
            return

        for (i in 0 until tabLayout.tabCount) {
            tabLayout.getTabAt(i)!!.icon!!.setTintList(
                ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_selected),
                        intArrayOf(-android.R.attr.state_selected)
                    ),
                    intArrayOf(
                        Color.RED,
                        Color.BLACK
                    )
                )
            )
        }
    }

    /**
     * MainActivity 에서 child fragment 를 참조하기 위해 fragment 생성시 singleton 레퍼런스를 저장
     */
    fun saveFragment(f: Fragment) {
        if (f is MovieDetailFragment) {
            movieDetailFragment = f
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
}
