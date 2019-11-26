package com.example.csr83.watchaproject.presentation.exoplayer

interface ExoPlayerContract {

    interface View {
        interface TopBottomBarListener {
            fun onBackButtonClick()
            fun onPipButtonClick()
            fun onHelpButtonClick()
            fun onSettingButtonClick()
            fun onLockButtonClick()
            fun onUnlockButtonClick()
            fun onLockScreenTouch()
            fun onPlayClick()
            fun onPauseClick()
        }

        fun initTimeSeekBar(duration: Int)
        fun updateTopBottomBarVisible(visible: Int?)
        fun updatePlayPauseButton()
        fun updateTimeBar(curPosition: Long)
    }

    interface Presenter {
        fun onResume()
        fun onPause()
        fun onStop()
        fun onDestroy()

        fun play()
        fun pause()
        fun seekTo(positionMs: Long)
        fun isPlaying(): Boolean
        fun getTotalDuration(): Long
        fun getCurrentPosition(): Long

        interface PlayerObserver {
            fun initTopBottomBarTaskTime()
            fun setEnableAutoUpdateTimeBar(enable: Boolean)
        }
        fun initTopBottomBarTaskTime()
        fun setEnableAutoUpdateTimeBar(enable: Boolean)
    }
}