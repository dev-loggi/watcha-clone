package com.example.csr83.watchaproject.view.exoplayer

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.support.v7.widget.LinearLayoutCompat
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.utils.Utils
import kotlinx.android.synthetic.main.activity_exo_player.*
import kotlinx.coroutines.delay
import java.lang.ref.WeakReference

class ExoGestureListener(private val activity: ExoPlayerActivity, private val controller_position: Int) : GestureDetector.SimpleOnGestureListener() {
    private val TAG = javaClass.simpleName

    private var doubleTabAsyncTask: DoubleTapAsyncTask? = null
    private var isStartScrolling = false
    private var startScrollingY = 0f

    override fun onDown(e: MotionEvent?): Boolean {
        Log.d(TAG, "onDown(), e=$e")
        if (e == null) return false

        when (controller_position) {
            POS_LEFT, POS_RIGHT -> startScrollingY = e.y
        }

        val task = doubleTabAsyncTask
        Log.d(TAG, "${task == null}, $controller_position")
        if (task == null) {
            doubleTabAsyncTask = DoubleTapAsyncTask(activity, this, controller_position)
            doubleTabAsyncTask!!.executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                controller_position.toLong(),
                activity.getPlayer().getCurrentPosition() ?: 0L
            )
        } else if (!task.isCancelled) {
            task.isDoubleTap = true
        }

