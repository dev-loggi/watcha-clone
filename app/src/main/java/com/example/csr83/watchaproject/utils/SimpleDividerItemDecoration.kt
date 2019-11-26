package com.example.csr83.watchaproject.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import com.example.csr83.watchaproject.R

class SimpleDividerItemDecoration(
    context: Context,
    private val marginleft: Int,
    private val marginRight: Int
) : RecyclerView.ItemDecoration() {

    private val mDivider: Drawable

    init {
        mDivider = context.resources.getDrawable(R.drawable.recycler_view_separator)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + marginleft
        val right = parent.width - (parent.paddingRight + marginRight)

        val childCount = parent.childCount
        for (i in 1 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider.intrinsicHeight

            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }
}