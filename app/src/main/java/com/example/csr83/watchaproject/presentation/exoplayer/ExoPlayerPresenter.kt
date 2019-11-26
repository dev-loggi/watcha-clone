package com.example.csr83.watchaproject.presentation.exoplayer

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import android.view.View
import com.example.csr83.watchaproject.R
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.android.synthetic.main.exo_player_top_bar.*
import java.lang.ref.WeakReference

class ExoPlayerPresenter(
    private val view: ExoPlayerContract.View,
    private val playerView: PlayerView,
    private val barListener: ExoPlayerContract.View.TopBottomBarListener
) : ExoPlayerContract.Presenter {

    private val context = view as Activity
    private val player: PlayerManager

    private var exoplayerTask: ExoPlayerObserverAsyncTask? = null
    private var playerObserver = PlayerObserver()

    init {
        player = PlayerManager(context, playerView)
    }

    override fun onResume() {
        val title = context.getString(R.string.content_url_dash)
        player.init(context, playerView, title)
        if (exoplayerTask == null) {
            exoplayerTask = ExoPlayerObserverAsyncTask(this)
            exoplayerTask!!.execute()
//            exoplayerTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    override fun onPause() {
        player.reset()
        if (exoplayerTask != null) {
            exoplayerTask!!.cancel(true)
            exoplayerTask = null
        }
    }

    override fun onStop() {
        this.onDestroy()
    }

    override fun onDestroy() {
        player.release()
        if (exoplayerTask != null) {
            exoplayerTask!!.cancel(true)
            exoplayerTask = null
        }
    }

    override fun play() {
        if (player.getCurrentPosition() >= player.getTotalDuration()) {
            player.seekTo(0)
        }
        player.play()
    }

    override fun pause() { player.pause() }
    override fun seekTo(positionMs: Long) { player.seekTo(positionMs) }
    override fun getTotalDuration(): Long = player.getTotalDuration()
    override fun isPlaying(): Boolean = player.isPlaying()
    override fun getCurrentPosition(): Long = player.getCurrentPosition()

    /**
     * Interface ([ExoPlayerContract.Presenter.PlayerObserver])'s override functions
     */
    override fun initTopBottomBarTaskTime() { playerObserver.initTopBottomBarTaskTime() }
    override fun setEnableAutoUpdateTimeBar(enable: Boolean) { playerObserver.setEnableAutoUpdateTimeBar(enable) }

    inner class PlayerObserver : ExoPlayerContract.Presenter.PlayerObserver {

        override fun initTopBottomBarTaskTime() {
            exoplayerTask?.initTopBottomBarTaskTime()
        }

        override fun setEnableAutoUpdateTimeBar(enable: Boolean) {
            exoplayerTask?.setEnableAutoUpdateTimeBar(enable)
        }
    }


    companion object {
        private class ExoPlayerObserverAsyncTask(reference: ExoPlayerPresenter) : AsyncTask<Unit, Long, Unit>() {
            private val TAG = javaClass.simpleName

            private val TOTAL_DURATION_INIT = 1L
            private val TIME_BAR_UPDATE_VIEW = 2L
            private val TOP_BOTTOM_BAR_AUTO_GONE = 3L

            private val mReference: WeakReference<ExoPlayerPresenter> = WeakReference(reference)

            // Top & Bottom bar Observer
            private var timeThreadStarted = Float.MAX_VALUE
            private var isRunningTimeBarObserver = false
            private var isChangingSeekBarFromUser = false // SeekBar 를 user가 직접 조정하고 있을때

            override fun doInBackground(vararg params: Unit?) {
                val ref = mReference.get() as ExoPlayerPresenter

                // Time bar variable
                var totalDuration = 0L
                var curPosition: Long
                var remainingTime: Long
                var progress: Long

                var threadTime = 0f
                while(!isCancelled) {
                    if ((threadTime * 10).toInt() % 100 == 0) { Log.d(TAG, "doInBackground(${threadTime}초), duration=${ref.player.getTotalDuration()}") }
                    // Time Bar Task
                    if (totalDuration <= 0) {
                        totalDuration = ref.player.getTotalDuration()

                        publishProgress(TOTAL_DURATION_INIT, totalDuration / 1000)
                    } else {
                        curPosition = ref.player.getCurrentPosition()
                        remainingTime = totalDuration - curPosition

                        progress = curPosition / 1000

                        publishProgress(TIME_BAR_UPDATE_VIEW, curPosition, remainingTime, progress)
                    }

                    // Top & Bottom bar visible task

                    isRunningTimeBarObserver = ((ref.view as ExoPlayerActivity).top_bar.visibility == View.VISIBLE)
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
                val reference = mReference.get() as ExoPlayerPresenter

                val type = values[0]
                when (type) {
                    TOTAL_DURATION_INIT -> {
//                        Log.d(TAG, "onProgressUpdate(TOTAL_DURATION_INIT)")
                        val maxValue = values[1]!!.toInt()
                        reference.view.initTimeSeekBar(
                            if (maxValue > 0) maxValue else 0
                        )
                    }
                    TIME_BAR_UPDATE_VIEW -> {
//                        Log.d(TAG, "onProgressUpdate(TIME_BAR_UPDATE_VIEW), progress=${values[3]}")
                        if (isChangingSeekBarFromUser)
                            return

                        val curPosition = values[1]!!
                        val remainingTime = values[2]!!
                        val progress = values[3]!!
                        reference.view.updateTimeBar(curPosition)

                        if (remainingTime.toInt() <= 0) {
                            reference.player.pause()
                        }
                    }
                    TOP_BOTTOM_BAR_AUTO_GONE -> {
//                        Log.d(TAG, "onProgressUpdate(TOP_BOTTOM_BAR_AUTO_GONE)")
                        reference.view.updateTopBottomBarVisible(View.GONE)
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
}