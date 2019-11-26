package com.example.csr83.watchaproject.presentation.evaluation

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.csr83.watchaproject.R
import com.example.csr83.watchaproject.data.remote.Movie
import com.example.csr83.watchaproject.utils.Constants

class CustomBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val TAG = javaClass.simpleName

    private var movie : Movie? = null

    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN)
                dismiss()
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        arguments?.let {
            movie = it.getSerializable(Constants.ARG_PARAM_MOVIE) as Movie
        }
        Log.d(TAG, "onCreateDialog()")
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        Log.d(TAG, "setupDialog()")

        val contentView = View.inflate(context, R.layout.evaluation_bottom_sheet_dialog, null)
        dialog.setContentView(contentView)

        val layoutParams = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams

        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }

        val ivMovie = contentView.findViewById<ImageView>(R.id.iv_movie)
        val tvMovieTitle = contentView.findViewById<TextView>(R.id.tv_movie_title)
        val tvMovieSubtitle = contentView.findViewById<TextView>(R.id.tv_movie_subtitle)

        Glide.with(activity)
            .load(movie!!.image_tall)
            .centerCrop()
            .fitCenter()
            .into(ivMovie)
        tvMovieTitle.text = movie!!.title
        tvMovieSubtitle.text = "${movie!!.year} · 한국"
    }


}