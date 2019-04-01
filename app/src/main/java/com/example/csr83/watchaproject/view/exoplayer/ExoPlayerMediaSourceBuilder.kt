/*
 * Copyright 2016 The Android Open Source Project
 * Copyright 2017 Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.csr83.watchaproject.view.exoplayer


import android.content.Context
import android.net.Uri
import android.os.Handler
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

class ExoPlayerMediaSourceBuilder(private val context: Context) {

    private val bandwidthMeter: DefaultBandwidthMeter
    private var uri: Uri? = null
    private var streamType: Int = 0
    private val mainHandler = Handler()

    init {
        this.bandwidthMeter = DefaultBandwidthMeter()
    }

    fun setUri(uri: Uri) {
        this.uri = uri
        this.streamType = Util.inferContentType(uri.lastPathSegment)
    }

    fun getMediaSource(preview: Boolean): MediaSource {
        when (streamType) {
            C.TYPE_SS -> return SsMediaSource(
                uri, DefaultDataSourceFactory(
                    context, null,
                    getHttpDataSourceFactory(preview)
                ),
                DefaultSsChunkSource.Factory(getDataSourceFactory(preview)),
                mainHandler, null
            )
            C.TYPE_DASH -> return DashMediaSource(
                uri,
                DefaultDataSourceFactory(
                    context, null,
                    getHttpDataSourceFactory(preview)
                ),
                DefaultDashChunkSource.Factory(getDataSourceFactory(preview)),
                mainHandler, null
            )
            C.TYPE_HLS -> return HlsMediaSource(uri, getDataSourceFactory(preview), mainHandler, null)
            C.TYPE_OTHER -> return ExtractorMediaSource(
                uri, getDataSourceFactory(preview),
                DefaultExtractorsFactory(), mainHandler, null
            )
            else -> {
                throw IllegalStateException("Unsupported type: $streamType")
            }
        }
    }

    private fun getDataSourceFactory(preview: Boolean): DataSource.Factory {
        return DefaultDataSourceFactory(
            context, if (preview) null else bandwidthMeter,
            getHttpDataSourceFactory(preview)
        )
    }

    private fun getHttpDataSourceFactory(preview: Boolean): DataSource.Factory {
        return DefaultHttpDataSourceFactory(
            Util.getUserAgent(
                context,
                "ExoPlayerDemo"
            ), if (preview) null else bandwidthMeter
        )
    }
}