        return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        when (controller_position) {
            POS_LEFT -> {
                val offset = (startScrollingY + distanceY) * activity.seekBar_brightness.max / activity.seekBar_brightness.height
                if (!isStartScrolling) {
                    if (-1 < offset && offset < 1) {
                        return false
                    } else {
                        isStartScrolling = true
                        doubleTabAsyncTask?.isScrolling = true
                        activity.seekBar_brightness.onExoTouchEvent(MotionEvent.ACTION_DOWN, 0f)
                    }
                }
                isStartScrolling = activity.seekBar_brightness.onExoTouchEvent(MotionEvent.ACTION_SCROLL, offset)
            }
            POS_RIGHT -> {
                val offset = (startScrollingY + distanceY) * activity.seekBar_sound.max / activity.seekBar_sound.height
                Log.d(TAG, "onScroll() offset=$offset, distanceY=$distanceY, seekBar_sound.max=${activity.seekBar_sound.max}")

                if (!isStartScrolling) {
                    if (-1 < offset && offset < 1) {
                        return false
                    } else {
                        isStartScrolling = true
                        doubleTabAsyncTask?.isScrolling = true
                        activity.seekBar_sound.onExoTouchEvent(MotionEvent.ACTION_DOWN, 0f)
                    }
                }
                isStartScrolling = activity.seekBar_sound.onExoTouchEvent(MotionEvent.ACTION_SCROLL, offset)
            }
        }
        return false
    }

    fun onUp(e: MotionEvent?) {
        Log.d(TAG, "onUp()")
        if (!isStartScrolling)
            return
        when (controller_position) {
            POS_LEFT -> activity.seekBar_brightness.onExoTouchEvent(MotionEvent.ACTION_UP, 0f)
            POS_RIGHT -> isStartScrolling = activity.seekBar_sound.onExoTouchEvent(MotionEvent.ACTION_UP, 0f)
        }
        isStartScrolling = false
    }

    fun setTaskNull() {
        doubleTabAsyncTask = null
    }


    @SuppressWarnings("ClickableViewAccessibility")
    private fun startDoubleTapButtonAnimation(type: Int, moveTime: Int, firstPosition: Long) {
        Log.i(TAG, "startDoubleTapButtonAnimation(type: $type, moveTime: $moveTime, firstPosition: $firstPosition)")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            return

        when (type) {
            POS_LEFT, POS_RIGHT -> {
//                activity.getPlayer().stop()
                activity.getPlayer().seekTo(firstPosition + (moveTime * 1000L * type))
                activity.updateTimeBar(firstPosition + (moveTime * 1000L * type))
//                activity.getPlayer().play()
                activity.updatePlayPauseButton()
            }
        }

        // Container
        var container: LinearLayout? = LinearLayout(activity)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        when (type) {
            POS_LEFT, POS_RIGHT -> layoutParams.setMargins(
                Utils.convertDpToPx(24, activity), Utils.convertDpToPx(16, activity),
                Utils.convertDpToPx(24, activity), Utils.convertDpToPx(16, activity)
            )
            POS_CENTER -> layoutParams.setMargins(
                Utils.convertDpToPx(0, activity), Utils.convertDpToPx(12, activity),
                Utils.convertDpToPx(12, activity), Utils.convertDpToPx(0, activity)
            )
        }
        container?.layoutParams = layoutParams
        container?.orientation = LinearLayout.VERTICAL
        container?.background = activity.resources.getDrawable(R.drawable.exo_controller_background)
        container?.gravity = Gravity.CENTER

        // imageView
        val imageView = ImageView(activity)
        imageView.layoutParams = LinearLayout.LayoutParams(
            Utils.convertDpToPx(50, activity),
            Utils.convertDpToPx(50, activity)
        )
        imageView.setImageResource(when(type) {
            POS_LEFT -> R.drawable.exo_controls_rewind
            POS_CENTER ->
                if (activity.getPlayer().isPlaying())
                    R.drawable.exo_controls_pause else
                    R.drawable.exo_controls_play
            POS_RIGHT -> R.drawable.exo_controls_fastforward
            else -> 0
        })
        container?.addView(imageView)

        // textView
        if (type == POS_LEFT || type == POS_RIGHT) {
            val textView = TextView(activity)
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textView.setTextColor(activity.resources.getColor(R.color.white))
            textView.textSize = 18f
            textView.text = "${moveTime}초"
            container?.addView(textView)
        }

        if (type == POS_CENTER) {
            if (activity.getPlayer().isPlaying()) {
                activity.getPlayer().stop()
            } else {
                activity.getPlayer().play()
            }
            activity.updatePlayPauseButton()
        } else {
            val isOneTouch = false
            container?.tag = isOneTouch
            container?.setOnClickListener {
                val isOneTouch = it.tag as Boolean
                if (isOneTouch)
                    return@setOnClickListener

                val nextMoveTime = moveTime + 10
                Log.i(TAG, "ok, nextMoveTime:$nextMoveTime, seekTo(${firstPosition + (nextMoveTime * 1000L * type)})")
                startDoubleTapButtonAnimation(type, nextMoveTime, firstPosition)

                it.tag = true
            }
        }

        // addView container
        when (type) {
            POS_LEFT -> {
                ((activity.controller_left
                    .getChildAt(0) as? LinearLayout)
                    ?.getChildAt(1) as? TextView)
                    ?.visibility = View.INVISIBLE
                activity.controller_left.addView(container, 0)
            }
            POS_CENTER -> activity.controller_center.addView(container, 0)
            POS_RIGHT -> {
                ((activity.controller_right
                    .getChildAt(0) as? LinearLayout)
                    ?.getChildAt(1) as? TextView)
                    ?.visibility = View.INVISIBLE
                activity.controller_right.addView(container, 0)
            }
        }

        val animAppear = AnimationUtils.loadAnimation(activity, R.anim.exoplayer_appear_controller)
        val animDisappear = AnimationUtils.loadAnimation(activity, R.anim.exoplayer_disappear_controller)
        animAppear.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                Log.i(TAG, "onAnimationEnd(animAppear)")
                container?.startAnimation(animDisappear)
            }
        })
        animDisappear.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                Log.i(TAG, "onAnimationEnd(animDisappear)")
                when (controller_position) {
                    POS_LEFT -> activity.controller_left.removeView(container)
                    POS_CENTER -> activity.controller_center.removeView(container)
                    POS_RIGHT -> activity.controller_right.removeView(container)
                }
                container = null
            }
        })
        container?.startAnimation(animAppear)
    }

    companion object {
        val POS_LEFT = -1
        val POS_CENTER = 0
        val POS_RIGHT = 1

        private class DoubleTapAsyncTask(activity: ExoPlayerActivity, private val caller: ExoGestureListener, private val taskType: Int) : AsyncTask<Long, Long, Unit>() {
            private val TAG = javaClass.simpleName

            private val activityReference: WeakReference<ExoPlayerActivity>?
            var isDoubleTap = false
            var isScrolling = false

            init { activityReference = WeakReference(activity) }

            override fun doInBackground(vararg params: Long?) {
                Log.e(TAG, "doInBackground()")
                var loopTime = 0

                while (loopTime < 200) {
                    if (isDoubleTap || isScrolling)
                        break

                    loopTime++
                    Thread.sleep(1)
                }
                publishProgress(params[0], params[1])
                return
            }

            override fun onProgressUpdate(vararg values: Long?) {
                Log.e(TAG, "onProgressUpdate(${values[0]})")
                val activity = activityReference?.get() as ExoPlayerActivity

                if (isScrolling) { // Scrolling
                } else if (!isDoubleTap) { // SingleTap
                    activity.updateTopBottomBarVisible()

                } else { // DoubleTap
                    val type = values[0]!!.toInt()
                    val firstPosition = values[1]!!

//                    if (type == POS_LEFT || type == POS_RIGHT) {
//                        activity.getPlayer().stop()
//                        activity.updatePlayPauseButton()
//                    }
                    caller.startDoubleTapButtonAnimation(type, 10, firstPosition)
                }
            }

            override fun onPostExecute(result: Unit?) {
                Log.e(TAG, "onPostExecute()")
                super.onPostExecute(result)
                caller.setTaskNull()
            }
        }
    }
}