package com.example.csr83.watchaproject.presentation.exoplayer

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.utils.Utils
import kotlinx.android.synthetic.main.exo_player_surface_controller.*

class SurfaceControllerAnimator(
    private val view: ExoPlayerActivity,
    private val presenter: ExoPlayerPresenter
) {
    private val TAG = javaClass.simpleName

    private var isPlaying = false

    private val containerRewind: FrameLayout = view.controller_left
    private val ivSmallRewinds = arrayListOf<ImageView>()
    private val tvRewindTime: TextView

    private val containerFfwd: FrameLayout
    private val ivSmallFfwds = arrayListOf<ImageView>()
    private val tvFfwdTime: TextView

    private val containerPlayPause: FrameLayout
    private val ivPlay: ImageView
    private val ivPause: ImageView

    private val listAnimIconShow = arrayListOf<AlphaAnimation>()
    private val listAnimIconHide = arrayListOf<AlphaAnimation>()

    private var listener: ControllerAnimationListener? = null

    private var countCurrentExistBgView = 0

    interface ControllerAnimationListener {
        fun onRewind(toTime: Long)
        fun onPlay()
        fun onPause()
        fun onFastForward(toTime: Long)
        fun onEachOneAnimationEnd(startTime: Long, rewindTime: Int)
        fun onAllAnimationEnd(startTime: Long, rewindTime: Int)
    }

    init {
        ivSmallRewinds.add(view.iv_small_rewind_1)
        ivSmallRewinds.add(view.iv_small_rewind_2)
        ivSmallRewinds.add(view.iv_small_rewind_3)
        tvRewindTime = view.tv_rewind_time

        containerFfwd = view.controller_right
        ivSmallFfwds.add(view.iv_small_ffwd_1)
        ivSmallFfwds.add(view.iv_small_ffwd_2)
        ivSmallFfwds.add(view.iv_small_ffwd_3)
        tvFfwdTime = view.tv_ffwd_time

        containerPlayPause = view.controller_center
        ivPlay = view.iv_play_controller
        ivPause = view.iv_pause_controller

        for (i in 0 until 3) {
            val anim = AlphaAnimation(0f, 1f)
            anim.duration = 100L
            listAnimIconShow.add(anim)
        }

        for (i in 0 until 3) {
            val anim = AlphaAnimation(1f, 0f)
            anim.duration = 100L
            anim.startOffset = i * 100L
            listAnimIconHide.add(anim)
        }
    }

    fun isPlaying() = isPlaying
    fun setOnControllerAnimationListener(listener: ControllerAnimationListener) { this.listener = listener }

    fun startAnimationRewind(startTime: Long, rewindTime: Int) {
        if (0 > startTime - (rewindTime - 10) * 1000)
            return

        isPlaying = true
        listener?.onRewind(startTime - (rewindTime * 1000L))

        // Small icon animation
        for (i in 0 until 3) {
            val icon = ivSmallRewinds[i]
            icon.visibility = View.VISIBLE
            listAnimIconShow[i].startOffset = (2 - i) * 100L
            icon.startAnimation(listAnimIconShow[i])
        }

        // TextView animation
        tvRewindTime.visibility = View.VISIBLE
        tvRewindTime.text = Utils.convertTimeFormat2(rewindTime, 1)
        val animFadeIn = AlphaAnimation(0f, 1f)
        animFadeIn.duration = 200
        if (rewindTime == 10) { // 처음 시작때만
            tvRewindTime.startAnimation(animFadeIn)
        }

        var bgView: View? = createBackgroundView(startTime, rewindTime, true)
        val animBgShow = AnimationUtils.loadAnimation(view, R.anim.exoplayer_appear_controller)
        val animBgHide = AnimationUtils.loadAnimation(view, R.anim.exoplayer_disappear_controller)
        animBgShow.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                Log.d(TAG, "animBgShow.onAnimationEnd(), childCount=$countCurrentExistBgView")
                bgView?.startAnimation(animBgHide)
                if (countCurrentExistBgView == 1) {
                    tvRewindTime.startAnimation(animBgHide)
                    for (icon in ivSmallRewinds) {
                        icon.startAnimation(animBgHide)
                    }
                }
            }
        })
        animBgHide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                containerRewind.removeView(bgView)
                countCurrentExistBgView--
                bgView = null
                Log.d(TAG, "animBgHide.onAnimationEnd(), childCount=$countCurrentExistBgView, rewindTime=$rewindTime")
                if (countCurrentExistBgView == 0) { // 마지막 animation
                    tvRewindTime.visibility = View.GONE
                    for (icon in ivSmallRewinds) {
                        icon.visibility = View.GONE
                    }
                    listener?.onAllAnimationEnd(startTime, rewindTime)
                    isPlaying = false
                } else { // 진행중
                    listener?.onEachOneAnimationEnd(startTime, rewindTime)
                }
            }
        })
        containerRewind.addView(bgView)
        countCurrentExistBgView++
        bgView?.startAnimation(animBgShow)
    }

    fun startAnimationFfwd(startTime: Long, ffwdTime: Int) {
        if (presenter.getTotalDuration() < startTime + (ffwdTime - 10) * 1000)
            return

        isPlaying = true
        listener?.onRewind(startTime + (ffwdTime * 1000L))

        // Small icon animation
        for (i in 0 until 3) {
            val icon = ivSmallFfwds[i]
            icon.visibility = View.VISIBLE
            listAnimIconShow[i].startOffset = i * 100L
            icon.startAnimation(listAnimIconShow[i])
        }

        // TextView animation
        tvFfwdTime.visibility = View.VISIBLE
        tvFfwdTime.text = Utils.convertTimeFormat2(ffwdTime, 1)
        val animFadeIn = AlphaAnimation(0f, 1f)
        animFadeIn.duration = 200
        if (ffwdTime == 10) { // 처음 시작때만
            tvFfwdTime.startAnimation(animFadeIn)
        }

        var bgView: View? = createBackgroundView(startTime, ffwdTime, false)
        val animBgShow = AnimationUtils.loadAnimation(view, R.anim.exoplayer_appear_controller)
        val animBgHide = AnimationUtils.loadAnimation(view, R.anim.exoplayer_disappear_controller)
        animBgShow.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                Log.d(TAG, "animBgShow.onAnimationEnd(), childCount=$countCurrentExistBgView")
                bgView?.startAnimation(animBgHide)
                if (countCurrentExistBgView == 1) {
                    tvFfwdTime.startAnimation(animBgHide)
                    for (icon in ivSmallFfwds) {
                        icon.startAnimation(animBgHide)
                    }
                }
            }
        })
        animBgHide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                containerFfwd.removeView(bgView)
                countCurrentExistBgView--
                bgView = null
                Log.d(TAG, "animBgHide.onAnimationEnd(), childCount=$countCurrentExistBgView, ffwdTime=$ffwdTime")
                if (countCurrentExistBgView == 0) { // 마지막 animation
                    tvFfwdTime.visibility = View.GONE
                    for (icon in ivSmallFfwds) {
                        icon.visibility = View.GONE
                    }
                    listener?.onAllAnimationEnd(startTime, ffwdTime)
                    isPlaying = false
                } else { // 진행중
                    listener?.onEachOneAnimationEnd(startTime, ffwdTime)
                }
            }
        })
        containerFfwd.addView(bgView)
        countCurrentExistBgView++
        bgView?.startAnimation(animBgShow)
    }

    fun startAnimationPlay() {
        isPlaying = true
        listener?.onPlay()

        ivPlay.visibility = View.VISIBLE
        val animFadeIn = AlphaAnimation(0f, 1f)
        animFadeIn.duration = 200
        ivPlay.startAnimation(animFadeIn)

        var bgView: View? = createBackgroundViewCenter()
        val animBgShow = AnimationUtils.loadAnimation(view, R.anim.exoplayer_appear_controller)
        val animBgHide = AnimationUtils.loadAnimation(view, R.anim.exoplayer_disappear_controller)
        animBgShow.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                ivPlay.startAnimation(animBgHide)
                bgView?.startAnimation(animBgHide)
            }
        })
        animBgHide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                containerPlayPause.removeView(bgView)
                bgView = null
                ivPlay.visibility = View.GONE
                isPlaying = false
            }
        })

        containerPlayPause.addView(bgView)
        bgView?.startAnimation(animBgShow)
    }

    fun startAnimationPause() {
        isPlaying = true
        listener?.onPause()

        ivPause.visibility = View.VISIBLE
        val animFadeIn = AlphaAnimation(0f, 1f)
        animFadeIn.duration = 200
        ivPause.startAnimation(animFadeIn)

        var bgView: View? = createBackgroundViewCenter()
        val animBgShow = AnimationUtils.loadAnimation(view, R.anim.exoplayer_appear_controller)
        val animBgHide = AnimationUtils.loadAnimation(view, R.anim.exoplayer_disappear_controller)
        animBgShow.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                ivPause.startAnimation(animBgHide)
                bgView?.startAnimation(animBgHide)
            }
        })
        animBgHide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                containerPlayPause.removeView(bgView)
                bgView = null
                ivPause.visibility = View.GONE
                isPlaying = false
            }
        })

        containerPlayPause.addView(bgView)
        bgView?.startAnimation(animBgShow)
    }

    private fun createBackgroundView(startTime: Long, moveTime: Int, isRewind: Boolean): View {
        val view = View(view)
        val layoutParams = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        val marginLeft = if (isRewind) 24 else 12
        val marginRight = if (isRewind) 12 else 24
        layoutParams.setMargins(
            Utils.convertDpToPx(marginLeft, this.view), Utils.convertDpToPx(12, this.view),
            Utils.convertDpToPx(marginRight, this.view), Utils.convertDpToPx(12, this.view)
        )
        view.layoutParams = layoutParams
        view.background = view.resources.getDrawable(R.drawable.exo_controller_background)
        view.tag = true
        view.setOnTouchListener {v, event ->
            Log.v(TAG, "createBackgroundView(), setOnTouchListener, tag=${v.tag}, moveTime=$moveTime")
            val isOneTouch = v.tag as Boolean
            if (event.action == MotionEvent.ACTION_DOWN && isOneTouch) {
                v.tag = false
                if (isRewind) {
                    startAnimationRewind(startTime, moveTime + 10)
                } else {
                    startAnimationFfwd(startTime, moveTime + 10)
                }
            }
            false
        }
        return view
    }

    private fun createBackgroundViewCenter(): View {
        val view = View(view)
        val layoutParams = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.setMargins(
            Utils.convertDpToPx(0, this.view), Utils.convertDpToPx(12, this.view),
            Utils.convertDpToPx(0, this.view), Utils.convertDpToPx(12, this.view)
        )
        view.layoutParams = layoutParams
        view.background = view.resources.getDrawable(R.drawable.exo_controller_background)
        return view
    }

}