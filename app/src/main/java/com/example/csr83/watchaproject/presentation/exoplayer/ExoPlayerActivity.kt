package com.example.csr83.watchaproject.presentation.exoplayer

import android.app.Dialog
import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.os.*
import android.support.v7.app.AppCompatActivity
import com.example.csr83.watchaproject.R
import kotlinx.android.synthetic.main.activity_exo_player.*
import kotlinx.android.synthetic.main.exo_player_surface_controller.*
import kotlinx.android.synthetic.main.exo_player_bottom_bar.*
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.csr83.watchaproject.utils.Constants
import com.example.csr83.watchaproject.utils.Utils
import com.example.csr83.watchaproject.presentation.exoplayer.glide.GlideThumbnailTransformation
import kotlinx.android.synthetic.main.exo_player_top_bar.*

class ExoPlayerActivity : AppCompatActivity(), ExoPlayerContract.View {
    private val TAG = javaClass.simpleName

    companion object {
        const val PIP_AVAILABLE_VERSION_CODE = Build.VERSION_CODES.O
    }

    private val barListener = BarListener()
    private lateinit var presenter: ExoPlayerPresenter

    private lateinit var leftGestureDetector: GestureDetector
    private lateinit var centerGestureDetector: GestureDetector
    private lateinit var rightGestureDetector: GestureDetector

    private lateinit var leftGestureListener: ExoGestureListener
    private lateinit var centerGestureListener: ExoGestureListener
    private lateinit var rightGestureListener: ExoGestureListener

    private var isPipToFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (Build.VERSION.SDK_INT >= 21) {
            window.navigationBarColor = Color.BLACK
        }

        setContentView(R.layout.activity_exo_player)

        player_view.useController = false
        presenter = ExoPlayerPresenter(this, player_view, barListener)

        leftGestureListener = ExoGestureListener(this, presenter ,ExoGestureListener.POS_LEFT)
        centerGestureListener = ExoGestureListener(this, presenter, ExoGestureListener.POS_CENTER)
        rightGestureListener = ExoGestureListener(this, presenter, ExoGestureListener.POS_RIGHT)

        leftGestureDetector = GestureDetector(this, leftGestureListener)
        centerGestureDetector = GestureDetector(this, centerGestureListener)
        rightGestureDetector = GestureDetector(this, rightGestureListener)

