package com.example.csr83.watchaproject.view.exoplayer

import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.*
import android.support.v7.app.AppCompatActivity
import com.example.csr83.watchaproject.R
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.android.synthetic.main.activity_exo_player.*
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import android.widget.SeekBar
import com.example.csr83.watchaproject.utils.Constants
import com.example.csr83.watchaproject.utils.Utils
import com.google.android.exoplayer2.Player
import java.lang.ref.WeakReference


class ExoPlayerActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private lateinit var playerView: PlayerView
    private lateinit var player: PlayerManager
    fun getPlayer() = player

    private lateinit var timebarAsyncTask: TimeBarAsyncTask
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
        player = PlayerManager(this)

        leftGestureListener = ExoGestureListener(this, ExoGestureListener.POS_LEFT)
        centerGestureListener = ExoGestureListener(this, ExoGestureListener.POS_CENTER)
        rightGestureListener = ExoGestureListener(this, ExoGestureListener.POS_RIGHT)

        initView()
    }

    override fun onResume() {
        Log.i(TAG, "onResume")
        super.onResume()
        setImmersiveMode()
        player.init(this, playerView)
        updatePlayPauseButton()
    }

    override fun onPause() {
        Log.i(TAG, "onPause")
        super.onPause()
        player.reset()
        updatePlayPauseButton()
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        player.release()
        timebarAsyncTask.cancel(true)
        super.onDestroy()
    }

    private fun initView() {
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
            player.play()
            updatePlayPauseButton()
        }
        iv_pause.setOnClickListener {
            player.stop()
            updatePlayPauseButton()
        }

        controller_left.setOnTouchListener {_, event ->
            GestureDetector(this, leftGestureListener).onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) {
                leftGestureListener.onUp(event)
            }
            true
        }
        controller_center.setOnTouchListener {_, event ->
            GestureDetector(this, centerGestureListener).onTouchEvent(event)
            true
        }
        controller_right.setOnTouchListener {_, event ->
//            Log.i(TAG, "controller_right_touch_panel.setOnTouchListener, event=$event")
            GestureDetector(this, rightGestureListener).onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) {
                rightGestureListener.onUp(event)
            }
            true
        }

        surface_controller.setOnClickListener {
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                Log.d(TAG, "onProgressChanged()")
                if (!fromUser)
                    return
                val position = progress * 1000L
                val totalDuration = player.getTotalDuration() ?: 0
                tv_position.text = Utils.convertTimeFormat(position)
                tv_duration.text = Utils.convertTimeFormat(totalDuration - position)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                Log.d(TAG, "onStopTrackingTouch()")
                val progress = seekBar?.progress ?: 0
                player.seekTo(progress * 1000L)
            }
        })

        val params = window.attributes
        params.screenBrightness = 0.7f
        seekBar_brightness.progress = (params.screenBrightness * 100).toInt()
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

        updatePlayPauseButton()
        timebarAsyncTask = TimeBarAsyncTask(this)
        timebarAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    fun updateTopBottomBarVisible() {
        val animFadeIn = AnimationUtils.loadAnimation(this, R.anim.exoplayer_fade_in_top_bottom_bar)
        val animFadeOut = AnimationUtils.loadAnimation(this, R.anim.exoplayer_fade_out_top_bottom_bar)
        when (container_top_bar.visibility) {
            View.VISIBLE -> {
                container_top_bar.startAnimation(animFadeOut)
                container_bottom_bar.startAnimation(animFadeOut)
                container_top_bar.visibility = View.GONE
                container_bottom_bar.visibility = View.GONE
            }
            View.GONE -> {
                container_top_bar.startAnimation(animFadeIn)
                container_bottom_bar.startAnimation(animFadeIn)
                container_top_bar.visibility = View.VISIBLE
                container_bottom_bar.visibility = View.VISIBLE
            }
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
            ?: return

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
        updateTopBottomBarVisible()
    }


    companion object {
        private class TimeBarAsyncTask(context: ExoPlayerActivity) : AsyncTask<Unit, Long, Unit>() {
            private val TAG = javaClass.simpleName

            private val activityReference: WeakReference<ExoPlayerActivity>?

            private val PARAMS_TYPE_1 = 1L
            private val PARAMS_TYPE_2 = 2L

            init { activityReference = WeakReference(context) }

            override fun doInBackground(vararg params: Unit?) {
                val activity = activityReference!!.get() as ExoPlayerActivity

                var totalDuration = 0L
                var curPosition: Long
                var remainingTime: Long
                var progress: Long

                var threadTime = 0f
                while(!isCancelled) {
                    if (threadTime % 10 == 0f) {
                        Log.d(TAG, "doInBackground(${threadTime}초), duration=${activity.player.getTotalDuration()}")
                    }
                    if (totalDuration <= 0) {
                        totalDuration = activity.player.getTotalDuration() ?: 0L

                        publishProgress(PARAMS_TYPE_1, totalDuration / 1000)
                    } else {
                        curPosition = activity.player.getCurrentPosition() ?: 0L
                        remainingTime = totalDuration - curPosition

                        progress = curPosition / 1000

                        publishProgress(PARAMS_TYPE_2, curPosition, remainingTime, progress)
                    }
                    Thread.sleep(500L)
                    threadTime += 0.5f

//                    if (threadTime == 600f)
//                        return
                }
            }

            override fun onProgressUpdate(vararg values: Long?) {
                val activity = activityReference!!.get() as ExoPlayerActivity

                val type = values[0]
                when (type) {
                    PARAMS_TYPE_1 -> {
//                        Log.d(TAG, "onProgressUpdate(PARAMS_TYPE_1)")
                        val maxValue = values[1]!!.toInt()
                        activity.seekBar.max = if (maxValue > 0) maxValue else 0
                    }
                    PARAMS_TYPE_2 -> {
//                        Log.d(TAG, "onProgressUpdate(PARAMS_TYPE_2), progress=${values[3]}")
                        activity.tv_position.text = Utils.convertTimeFormat(values[1])
                        activity.tv_duration.text = Utils.convertTimeFormat(values[2])
                        activity.seekBar.progress = values[3]!!.toInt()
                    }
                }
            }
        }



    }



    override fun onStart() {Log.i(TAG, "onStart");super.onStart()}
    override fun onStop() {Log.i(TAG, "onStop");super.onStop()}

}