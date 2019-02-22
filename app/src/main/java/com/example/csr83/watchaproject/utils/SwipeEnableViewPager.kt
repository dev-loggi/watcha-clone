package com.example.csr83.watchaproject.utils

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class SwipeEnableViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    override fun onTouchEvent(ev: MotionEvent?): Boolean { return false }
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean { return false }
}