package com.example.csr83.watchaproject.presentation.exoplayer

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class PlayerManager(context: Context, playerView: PlayerView) {

    private val TAG = javaClass.simpleName

    private val dataSourceFactory: DataSource.Factory
    private val mediaSourceBuilder: ExoPlayerMediaSourceBuilder
    private var player: SimpleExoPlayer? = null

    private var contentPosition = 0L

    private val eventListener = object : Player.DefaultEventListener() {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            val state = when(playbackState) {
                Player.STATE_IDLE -> "STATE_IDLE"
                Player.STATE_BUFFERING -> "STATE_BUFFERING"
                Player.STATE_READY -> "STATE_READY"
                Player.STATE_ENDED -> "STATE_ENDED"
                else -> ""
            }
            Log.i(TAG, "onPlayerStateChanged(), playWhenReady=$playWhenReady, playbackState=$state")
            if (playbackState == Player.STATE_READY) {
                (context as ExoPlayerActivity).updatePlayPauseButton()
            }
            if (playbackState == Player.STATE_READY && playWhenReady) {
//                if (previewTimeBar != null) {
//                    previewTimeBar.hidePreview()
//                } else if (previewSeekBar != null) {
//                    previewSeekBar.hidePreview()
//                }
            }
        }
    }

    init {
        dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context,"ExoPlayer"))
        mediaSourceBuilder = ExoPlayerMediaSourceBuilder(playerView.context)
    }

    fun init(context: Context, playerView: PlayerView, title: String) {
        player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
        playerView.player = this.player

//        var contentUrl = ""
//        when (title) {
//            "어메이징 스파이더맨2" -> contentUrl = context.getString(R.string.content_url)
//            "트랜스포머" -> contentUrl = context.getString(R.string.content_url2)
//            "트랜스포머2: 패자의 역습" -> contentUrl = context.getString(R.string.content_url3)
//            "트랜스포머3: Dark Of the Moon" -> contentUrl = context.getString(R.string.content_url4)
//            else -> contentUrl = context.getString(R.string.content_url)
//        }
//        val contentMediaSource: MediaSource =
//            ExtractorMediaSource
//                .Factory(dataSourceFactory)
//                .createMediaSource(Uri.parse(contentUrl))
//        player?.prepare(contentMediaSource)

        mediaSourceBuilder.setUri(Uri.parse(title))
        player?.prepare(mediaSourceBuilder.getMediaSource(false))
        player?.addListener(eventListener)
        player?.seekTo(contentPosition)
        player?.playWhenReady = true
    }

    fun play() {
        player?.playWhenReady = true
    }
    fun pause() { player?.playWhenReady = false }
    fun seekTo(positionMs: Long) {
        var pos = positionMs
        val totalDuration = player?.duration
            ?: return

        if (pos < 0) {
            pos = 0L
        } else if (pos > totalDuration) {
            pos = totalDuration
        }
        player?.seekTo(pos)
    }
    fun getTotalDuration() = player?.duration ?: 0
    fun getCurrentPosition() = player?.currentPosition ?: 0
    fun isPlaying() = player?.playbackState != Player.STATE_ENDED
            && player?.playbackState != Player.STATE_IDLE
            && player?.playWhenReady
            ?: false

    fun reset() {
        Log.d(TAG, "reset(), player?.contentPosition=${player?.contentPosition}")
        contentPosition = player?.contentPosition ?: 0L
        player?.release()
        player = null
    }

    fun release() {
        player?.release()
        player = null
    }


}