package com.example.csr83.watchaproject.view.exoplayer

import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.*
import android.support.annotation.IdRes
import android.support.annotation.IntDef
import android.support.v7.app.AppCompatActivity
import com.example.csr83.watchaproject.R
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.android.synthetic.main.activity_exo_player.*
import kotlinx.android.synthetic.main.exo_player_surface_controller.*
import kotlinx.android.synthetic.main.exo_player_bottom_bar.*
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.example.csr83.watchaproject.utils.Constants
import com.example.csr83.watchaproject.utils.Utils
import com.example.csr83.watchaproject.view.exoplayer.glide.GlideThumbnailTransformation
import kotlinx.android.synthetic.main.exo_player_top_bar.*
import java.lang.ref.WeakReference
import kotlin.math.max


class ExoPlayerActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private lateinit var playerView: PlayerView
    private lateinit var player: PlayerManager
    fun getPlayer() = player

    private var exoplayerTask: ExoPlayerObserverAsyncTask? = null
    private lateinit var leftGestureDetector: GestureDetector
    private lateinit var centerGestureDetector: GestureDetector
    private lateinit var rightGestureDetector: GestureDetector
    private lateinit var leftGestureListener: ExoGestureListener
    private lateinit var centerGestureListener: ExoGestureListener
    private lateinit var rightGestureListener: ExoGestureListener

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_exo_player)

        playerView = player_view
        playerView.useController = false
        player = PlayerManager(this, playerView)

        leftGestureListener = ExoGestureListener(this, ExoGestureListener.POS_LEFT)
        centerGestureListener = ExoGestureListener(this, ExoGestureListener.POS_CENTER)
        rightGestureListener = ExoGestureListener(this, ExoGestureListener.POS_RIGHT)

        leftGestureDetector = GestureDetector(this, leftGestureListener)
        centerGestureDetector = GestureDetector(this, centerGestureListener)
        rightGestureDetector = GestureDetector(this, rightGestureListener)

        title = intent.getStringExtra(Constants.INTENT_KEY_MOVIE_TITLE) ?: ""
        initView()
    }
    var title = ""
    override fun onResume() {
        Log.i(TAG, "onResume")
        super.onResume()
        setImmersiveMode()
        player.init(this, playerView, title)
        if (exoplayerTask == null) {
            exoplayerTask = ExoPlayerObserverAsyncTask(this)
            exoplayerTask!!.execute()
//            exoplayerTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    override fun onPause() {
        Log.i(TAG, "onPause")
        super.onPause()
        player.reset()
        if (exoplayerTask != null) {
            exoplayerTask!!.cancel(true)
            exoplayerTask = null
        }
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        player.release()
        if (exoplayerTask != null) {
            exoplayerTask!!.cancel(true)
            exoplayerTask = null
        }
        super.onDestroy()
    }

    private fun initView() {
        container.viewTreeObserver.addOnGlobalLayoutListener {
            Log.i(TAG, "addOnGlobalLayoutListener")
            exoplayerTask?.initTopBottomBarTaskTime()
        }

        tv_movie_title.text = intent.getStringExtra(Constants.INTENT_KEY_MOVIE_TITLE) ?: ""
        iv_back.setOnClickListener { finish() }
        iv_lock.setOnClickListener { lockScreen(true) }
        iv_unlock.setOnClickListener { lockScreen(false) }

        screen_lock.setOnClickListener {
            if (iv_unlock.visibility == View.VISIBLE) {
                iv_unlock.startAnimation(AnimationUtils.loadAnimation(this, R.anim.exoplayer_fade_out_top_bottom_bar))
                iv_unlock.visibility = View.GONE
            } else if (iv_unlock.visibility == View.GONE) {
                iv_unlock.visibility = View.VISIBLE
                iv_unlock.startAnimation(AnimationUtils.loadAnimation(this, R.anim.exoplayer_fade_in_top_bottom_bar))
            }
        }
        iv_play.setOnClickListener {
            if (player.getCurrentPosition() >= player.getTotalDuration()) {
                player.seekTo(0)
            }
            player.play()
        }
        iv_pause.setOnClickListener {
            player.pause()
        }

        controller_left.setOnTouchListener {_, event ->
            leftGestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) {
                leftGestureListener.onUp(event)
            }
            true
        }
        controller_center.setOnTouchListener {_, event ->
            centerGestureDetector.onTouchEvent(event)
            true
        }
        controller_right.setOnTouchListener {_, event ->
            rightGestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) {
                rightGestureListener.onUp(event)
            }
            true
        }

        val thumbnailsUrl = resources.getString(R.string.content_url_thumbnails)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(view: SeekBar?, progress: Int, fromUser: Boolean) {
//                Log.d(TAG, "onProgressChanged()")
                if (!fromUser)
                    return
                val position = progress * 1000L
                val totalDuration = player.getTotalDuration()
                tv_position.text = Utils.convertTimeFormat(position)
                tv_duration.text = Utils.convertTimeFormat(totalDuration - position)

                Log.d(TAG, "view.x=${view?.x}")
                val x = view?.x ?: 0f
                val width = view?.width ?: 0
                val maxProgress = view?.max ?: 0
                fl_preview.x = x + (width * progress / maxProgress.toFloat())

                Glide.with(applicationContext)
                    .load(thumbnailsUrl)
                    .transform(GlideThumbnailTransformation(applicationContext, position))
                    .into(iv_preview)

                exoplayerTask?.initTopBottomBarTaskTime()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                exoplayerTask?.setEnableAutoUpdateTimeBar(true)
                fl_preview.visibility = View.VISIBLE
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                Log.d(TAG, "onStopTrackingTouch()")
                val progress = seekBar?.progress ?: 0
                player.seekTo(progress * 1000L)

                exoplayerTask?.setEnableAutoUpdateTimeBar(false)
                fl_preview.visibility = View.GONE
            }
        })

        val params = window.attributes
        params.screenBrightness = 0.7f
        seekBar_brightness.progress = (params.screenBrightness * 100).toInt()
        seekBar_brightness.visibility = View.INVISIBLE  // GONE 으로 하면 동작오류남.
        seekBar_brightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                Log.d(TAG, "seekBar_sound.onProgressChanged(), progress=$progress, fromUser=$fromUser")
                if (!fromUser) {
                    params.screenBrightness = progress / 100f
                    window.attributes = params
                    Log.d(TAG, "brightness=${window.attributes.screenBrightness}")
                    tv_brightness.text = String.format("%03d", progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekBar_brightness.visibility = View.VISIBLE
                brightness_view.visibility = View.VISIBLE
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar_brightness.visibility = View.INVISIBLE
                brightness_view.visibility = View.INVISIBLE
            }
        })

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        seekBar_sound.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        seekBar_sound.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        seekBar_sound.visibility = View.INVISIBLE   // GONE 으로 하면 동작오류남.
        seekBar_sound.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                Log.d(TAG, "seekBar_sound.onProgressChanged(), progress=$progress, fromUser=$fromUser")
                if (!fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                    tv_sound.text = String.format("%03d", progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekBar_sound.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                seekBar_sound.visibility = View.VISIBLE
                sound_view.visibility = View.VISIBLE
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar_sound.visibility = View.INVISIBLE
                sound_view.visibility = View.INVISIBLE
            }
        })

    }

    /**
     * curVisible == null : 수동으로 visible 조정
     * curVisible != null : 자동으로 visible 조정
     */
    fun updateTopBottomBarVisible(visible: Int?) {
        val animFadeIn = AnimationUtils.loadAnimation(this, R.anim.exoplayer_fade_in_top_bottom_bar)
        val animFadeOut = AnimationUtils.loadAnimation(this, R.anim.exoplayer_fade_out_top_bottom_bar)
        val selectVisible = when (visible) {
            View.VISIBLE -> View.GONE
            View.GONE -> View.VISIBLE
            else -> null
        }
        if (top_bar.visibility == selectVisible ?: View.VISIBLE) {
            top_bar.startAnimation(animFadeOut)
            top_bar.visibility = View.GONE
            bottom_bar.startAnimation(animFadeOut)
            bottom_bar.visibility = View.GONE
        } else if (top_bar.visibility == selectVisible ?: View.GONE) {
            top_bar.startAnimation(animFadeIn)
            top_bar.visibility = View.VISIBLE
            bottom_bar.startAnimation(animFadeIn)
            bottom_bar.visibility = View.VISIBLE
        }
    }

    fun updatePlayPauseButton() {
        val isPlaying = player.isPlaying()
        iv_play.visibility = if (isPlaying) View.GONE else View.VISIBLE
        iv_pause.visibility = if (isPlaying) View.VISIBLE else View.GONE
    }

    fun updateTimeBar(curPosition: Long) {
        var pos = curPosition
        val totalDuration = player.getTotalDuration()

        if (pos < 0) {
            pos = 0L
        } else if (pos > totalDuration) {
            pos = totalDuration
        }

        seekBar.progress = (pos / 1000).toInt()
        tv_position.text = Utils.convertTimeFormat(pos)
        tv_duration.text = Utils.convertTimeFormat(totalDuration - pos)
    }

    @SuppressWarnings("ObsoleteSdkInt")
    private fun setImmersiveMode() {
        val decorView = window.decorView
        var uiOption = window.decorView.systemUiVisibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            uiOption = uiOption or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            uiOption = uiOption or View.SYSTEM_UI_FLAG_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            uiOption = uiOption or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        decorView.systemUiVisibility = uiOption
    }

    private fun lockScreen(isLock: Boolean) {
        if (isLock) { // 잠그기
            screen_lock.visibility = View.VISIBLE
            screen_lock.startAnimation(AnimationUtils.loadAnimation(this, R.anim.exoplayer_fade_in_top_bottom_bar))
        } else { // 풀기
            screen_lock.visibility = View.GONE
            screen_lock.startAnimation(AnimationUtils.loadAnimation(this, R.anim.exoplayer_fade_out_top_bottom_bar))
        }
        updateTopBottomBarVisible(null)
    }

    companion object {
        private class ExoPlayerObserverAsyncTask(context: ExoPlayerActivity) : AsyncTask<Unit, Long, Unit>() {
            private val TAG = javaClass.simpleName

            private val activityReference: WeakReference<ExoPlayerActivity>?

            private val TOTAL_DURATION_INIT = 1L
            private val TIMB_BAR_UPDATE_VIEW = 2L
            private val TOP_BOTTOM_BAR_AUTO_GONE = 3L

            // Top & Bottom bar Observer
            private var timeThreadStarted = Float.MAX_VALUE
            private var isRunningTimeBarObserver = false
            private var isChangingSeekBarFromUser = false // SeekBar 를 user가 직접 조정하고 있을때

            init { activityReference = WeakReference(context) }

            override fun doInBackground(vararg params: Unit?) {
                val activity = activityReference!!.get() as ExoPlayerActivity

                // Time bar variable
                var totalDuration = 0L
                var curPosition: Long
                var remainingTime: Long
                var progress: Long

                var threadTime = 0f
                while(!isCancelled) {
                    if ((threadTime * 10).toInt() % 100 == 0) { Log.d(TAG, "doInBackground(${threadTime}초), duration=${activity.player.getTotalDuration()}") }
                    // Time Bar Task
                    if (totalDuration <= 0) {
                        totalDuration = activity.player.getTotalDuration()

                        publishProgress(TOTAL_DURATION_INIT, totalDuration / 1000)
                    } else {
                        curPosition = activity.player.getCurrentPosition()
                        remainingTime = totalDuration - curPosition

                        progress = curPosition / 1000

                        publishProgress(TIMB_BAR_UPDATE_VIEW, curPosition, remainingTime, progress)
                    }

                    // Top & Bottom bar visible task
                    isRunningTimeBarObserver = (activity.top_bar.visibility == View.VISIBLE)
                    if (isRunningTimeBarObserver) { // top & bottom bar : visible
                        if (timeThreadStarted == Float.MAX_VALUE) {
                            timeThreadStarted = threadTime
                        }
                        if (threadTime - timeThreadStarted > 5f) {
                            publishProgress(TOP_BOTTOM_BAR_AUTO_GONE)
                            initTopBottomBarTaskTime()
                        }
                    } else { // top & bottom bar : gone
                        initTopBottomBarTaskTime()
                    }

                    Thread.sleep(100L)
                    threadTime += 0.1f
                }
            }

            override fun onProgressUpdate(vararg values: Long?) {
                val activity = activityReference!!.get() as ExoPlayerActivity

                val type = values[0]
                when (type) {
                    TOTAL_DURATION_INIT -> {
//                        Log.d(TAG, "onProgressUpdate(PARAMS_TYPE_1)")
                        val maxValue = values[1]!!.toInt()
                        activity.seekBar.max = if (maxValue > 0) maxValue else 0
                    }
                    TIMB_BAR_UPDATE_VIEW -> {
//                        Log.d(TAG, "onProgressUpdate(PARAMS_TYPE_2), progress=${values[3]}")
                        if (isChangingSeekBarFromUser)
                            return

                        val curPosition = values[1]!!
                        val remainingTime = values[2]!!
                        val progress = values[3]!!
                        activity.tv_position.text = Utils.convertTimeFormat(curPosition)
                        activity.tv_duration.text = Utils.convertTimeFormat(remainingTime)
                        activity.seekBar.progress = progress.toInt()

                        if (remainingTime.toInt() <= 0) {
                            activity.getPlayer().pause()
                        }
                    }
                    TOP_BOTTOM_BAR_AUTO_GONE -> {
                        activity.updateTopBottomBarVisible(View.GONE)
                    }
                }
            }

            fun initTopBottomBarTaskTime() {
                timeThreadStarted = Float.MAX_VALUE
            }
            fun setEnableAutoUpdateTimeBar(enable: Boolean) {
                isChangingSeekBarFromUser = enable
            }
        }
    }

    override fun onStart() {Log.i(TAG, "onStart");super.onStart()}
    override fun onStop() {Log.i(TAG, "onStop");super.onStop()}
}