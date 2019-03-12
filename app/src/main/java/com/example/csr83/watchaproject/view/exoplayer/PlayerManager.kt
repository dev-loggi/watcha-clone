package com.example.csr83.watchaproject.view.exoplayer

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.csr83.watchaproject.R
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class PlayerManager(context: Context) {

    private val TAG = javaClass.simpleName

    private val dataSourceFactory: DataSource.Factory
    private var player: SimpleExoPlayer? = null
    private var contentPosition = 0L

    init {
        dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context,"ExoPlayer"))
    }

    fun init(context: Context, playerView: PlayerView) {
        player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
        playerView.player = this.player

        val contentUrl = context.getString(R.string.content_url)
        val contentMediaSource: MediaSource =
            ExtractorMediaSource
                .Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(contentUrl))

        Log.d(TAG, "init(), contentPosition$contentPosition")
        player?.prepare(contentMediaSource)
        player?.seekTo(contentPosition)
        player?.playWhenReady = true
    }

//    fun getExoPlayer() = player

    fun play() { player?.playWhenReady = true }
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
    fun getTotalDuration() = player?.duration
    fun getCurrentPosition() = player?.currentPosition
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