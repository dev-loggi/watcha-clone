package com.example.csr83.watchaproject.view.exoplayer

import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.utils.Utils
import kotlinx.android.synthetic.main.exo_player_surface_controller.*
import java.lang.ref.WeakReference

class ExoGestureListener(private val activity: ExoPlayerActivity, private val controller_position: Int)
    : GestureDetector.SimpleOnGestureListener(), SurfaceControllerAnimator.ControllerAnimationListener {
    private val TAG = javaClass.simpleName

    companion object {
        val POS_LEFT = -1
        val POS_CENTER = 0
        val POS_RIGHT = 1
    }
    private val animator: SurfaceControllerAnimator

    private var isStartScrolling = false
    private var scrollOffset = 0f
    private var isStartingSurfaceController = false

    init {
        animator = SurfaceControllerAnimator(activity)
        animator.setOnControllerAnimationListener(this)
    }

    /**
     * Interface GestureDetector.SimpleOnGestureListener
     */
    override fun onDown(e: MotionEvent?): Boolean {
        Log.w(TAG, "onDown(), e=$e")
        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        if (animator.isPlaying())
            return true

        when (controller_position) {
            POS_LEFT -> {
                scrollOffset += distanceY * (activity.seekBar_brightness.max / activity.seekBar_brightness.height.toFloat())

                if (!isStartScrolling) {
                    if (-1 < scrollOffset && scrollOffset < 1) {
                        return true
                    } else {
                        isStartScrolling = true
                        activity.seekBar_brightness.onExoTouchEvent(MotionEvent.ACTION_DOWN, 0f)
                    }
                }
                isStartScrolling = activity.seekBar_brightness.onExoTouchEvent(MotionEvent.ACTION_SCROLL, scrollOffset)
            }
            POS_RIGHT -> {
                scrollOffset += distanceY * (activity.seekBar_sound.max / activity.seekBar_sound.height.toFloat())
                Log.d(TAG, "onScroll() offset=$scrollOffset, distanceY=$distanceY, seekBar_sound.max=${activity.seekBar_sound.max}")

                if (!isStartScrolling) {
                    if (-1 < scrollOffset && scrollOffset < 1) {
                        return true
                    } else {
                        isStartScrolling = true
                        activity.seekBar_sound.onExoTouchEvent(MotionEvent.ACTION_DOWN, 0f)
                    }
                }
                isStartScrolling = activity.seekBar_sound.onExoTouchEvent(MotionEvent.ACTION_SCROLL, scrollOffset)
            }
        }
        return true
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        Log.w(TAG, "onSingleTapUp")
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        Log.w(TAG, "onSingleTapConfirmed")
        if (animator.isPlaying())
            return true
        activity.updateTopBottomBarVisible()
        return true
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        Log.w(TAG, "onDoubleTap, animator.isPlaying()=${animator.isPlaying()}")
        if (animator.isPlaying())
            return true
        val startPosition = activity.getPlayer().getCurrentPosition()
            ?: return true

        when (controller_position) {
            POS_LEFT -> animator.startAnimationRewind(startPosition, 10)
            POS_CENTER ->
                if (activity.getPlayer().isPlaying())
                    animator.startAnimationPause()
                else
                    animator.startAnimationPlay()
            POS_RIGHT -> animator.startAnimationFfwd(startPosition, 10)
        }

        return true
    }

    /**
     * Interface SurfaceControllerAnimator.ControllerAnimationListener
     */
    override fun onEachOneAnimationEnd(startTime: Long, rewindTime: Int) {
        Log.i(TAG, "onEachOneAnimationEnd()")
    }
    override fun onAllAnimationEnd(startTime: Long, rewindTime: Int) {
        Log.i(TAG, "onAllAnimationEnd()")
    }
    override fun onRewind(toTime: Long) {
        Log.i(TAG, "onRewind()")
        activity.getPlayer().seekTo(toTime)
        activity.updateTimeBar(toTime)
    }
    override fun onFastForward(toTime: Long) {
        Log.i(TAG, "onFastForward()")
        activity.getPlayer().seekTo(toTime)
        activity.updateTimeBar(toTime)
    }
    override fun onPlay() {
        activity.getPlayer().play()
        activity.updatePlayPauseButton()
    }
    override fun onPause() {
        activity.getPlayer().pause()
        activity.updatePlayPauseButton()
    }

    fun onUp(e: MotionEvent?) {
        Log.i(TAG, "onUp")
        if (controller_position == POS_CENTER)
            return
        if (isStartScrolling) {
            when (controller_position) {
                POS_LEFT -> activity.seekBar_brightness.onExoTouchEvent(MotionEvent.ACTION_UP, 0f)
                POS_RIGHT -> isStartScrolling = activity.seekBar_sound.onExoTouchEvent(MotionEvent.ACTION_UP, 0f)
            }
            scrollOffset = 0f
            isStartScrolling = false
        }
    }


}