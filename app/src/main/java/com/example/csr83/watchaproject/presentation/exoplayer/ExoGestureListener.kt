package com.example.csr83.watchaproject.presentation.exoplayer

import android.util.Log
import android.view.*
import kotlinx.android.synthetic.main.exo_player_surface_controller.*

class ExoGestureListener(
    private val view: ExoPlayerActivity,
    private val presenter: ExoPlayerPresenter,
    private val controller_position: Int
) : GestureDetector.SimpleOnGestureListener(),
    SurfaceControllerAnimator.ControllerAnimationListener {
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
        animator = SurfaceControllerAnimator(view, presenter)
        animator.setOnControllerAnimationListener(this)
    }

    /**
     * Interface ([GestureDetector.SimpleOnGestureListener])'s override functions
     */
    override fun onDown(e: MotionEvent?): Boolean {
        Log.d(TAG, "onDown(), e=$e")
        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        if (animator.isPlaying())
            return true

        when (controller_position) {
            POS_LEFT -> {
                scrollOffset += distanceY * (view.seekBar_brightness.max / view.seekBar_brightness.height.toFloat())

                if (!isStartScrolling) {
                    if (-1 < scrollOffset && scrollOffset < 1) {
                        return true
                    } else {
                        isStartScrolling = true
                        view.seekBar_brightness.onExoTouchEvent(MotionEvent.ACTION_DOWN, 0f)
                    }
                }
                isStartScrolling = view.seekBar_brightness.onExoTouchEvent(MotionEvent.ACTION_SCROLL, scrollOffset)
            }
            POS_RIGHT -> {
                scrollOffset += distanceY * (view.seekBar_sound.max / view.seekBar_sound.height.toFloat())
                Log.d(TAG, "onScroll() offset=$scrollOffset, distanceY=$distanceY, seekBar_sound.max=${view.seekBar_sound.max}")

                if (!isStartScrolling) {
                    if (-1 < scrollOffset && scrollOffset < 1) {
                        return true
                    } else {
                        isStartScrolling = true
                        view.seekBar_sound.onExoTouchEvent(MotionEvent.ACTION_DOWN, 0f)
                    }
                }
                isStartScrolling = view.seekBar_sound.onExoTouchEvent(MotionEvent.ACTION_SCROLL, scrollOffset)
            }
        }
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        Log.d(TAG, "onSingleTapConfirmed")
        if (animator.isPlaying())
            return true
        view.updateTopBottomBarVisible(null)
        return true
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        Log.d(TAG, "onDoubleTap, animator.isPlaying()=${animator.isPlaying()}")
        if (animator.isPlaying())
            return true
        val startPosition = presenter.getCurrentPosition()

        when (controller_position) {
            POS_LEFT -> animator.startAnimationRewind(startPosition, 10)
            POS_CENTER ->
                if (presenter.isPlaying())
                    animator.startAnimationPause()
                else
                    animator.startAnimationPlay()
            POS_RIGHT -> animator.startAnimationFfwd(startPosition, 10)
        }

        return true
    }

    fun onUp(e: MotionEvent?) {
        Log.i(TAG, "onUp")
        if (controller_position == POS_CENTER)
            return
        if (isStartScrolling) {
            when (controller_position) {
                POS_LEFT -> view.seekBar_brightness.onExoTouchEvent(MotionEvent.ACTION_UP, 0f)
                POS_RIGHT -> isStartScrolling = view.seekBar_sound.onExoTouchEvent(MotionEvent.ACTION_UP, 0f)
            }
            scrollOffset = 0f
            isStartScrolling = false
        }
    }

    /**
     * Interface ([SurfaceControllerAnimator.ControllerAnimationListener])'s override functions
     */
    override fun onEachOneAnimationEnd(startTime: Long, rewindTime: Int) {
    }
    override fun onAllAnimationEnd(startTime: Long, rewindTime: Int) {
    }
    override fun onRewind(toTime: Long) {
        presenter.seekTo(toTime)
        view.updateTimeBar(toTime)
    }
    override fun onFastForward(toTime: Long) {
        presenter.seekTo(toTime)
        view.updateTimeBar(toTime)
    }
    override fun onPlay() {
        presenter.play()
    }
    override fun onPause() {
        presenter.pause()
    }
}