        title = intent.getStringExtra(Constants.INTENT_KEY_MOVIE_TITLE) ?: ""
        initView()

    }

    override fun onStart() {Log.i(TAG, "onStart");super.onStart()}

    override fun onResume() {
        Log.i(TAG, "onResume")
        super.onResume()
        setImmersiveMode(window)
        updateTopBottomBarVisible(View.VISIBLE)
        if (Build.VERSION.SDK_INT >= PIP_AVAILABLE_VERSION_CODE && isPipToFullScreen) {
            isPipToFullScreen = false
        } else {
            presenter.onResume()
        }
    }

    override fun onPause() {
        Log.i(TAG, "onPause")
        super.onPause()
        if (Build.VERSION.SDK_INT >= PIP_AVAILABLE_VERSION_CODE && isInPictureInPictureMode) {
            isPipToFullScreen = true
            updateTopBottomBarVisible(View.GONE)
        } else {
            presenter.onPause()
        }
    }

    override fun onStop() {
        Log.i(TAG, "onStop")
        super.onStop()
        presenter.onStop()
        isPipToFullScreen = false
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        presenter.onDestroy()
        super.onDestroy()
    }



    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        Log.d(TAG, "onPictureInPictureModeChanged($isInPictureInPictureMode) $newConfig")
    }

    private fun initView() {
        container.viewTreeObserver.addOnGlobalLayoutListener {
            Log.i(TAG, "addOnGlobalLayoutListener")
            presenter.initTopBottomBarTaskTime()
        }

        tv_movie_title.text = intent.getStringExtra(Constants.INTENT_KEY_MOVIE_TITLE) ?: ""

        iv_back.setOnClickListener { barListener.onBackButtonClick() }
        iv_pip_mode.setOnClickListener { barListener.onPipButtonClick() }
        iv_info.setOnClickListener { barListener.onHelpButtonClick() }
        iv_setting.setOnClickListener { barListener.onSettingButtonClick() }
        iv_lock.setOnClickListener { barListener.onLockButtonClick() }
        iv_unlock.setOnClickListener { barListener.onUnlockButtonClick() }
        screen_lock.setOnClickListener { barListener.onLockScreenTouch() }
        iv_play.setOnClickListener { barListener.onPlayClick() }
        iv_pause.setOnClickListener { barListener.onPauseClick() }

        val thumbnailsUrl = resources.getString(R.string.content_url_thumbnails)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(view: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser || view == null)
                    return

                val position = progress * 1000L
                val totalDuration = presenter.getTotalDuration()
                tv_position.text = Utils.convertTimeFormat(position)
                tv_duration.text = Utils.convertTimeFormat(totalDuration - position)

                Log.d(TAG, "view.x=${view.x}")
                val x = view.x
                val width = view.width
                val maxProgress = view.max
                fl_preview.x = x + (width * progress / maxProgress.toFloat())

                Glide.with(applicationContext)
                    .load(thumbnailsUrl)
                    .transform(GlideThumbnailTransformation(applicationContext, position))
                    .into(iv_preview)

                presenter.initTopBottomBarTaskTime()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                presenter.setEnableAutoUpdateTimeBar(true)
                fl_preview.visibility = View.VISIBLE
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                Log.d(TAG, "onStopTrackingTouch()")
                val progress = seekBar?.progress ?: 0
                presenter.seekTo(progress * 1000L)

                presenter.setEnableAutoUpdateTimeBar(false)
                fl_preview.visibility = View.GONE
            }
        })

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

    override fun initTimeSeekBar(duration: Int) {
        seekBar.max = duration
    }

    /**
     * curVisible == null : 수동으로 visible 조정
     * curVisible != null : 자동으로 visible 조정
     */
    override fun updateTopBottomBarVisible(visible: Int?) {
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

    override fun updatePlayPauseButton() {
        val isPlaying = presenter.isPlaying()
        iv_play.visibility = if (isPlaying) View.GONE else View.VISIBLE
        iv_pause.visibility = if (isPlaying) View.VISIBLE else View.GONE
    }

    override fun updateTimeBar(curPosition: Long) {
        var pos = curPosition
        val totalDuration = presenter.getTotalDuration()

        if (pos < 0) {
            pos = 0L
        } else if (pos > totalDuration) {
            pos = totalDuration
        }

        seekBar.progress = (pos / 1000).toInt()
        tv_position.text = Utils.convertTimeFormat(pos)
        tv_duration.text = Utils.convertTimeFormat(totalDuration - pos)
    }

    inner class BarListener : ExoPlayerContract.View.TopBottomBarListener {

        override fun onBackButtonClick() { finish() }

        override fun onPipButtonClick() {
            if (Build.VERSION.SDK_INT >= PIP_AVAILABLE_VERSION_CODE) {
                enterPictureInPictureMode(PictureInPictureParams.Builder().build())
            }
        }

        override fun onHelpButtonClick() {
            presenter.pause()

            Dialog(this@ExoPlayerActivity).also {
                it.setContentView(R.layout.exo_player_info_dialog)
                it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setImmersiveMode(it.window)
                it.setOnCancelListener {
                    setImmersiveMode(this@ExoPlayerActivity.window)
                    presenter.play()
                }
            }.show()
        }

        override fun onSettingButtonClick() { Toast.makeText(this@ExoPlayerActivity, resources.getString(R.string.exoplayer_no_translations), Toast.LENGTH_SHORT).show() }

        override fun onLockButtonClick() { lockScreen(true) }

        override fun onUnlockButtonClick() { lockScreen(false) }

        override fun onLockScreenTouch() {
            if (iv_unlock.visibility == View.VISIBLE) {
                iv_unlock.startAnimation(AnimationUtils.loadAnimation(this@ExoPlayerActivity, R.anim.exoplayer_fade_out_top_bottom_bar))
                iv_unlock.visibility = View.GONE
            } else if (iv_unlock.visibility == View.GONE) {
                iv_unlock.visibility = View.VISIBLE
                iv_unlock.startAnimation(AnimationUtils.loadAnimation(this@ExoPlayerActivity, R.anim.exoplayer_fade_in_top_bottom_bar))
            }
        }

        override fun onPlayClick() { presenter.play() }
        override fun onPauseClick() { presenter.pause() }

        private fun lockScreen(isLock: Boolean) {
            if (isLock) { // 잠그기
                screen_lock.visibility = View.VISIBLE
                screen_lock.startAnimation(AnimationUtils.loadAnimation(this@ExoPlayerActivity, R.anim.exoplayer_fade_in_top_bottom_bar))
            } else { // 풀기
                screen_lock.visibility = View.GONE
                screen_lock.startAnimation(AnimationUtils.loadAnimation(this@ExoPlayerActivity, R.anim.exoplayer_fade_out_top_bottom_bar))
            }
            updateTopBottomBarVisible(null)
        }
    }

    @SuppressWarnings("ObsoleteSdkInt")
    private fun setImmersiveMode(window: Window?) {
        if (window == null)
            return

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
}