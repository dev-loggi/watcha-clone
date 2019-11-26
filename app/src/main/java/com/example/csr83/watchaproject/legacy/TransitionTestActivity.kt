package com.example.csr83.watchaproject.legacy

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.transition.Fade
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.view.View
import com.example.csr83.watchaproject.R

class TransitionTestActivity : AppCompatActivity() {
    private val MOVE_DEFAULT_TIME: Long = 1000
    private val FADE_DEFAULT_TIME: Long = 300


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition_test)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, TransitionTestFragment2())
            .commit()
    }

    fun changeFragment(curFragment: Fragment, newFragment: Fragment, sharedElement: View) {
        if (Build.VERSION.SDK_INT < 21)
            return

//        curFragment.exitTransition = Fade().apply {
//            duration = FADE_DEFAULT_TIME
//        }

        newFragment.sharedElementEnterTransition = TransitionSet().apply {
            addTransition(
                TransitionInflater
                    .from(this@TransitionTestActivity)
                    .inflateTransition(android.R.transition.move)
            )
            duration = MOVE_DEFAULT_TIME
            startDelay = FADE_DEFAULT_TIME
        }
        newFragment.enterTransition = Fade().apply {
            startDelay = MOVE_DEFAULT_TIME + FADE_DEFAULT_TIME
            duration = FADE_DEFAULT_TIME
        }

        supportFragmentManager.beginTransaction()
            .addSharedElement(sharedElement, sharedElement.transitionName)
            .replace(R.id.frameLayout, newFragment, null)
            .commit()
    }
}