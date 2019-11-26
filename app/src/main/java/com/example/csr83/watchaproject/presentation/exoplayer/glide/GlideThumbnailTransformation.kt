/*
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

package com.example.csr83.watchaproject.presentation.exoplayer.glide

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

import java.nio.ByteBuffer
import java.security.MessageDigest


class GlideThumbnailTransformation(context: Context, position: Long) : BitmapTransformation(context) {
    private val TAG = javaClass.simpleName

    companion object {

        val MAX_LINES = 7
        val MAX_COLUMNS = 7
        val THUMBNAILS_EACH = 5000 // millisseconds
    }

    private val x: Int
    private val y: Int

    init {
        val square = position.toInt() / THUMBNAILS_EACH
        y = square / MAX_LINES
        x = square % MAX_COLUMNS
    }

    override fun transform(
        pool: BitmapPool, toTransform: Bitmap,
        outWidth: Int, outHeight: Int
    ): Bitmap {
        val width = toTransform.width / MAX_COLUMNS
        val height = toTransform.height / MAX_LINES
        return Bitmap.createBitmap(toTransform, x * width, y * height, width, height)
    }

    fun updateDiskCacheKey(messageDigest: MessageDigest) {
        val data = ByteBuffer.allocate(8).putInt(x).putInt(y).array()
        messageDigest.update(data)
    }

    override fun hashCode(): Int {
        return (x.toString() + y.toString()).hashCode()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is GlideThumbnailTransformation) {
            return false
        }
        val transformation = obj as GlideThumbnailTransformation?
        return transformation!!.x == x && transformation.y == y
    }

    override fun getId(): String {
        Log.d(TAG, "getId()")
        return "${x}_$y"
//        return ""
    }


}
